package com.cesoft.cesgas.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.intl.Locale
import androidx.navigation.NavController
import com.adidas.mvi.compose.MviScreen
import com.cesoft.cesgas.ui.common.LoadingCompo
import com.cesoft.cesgas.ui.common.toMoneyFormat
import com.cesoft.cesgas.ui.home.mvi.HomeIntent
import com.cesoft.cesgas.ui.home.mvi.HomeState
import com.cesoft.cesgas.ui.theme.SepMax
import com.cesoft.cesgas.ui.theme.SepMin
import com.cesoft.domain.entity.Station
import kotlin.reflect.KFunction1

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
    reduce: KFunction1<HomeIntent, Unit>
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
        var i = 0
        for (station in state.list) {
            item {
                Item(
                    modifier = Modifier,
                    even = i++ % 2 == 0,
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
    even: Boolean,
    station: Station,
    reduce: (HomeIntent) -> Unit,
) {
    Row(
        modifier = if(even) {
                modifier.then(Modifier.background(MaterialTheme.colorScheme.inverseSurface))
            }
            else {
                modifier.then(Modifier.background(MaterialTheme.colorScheme.surface))
            }
    ) {
        Text(
            text = station.prices.G95.toMoneyFormat(Locale.current.platformLocale),
            modifier = Modifier.weight(.2f),
            color =
                if(even) MaterialTheme.colorScheme.inverseOnSurface
                else MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = station.title + " ("+station.id+")",
            modifier = Modifier.weight(.7f),
            color =
                if(even) MaterialTheme.colorScheme.inverseOnSurface
                else MaterialTheme.colorScheme.onSurface,
        )
    }
}