package com.cesoft.cesgas.ui.common

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import com.cesoft.cesgas.R
import com.cesoft.domain.entity.ProductType
import com.cesoft.domain.entity.Station
import org.osmdroid.config.Configuration
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

@SuppressLint("UseCompatLoadingForDrawables")
@Composable
fun MapCompo(
    context : Context,
    mapView: MapView,
    modifier: Modifier = Modifier,
    stations: List<Station>,
    productType: ProductType?,
    doZoom: Boolean = false
) {
    //Without Scaffold the osmdroid map draws outside its AndroidView limits
    val selectedStation = remember { mutableStateOf<Station?>(null) }

    Column {
        selectedStation.value?.let {
            Text(" ************ ")
            Text("${it.title}")
            Text(stringResource(R.string.g95) + " : " + it.prices.G95 + "€")
            Text(stringResource(R.string.goa) + " : " + it.prices.GOA + "€")
        }
        Scaffold(modifier = modifier) { innerPadding ->
            AndroidView(
                factory = { mapView },
                modifier = Modifier.padding(innerPadding)
            ) { view ->
                view.overlays.removeAll { true }

                val locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(context), mapView)
                locationOverlay.enableMyLocation()
                view.overlays.add(locationOverlay)
                view.controller.setCenter(locationOverlay.myLocation)

                var ss =
                    stations.filter { it.location.latitude != 0.0 && it.location.latitude != 0.0 }
                ss = when (productType) {
                    ProductType.G95 -> ss.map { it.copy(workingPrice = it.prices.G95 ?: 0f) }
                    ProductType.G98 -> ss.map { it.copy(workingPrice = it.prices.G98 ?: 0f) }
                    ProductType.GOA -> ss.map { it.copy(workingPrice = it.prices.GOA ?: 0f) }
                    ProductType.GOB -> ss.map { it.copy(workingPrice = it.prices.GOB ?: 0f) }
                    ProductType.GOC -> ss.map { it.copy(workingPrice = it.prices.GOC ?: 0f) }
                    ProductType.GLP -> ss.map { it.copy(workingPrice = it.prices.GLP ?: 0f) }
                    ProductType.GOAP -> ss.map { it.copy(workingPrice = it.prices.GOAP ?: 0f) }
                    else -> ss.map { it.copy(workingPrice = it.prices.G95 ?: 0f) }
                }
                ss = ss.filter { it.workingPrice > 0 }.sortedBy { it.workingPrice }
                val maxStations = 10
                if (ss.size > maxStations) ss = ss.subList(0, maxStations)

                val maxPrice = ss.maxOf { it.workingPrice }
                val minPrice = ss.minOf { it.workingPrice }
                val threePart = (maxPrice - minPrice) / 3

                ss.map { s ->
                    val icon =
                        if (s.workingPrice < minPrice + threePart)
                            context.getDrawable(android.R.drawable.btn_star_big_on)
                        else if (s.workingPrice > maxPrice - threePart)
                            context.getDrawable(android.R.drawable.ic_lock_lock)
                        else
                            context.getDrawable(android.R.drawable.ic_lock_idle_lock)
                    val snippet = context.getString(R.string.price) + s.workingPrice
                    addMarker(
                        mapView = view,
                        geoPoint = GeoPoint(s.location.latitude, s.location.longitude),
                        icon = icon,
                        title = s.title,
                        snippet = snippet,
                        onClick = {
                            selectedStation.value = s
android.util.Log.e("AAA", "on marker click---------- ${s.title} : ${s.hours} : ${s.prices.G95}")
                        }
                    )
                }

                view.addOnFirstLayoutListener { _, _, _, _, _ ->//v, left, top, right, bottom ->
                    //if (points.isEmpty()) {
                    //view.controller.setCenter(locationOverlay.myLocation)
                    //location?.let { view.controller.setCenter(GeoPoint(it.latitude, it.longitude)) }
                    //} else {
                    val gps = ss.map { GeoPoint(it.location.latitude, it.location.longitude) }
                    view.zoomToBoundingBox(BoundingBox.fromGeoPointsSafe(gps), false)
                    //view.controller.setCenter(locationOverlay.myLocation)
                    //}
                    view.invalidate()
                }
            }
        }
    }
}

@Composable
fun rememberMapCompo(context : Context): MapView {
    val pack = context.packageName
    val prefs = context.getSharedPreferences(pack+"OSM", Context.MODE_PRIVATE)
    Configuration.getInstance().load(context, prefs)
    val mapView = remember { MapView(context) }
    DisposableEffect(Unit) {
        onDispose {
            mapView.onDetach()
        }
    }
    return mapView.apply { initMap(this) }
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
