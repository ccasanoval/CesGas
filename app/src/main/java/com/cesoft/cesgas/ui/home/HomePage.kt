package com.cesoft.cesgas.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.adidas.mvi.compose.MviScreen
import com.cesoft.cesgas.R
import com.cesoft.cesgas.ui.common.FilterCompo
import com.cesoft.cesgas.ui.common.FilterField
import com.cesoft.cesgas.ui.common.FilterOptions
import com.cesoft.cesgas.ui.common.FilterZipCodeCompo
import com.cesoft.cesgas.ui.common.LoadingCompo
import com.cesoft.cesgas.ui.common.toMoneyFormat
import com.cesoft.cesgas.ui.home.mvi.HomeIntent
import com.cesoft.cesgas.ui.home.mvi.HomeState
import com.cesoft.cesgas.ui.message
import com.cesoft.cesgas.ui.theme.FontMin
import com.cesoft.cesgas.ui.theme.SepMax
import com.cesoft.cesgas.ui.theme.SepMed
import com.cesoft.cesgas.ui.theme.SepMin
import com.cesoft.domain.AppError
import com.cesoft.domain.entity.Location
import com.cesoft.domain.entity.Prices
import com.cesoft.domain.entity.ProductType
import com.cesoft.domain.entity.Station

private val TitleHeight = 50.dp

@Composable
fun HomePage(
    navController: NavController,
    viewModel: HomeViewModel,
) {
    val context = LocalContext.current
    MviScreen(
        state = viewModel.state,
        onSideEffect = { sideEffect ->
            viewModel.consumeSideEffect(
                sideEffect = sideEffect,
                navController = navController,
                context = context
            )
        },
        onBackPressed = {
            viewModel.execute(HomeIntent.Close)
        },
    ) { state: HomeState ->
        when(state) {
            is HomeState.Loading -> {
                viewModel.execute(HomeIntent.Load)
                LoadingCompo()
            }
            is HomeState.Init -> {
                Init(state = state, reduce = viewModel::execute)
            }
        }
    }
}

@Composable
private fun Init(
    state: HomeState.Init,
    reduce: (HomeIntent) -> Unit
) {
    Column {
        if(state.wait) LoadingCompo(background = false)

        val isErrorVisible = remember { mutableStateOf(true) }
        LaunchedEffect(state.error) { isErrorVisible.value = true }
        if(state.error != null && isErrorVisible.value) {
            HeaderError(state.error.message(LocalContext.current), isErrorVisible)
        }
        else {
            HeaderTitle()
        }

        HeaderFilter(state, reduce)
        StationList(state, reduce)
    }
}

@Composable
private fun HeaderError(
    text: String,
    isErrorVisible: MutableState<Boolean>,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(MaterialTheme.colorScheme.inverseSurface)
            .fillMaxWidth()
            .height(TitleHeight)
            .padding(SepMed)
    ) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.inversePrimary,
            modifier = Modifier
                .padding(start = SepMax)
                .weight(.5f)
        )
        IconButton(onClick = { isErrorVisible.value = false }) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = stringResource(R.string.close),
                tint = MaterialTheme.colorScheme.inversePrimary
            )
        }
    }
}

@Composable
private fun HeaderTitle() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(TitleHeight)
            .padding(SepMed)
    ) {
        Icon(
            painter = painterResource(R.mipmap.ic_launcher_round),
            contentDescription = stringResource(R.string.app_name),
            tint = Color.Unspecified,
            modifier = Modifier.size(32.dp)
        )
        Text(
            text = stringResource(R.string.app_name),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = SepMed)
        )
    }
}

