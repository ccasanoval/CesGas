package com.cesoft.cesgas.ui.common

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.graphics.createBitmap
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.cesoft.cesgas.R
import com.cesoft.cesgas.ui.theme.SepMax
import com.cesoft.domain.entity.ProductType
import com.cesoft.domain.entity.Station
import org.osmdroid.config.Configuration
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

private const val SINGLE_STATION_ZOOM = 17.0
private const val ZOOM_BORDER_PX = 100
private val MY_LOCATION_COLOR = Color(0, 100, 220)
private val LOCATION_PERMISSIONS = arrayOf(
    Manifest.permission.ACCESS_FINE_LOCATION,
    Manifest.permission.ACCESS_COARSE_LOCATION,
)

@Composable
fun MapCompo(
    context : Context,
    mapView: MapView,
    modifier: Modifier = Modifier,
    onEvent: () -> Unit,
    stations: List<Station>,
    productType: ProductType?
) {
    val mapStations = remember(stations, productType) { stationsForMap(stations, productType) }
    // With a single station it is shown right away, otherwise the one whose marker is clicked
    var selectedStation by remember(stations) { mutableStateOf(stations.singleOrNull()) }
    val locationOverlay = rememberLocationOverlay(context, mapView)

    // Only when the stations change, so recompositions (e.g. selecting one) don't reset the user's zoom
    LaunchedEffect(mapView, mapStations) {
        showStations(context, mapView, mapStations, locationOverlay) { selectedStation = it }
    }

    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onEvent) {
                Icon(
                    painter = painterResource(R.mipmap.arrow_back),
                    contentDescription = stringResource(R.string.back),
                    modifier = Modifier.size(24.dp)
                )
            }
            Text(
                text = stringResource(R.string.map),
                fontWeight = FontWeight.Bold
            )
            selectedStation?.let {
                val type = mapProductType(productType)
                Column(modifier = Modifier.padding(SepMax)) {
                    Text(it.title, fontWeight = FontWeight.Bold)
                    Text(type.name + " : " + it.prices.of(type).toMoneyFormat(Locale.current.platformLocale))
                    Text(it.hours)
                }
            }
        }
        //Without Scaffold the osmdroid map draws outside its AndroidView limits
        Scaffold(modifier = modifier) { innerPadding ->
            AndroidView(
                factory = { mapView },
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

/** Replaces the station markers (keeping the location overlay) and zooms to show them */
@SuppressLint("UseCompatLoadingForDrawables")
private fun showStations(
    context: Context,
    view: MapView,
    stations: List<Station>,
    locationOverlay: MyLocationNewOverlay,
    onClick: (Station) -> Unit,
) {
    view.overlays.removeAll { it is Marker }

    val minPrice = stations.minOfOrNull { it.workingPrice } ?: 0f
    val maxPrice = stations.maxOfOrNull { it.workingPrice } ?: 0f
    //https://fonts.google.com/icons
    stations.forEach { s ->
        // mutate() so each marker gets its own tint instead of sharing the drawable state
        val icon = context.getDrawable(R.mipmap.star_48dp)?.mutate()
        val color = when(priceTier(s.workingPrice, minPrice, maxPrice)) {
            PriceTier.CHEAP -> Color(0, 200, 0)
            PriceTier.MEDIUM -> Color(250, 150, 0)
            PriceTier.EXPENSIVE -> Color(200, 0, 0)
        }
        icon?.setTint(color.toArgb())
        addMarker(
            mapView = view,
            geoPoint = GeoPoint(s.location.latitude, s.location.longitude),
            icon = icon,
            title = s.title,
            snippet = context.getString(R.string.price) + s.workingPrice,
            onClick = { onClick(s) }
        )
    }

    val points = stations.map { GeoPoint(it.location.latitude, it.location.longitude) }
    val zoom = {
        when(points.size) {
            // Nothing to show: center on the user as soon as there is a location fix
            0 -> locationOverlay.runOnFirstFix {
                view.post { locationOverlay.myLocation?.let { view.controller.animateTo(it) } }
            }
            1 -> {
                view.controller.setZoom(SINGLE_STATION_ZOOM)
                view.controller.setCenter(points.first())
            }
            else -> view.zoomToBoundingBox(BoundingBox.fromGeoPointsSafe(points), false, ZOOM_BORDER_PX)
        }
        view.invalidate()
    }
    // The first layout listener never fires if the map was already laid out
    if(view.isLayoutOccurred) zoom()
    else view.addOnFirstLayoutListener { _, _, _, _, _ -> zoom() }
}

/**
 * A single "my location" overlay for the map's whole life. It asks for the location permission
 * and only listens to the GPS while it is granted and the screen is on (see [rememberMapCompo]).
 */
@Composable
private fun rememberLocationOverlay(context: Context, mapView: MapView): MyLocationNewOverlay {
    val overlay = remember(mapView) {
        MyLocationNewOverlay(GpsMyLocationProvider(context), mapView).also { overlay ->
            // osmdroid's default direction arrow is white: almost invisible over the light OSM tiles
            ContextCompat.getDrawable(context, org.osmdroid.library.R.drawable.round_navigation_white_48)
                ?.tintedBitmap(MY_LOCATION_COLOR)
                ?.let { overlay.setDirectionIcon(it) }
            mapView.overlays.add(overlay)
        }
    }
    var isGranted by remember { mutableStateOf(hasLocationPermission(context)) }
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result -> isGranted = result.values.any { it } }
    LaunchedEffect(Unit) {
        if(!isGranted) launcher.launch(LOCATION_PERMISSIONS)
    }
    DisposableEffect(overlay, isGranted) {
        if(isGranted) overlay.enableMyLocation()
        onDispose { overlay.disableMyLocation() }
    }
    return overlay
}

/** Draws the drawable with a tint (toBitmap() would return a BitmapDrawable's bitmap untinted) */
private fun Drawable.tintedBitmap(color: Color): Bitmap {
    val bitmap = createBitmap(intrinsicWidth, intrinsicHeight)
    mutate().apply {
        setTint(color.toArgb())
        setBounds(0, 0, intrinsicWidth, intrinsicHeight)
        draw(Canvas(bitmap))
    }
    return bitmap
}

private fun hasLocationPermission(context: Context) = LOCATION_PERMISSIONS.any {
    ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
}

/** The MapView follows the screen lifecycle: tiles and GPS are paused while it is not visible */
@Composable
fun rememberMapCompo(context : Context): MapView {
    val mapView = remember {
        val prefs = context.getSharedPreferences(context.packageName + "OSM", Context.MODE_PRIVATE)
        Configuration.getInstance().load(context, prefs)
        MapView(context).also { initMap(it) }
    }
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner, mapView) {
        val observer = LifecycleEventObserver { _, event ->
            when(event) {
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            mapView.onDetach()
        }
    }
    return mapView
}

private fun initMap(mapView: MapView) {
    mapView.apply {
        isHorizontalMapRepetitionEnabled = false
        isVerticalMapRepetitionEnabled = false
        setMultiTouchControls(true)
        val tileSystem = MapView.getTileSystem()
        setScrollableAreaLimitDouble(
            BoundingBox(
                tileSystem.maxLatitude, tileSystem.maxLongitude, // top-left
                tileSystem.minLatitude, tileSystem.minLongitude  // bottom-right
            )
        )
        minZoomLevel = 5.0
        maxZoomLevel = 24.0
        controller.setZoom(20.0)
    }
}

fun addMarker(
    mapView : MapView,
    geoPoint: GeoPoint,
    icon: Drawable?=null,
    title: String?=null,
    snippet : String?=null,
    onClick: (() -> Unit)? = null
): Marker {
    val marker = Marker(mapView)
    marker.position = geoPoint
    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
    marker.infoWindow = null
    marker.setOnMarkerClickListener { _, _ ->
        //marker.showInfoWindow()
        onClick?.let {
            it()
            true
        } ?: run { false }
    }
    //marker.setInfoWindow(MarkerInfoWindow())

    title?.let { marker.title = it }
    snippet?.let { marker.snippet = it }
    icon?.let { marker.icon = it }

    mapView.overlays.add(marker)
    return marker
}

/*
fun addMyLocation(context: Context, mapView: MapView) {
    val locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(context), mapView)
    locationOverlay.enableMyLocation()
    //locationOverlay.enableFollowLocation()--> Doesn't let you move manually
    //locationOverlay.runOnFirstFix { mapView.setExpectedCenter(locationOverlay.myLocation) }
    mapView.overlays.add(locationOverlay)
    //
    val compassOverlay = CompassOverlay(context, InternalCompassOrientationProvider(context), mapView)
    compassOverlay.enableCompass()
    mapView.overlays.add(compassOverlay)
}

fun createPolyline(
    mapView: MapView,
    points: List<GeoPoint>,
    color: Color?
): Polyline {
    val polyline = Polyline(mapView)
    color?.let { polyline.color = color.toArgb() }
    polyline.setPoints(points)
    polyline.infoWindow = null
    return polyline
}

fun createPolyline(mapView: MapView, points: List<GeoPoint>): Polyline {
    val polyline = Polyline(mapView)
    //polyline.color = Green.toArgb()
    //for(p in points) polyline.addPoint(p)
    polyline.setPoints(points)
    polyline.infoWindow = null
    mapView.overlayManager.add(polyline)
    return polyline
}*/
//
//fun drawPath(mapView: MapView, points: List<GeoPoint>): Polyline {
//    val paint: Paint = Paint()
//    paint.color = Green.toArgb()
//    paint.alpha = 90
//    paint.style = Paint.Style.STROKE
//    paint.strokeWidth = 10f
//    val myPath: PathOverlay = PathOverlay(Color.RED, this)
//    myPath.setPaint(paint)
//    for(p in points) {
//        myPath.addPoint(p)
//    }
//    mapView.overlays.add(myPath)
//}

//fun createPolyline(mapView: MapView, startPoint: GeoPoint, endPoint: GeoPoint): Polyline {
//    val polyline = Polyline(mapView)
//    polyline.color = Green.toArgb()
//    polyline.addPoint(startPoint)
//    polyline.addPoint(endPoint)
//    polyline.infoWindow = null
//    return polyline
//}

//
//fun mapEventsOverlay(
//    view : MapView,
//    onTap : (GeoPoint)->Unit
//): MapEventsOverlay {
//    return MapEventsOverlay(object : MapEventsReceiver {
//        override fun singleTapConfirmedHelper(geoPoint: GeoPoint?): Boolean {
//            // Handle the map click event
//            if (geoPoint != null) {
//                onTap(geoPoint)
//                view.invalidate() // Refresh the map view
//            }
//            return true
//        }
//
//        override fun longPressHelper(p: GeoPoint?): Boolean {
//            // Handle long press event if needed
//            return false
//        }
//    })
//}
