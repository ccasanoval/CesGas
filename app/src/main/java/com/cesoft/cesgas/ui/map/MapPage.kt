package com.cesoft.cesgas.ui.map

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.adidas.mvi.compose.MviScreen
import com.cesoft.cesgas.ui.common.LoadingCompo
import com.cesoft.cesgas.ui.common.MapCompo
import com.cesoft.cesgas.ui.common.rememberMapCompo
import com.cesoft.cesgas.ui.map.mvi.MapIntent
import com.cesoft.cesgas.ui.map.mvi.MapState

@Composable
fun MapPage(
    navController: NavController,
    viewModel: MapViewModel,
) {
    MviScreen(
        state = viewModel.state,
        onSideEffect = { sideEffect ->
            viewModel.consumeSideEffect(
                sideEffect = sideEffect,
                navController = navController
            )
        },
        onBackPressed = {
            viewModel.execute(MapIntent.Close)
        },
    ) { state: MapState ->
        when(state) {
            is MapState.Loading -> {
                viewModel.execute(MapIntent.Load)
                LoadingCompo()
            }
            is MapState.Init -> {
                val context = LocalContext.current
                val mapView = rememberMapCompo(context)
                Text(text = state.stations.first().title)
                MapCompo(
                    context = context,
                    mapView = mapView,
                    stations = state.stations,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}