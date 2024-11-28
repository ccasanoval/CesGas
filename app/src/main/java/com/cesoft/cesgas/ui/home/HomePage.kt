package com.cesoft.cesgas.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
        for (station in state.list) {
            item {
                Row {
                    Text(station.prices.G95.toMoneyFormat(Locale.current.platformLocale), modifier = Modifier.weight(.2f))
                    Text(station.title, modifier = Modifier.weight(.7f))
                    val a: Float? = null
                    Text(a.toMoneyFormat(Locale.current.platformLocale), modifier = Modifier.weight(.2f))
                }
            }
        }
    }

}