@Composable
private fun HeaderFilter(
    state: HomeState.Init,
    reduce: (HomeIntent) -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }
    Row {
        Button(onClick = { isVisible = !isVisible }, modifier = Modifier.padding(SepMed)) {
            Text(text = stringResource(if (isVisible) R.string.hide_filter else R.string.show_filter))
        }
        Spacer(modifier = Modifier.weight(.5f))
        Button(onClick = { reduce(HomeIntent.GoMap()) }, modifier = Modifier.padding(SepMed)) {
            Text(text = stringResource(R.string.map))
        }
    }
    if(isVisible) {
        val isProductVisible = remember { mutableStateOf(false) }
        val isStateVisible = remember { mutableStateOf(false) }
        val isProvinceVisible = remember { mutableStateOf(false) }
        val isCountyVisible = remember { mutableStateOf(false) }
        val isZipCodeVisible = remember { mutableStateOf(false) }

        val products = mutableListOf<FilterField>()
        for(pt in state.masters.products) {
            val selected = pt == state.filter.productType
            val favorite = pt == ProductType.G95//TODO: Delete when prefs in use-------------------------------
            products.add(FilterField(pt.ordinal, pt.name, selected, favorite))
        }
        val states = mutableListOf<FilterField>()
        for(s in state.masters.states) {
            val selected = s.id == state.filter.state
            val favorite = s.id == 13 || s.id == 10//TODO: Delete when prefs in use
            states.add(FilterField(s.id, s.name, selected, favorite))
        }
        val provinces = mutableListOf<FilterField>()
        for(p in state.masters.provinces) {
            val selected = p.id == state.filter.province
            val favorite = p.id == 28 || p.id == 46//TODO: Delete when prefs in use
            provinces.add(FilterField(p.id, p.name, selected, favorite))
        }
        val counties = mutableListOf<FilterField>()
        for(c in state.masters.counties) {
            val selected = c.id == state.filter.county
            val favorite = c.id == 4418 || c.id == 4326 || c.id == 4402 || c.id == 4354
                    || c.id == 7183 || c.id == 4277 //TODO: Delete when prefs in use
            counties.add(FilterField(c.id, c.name, selected, favorite))
        }
        Column {
            /// PRODUCT FILTER
            FilterCompo(
                stringResource(R.string.product),
                isProductVisible,
                FilterOptions(products),
                unique = true
            ) {
                reduce(HomeIntent.ChangeProduct(it))
            }
            /// STATE FILTER
            FilterCompo(
                stringResource(R.string.state),
                isStateVisible,
                FilterOptions(states),
                unique = true
            ) {
                reduce(HomeIntent.ChangeAddressState(it))
            }
            if(state.filter.state != null) {
                /// PROVINCE FILTER
                FilterCompo(
                    stringResource(R.string.province),
                    isProvinceVisible,
                    FilterOptions(provinces),
                    unique = true
                ) {
                    reduce(HomeIntent.ChangeAddressProvince(it))
                }
                /// ZIP CODE FILTER
                FilterZipCodeCompo(isZipCodeVisible, state.filter.zipCode) {
                    isZipCodeVisible.value = false
                    reduce(HomeIntent.ChangeAddressZipCode(it))
                }
                /// COUNTY FILTER
                if(state.filter.province != null) {
                    android.util.Log.e("AAA", "----------------- ${state.filter.province} / ${state.filter.county} / ${counties.size}")
                    for(c in counties)
                        android.util.Log.e("AAA", "----------------- $c")
                    FilterCompo(
                        stringResource(R.string.county),
                        isCountyVisible,
                        FilterOptions(counties),
                        unique = true
                    ) {
                        reduce(HomeIntent.ChangeAddressCounty(it))
                    }
                }
            }
        }
    }
}

@Composable
private fun StationList(
    state: HomeState.Init,
    reduce: (HomeIntent) -> Unit
) {
    LazyColumn {
        for (station in state.stations) {
            item {
                Item(
                    modifier = Modifier.padding(horizontal = SepMed, vertical = SepMin),
                    station = station,
                    reduce = reduce
                )
            }
        }
    }
}

@Composable
private fun Item(
    modifier: Modifier,
    station: Station,
    reduce: (HomeIntent) -> Unit,
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = modifier.weight(.1f)) {
            Row {
                Text(
                    text = station.prices.G95.toMoneyFormat(Locale.current.platformLocale),
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = station.title,
                    modifier = Modifier.padding(horizontal = SepMed)
                )
            }
            Row {
                Text(
                    text = "${station.county}, ${station.city} (${station.zipCode}), ${station.address}",
                    fontSize = FontMin,
                    modifier = Modifier.padding(vertical = SepMed),
                )
            }
        }
        // Map Icon
        IconButton(onClick = { reduce(HomeIntent.GoMap(station)) }) {
            Icon(Icons.Default.Place, null)
        }
    }
    HorizontalDivider()
}

//--------------------------------------------------------------------------------------------------
@Preview
@Composable
private fun HomePage_Preview() {
    val state = HomeState.Init(
        stations = listOf(
            Station(69, "28001", "Paper 123",
                "Sin City", "Capital County", "Bad bad state",
                Location(40.2, -3.1), "9.00 a 18.00", "Estacion final",
                Prices(1.6955f, 1.79f, 1.40f, 1.35f, 1.25f, null, null)
            ),
            Station(70, "28002", "Peach Trees 123",
                "MegaCity II", "Capital County", "Bad bad state",
                Location(40.0, -3.2), "9.00 a 18.00", "Polaris",
                Prices(1.895f, 1.79f, 1.40f, 1.35f, 1.25f, null, null)
            ),
            Station(71, "28003", "Paper 123",
                "Freetown", "Capital County", "Bad bad state",
                Location(40.4, -3.15), "9.00 a 18.00", "Mark IV",
                Prices(1.590f, 1.79f, 1.40f, 1.35f, 1.25f, null, null)
            ),
        ),
        error = AppError.NotFound,
    )
    Surface {
        Init(state) { }
    }
}