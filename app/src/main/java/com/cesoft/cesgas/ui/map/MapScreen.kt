package com.cesoft.cesgas.ui.map

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.cesoft.cesgas.R
import com.cesoft.cesgas.ui.common.LoadingCompo
import com.cesoft.cesgas.ui.common.MapCompo
import com.cesoft.cesgas.ui.common.rememberMapCompo
import com.slack.circuit.runtime.screen.Screen
import kotlinx.parcelize.Parcelize

@Parcelize
data object MapScreen : Screen

@Composable
fun MapScreen(state: MapState) {
    when(state) {
        is MapState.Loading -> {
            LoadingCompo()
            state.onEvent(MapIntent.Load)
        }
        is MapState.Success -> {
            val context = LocalContext.current
            val mapView = rememberMapCompo(context)
            if(state.stations.isEmpty()) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(text = stringResource(R.string.error_not_found))
                }
            }
            else {
                MapCompo(
                    context = context,
                    mapView = mapView,
                    stations = state.stations,
                    modifier = Modifier.fillMaxSize(),
                    onEvent = { state.onEvent(MapIntent.Close) },
                    productType = state.filter.productType
                )
            }
        }
    }
}