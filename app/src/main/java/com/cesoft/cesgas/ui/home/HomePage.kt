package com.cesoft.cesgas.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.adidas.mvi.compose.MviScreen
import com.cesoft.cesgas.ui.common.LoadingCompo
import com.cesoft.cesgas.ui.common.toMoneyFormat
import com.cesoft.cesgas.ui.home.mvi.HomeIntent
import com.cesoft.cesgas.ui.home.mvi.HomeState
import com.cesoft.cesgas.ui.theme.FontMin
import com.cesoft.cesgas.ui.theme.SepMax
import com.cesoft.cesgas.ui.theme.SepMed
import com.cesoft.domain.AppError
import com.cesoft.domain.entity.Location
import com.cesoft.domain.entity.Prices
import com.cesoft.domain.entity.Station

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
        Header()
        StationList(state, reduce)
    }
}

@Composable
private fun Header() {
    Text("Filtros para la busqueda", modifier = Modifier.padding(SepMax))
    
}

@Composable
private fun StationList(
    state: HomeState.Init,
    reduce: (HomeIntent) -> Unit
) {
    Text("Station list", modifier = Modifier.padding(SepMax))

    LazyColumn {
        for (station in state.list) {
            item {
                Item(
                    modifier = Modifier.padding(horizontal = SepMed),
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
    Column(modifier = modifier) {
        Row {
            Text(
                text = station.prices.G95.toMoneyFormat(Locale.current.platformLocale),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(.15f),
            )
            Text(
                text = station.title,
                modifier = Modifier.weight(.8f),
            )
        }
        Row {
            Box(modifier = Modifier.weight(.15f))
            Text(
                text = "${station.county}, ${station.city} (${station.zipCode}), ${station.address}",
                fontSize = FontMin,
                modifier = Modifier.weight(.8f),
            )
        }
        HorizontalDivider()
    }
}

//--------------------------------------------------------------------------------------------------
@Preview
@Composable
private fun HomePage_Preview() {
    val state = HomeState.Init(
        list = listOf(
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
                Prices(1.593f, 1.79f, 1.40f, 1.35f, 1.25f, null, null)
            ),
        ),
        error = AppError.NotFound,
    )
    Surface {
        Init(state) { }
    }
}