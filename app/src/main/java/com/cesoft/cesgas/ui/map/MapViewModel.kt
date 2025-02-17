package com.cesoft.cesgas.ui.map

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.adidas.mvi.IntentExecutor
import com.adidas.mvi.MviHost
import com.adidas.mvi.Reducer
import com.adidas.mvi.State
import com.adidas.mvi.reducer.Reducer
import com.cesoft.cesgas.Page
import com.cesoft.cesgas.ui.map.mvi.MapIntent
import com.cesoft.cesgas.ui.map.mvi.MapSideEffect
import com.cesoft.cesgas.ui.map.mvi.MapState
import com.cesoft.cesgas.ui.map.mvi.MapTransform
import com.cesoft.domain.usecase.GetCurrentStationUC
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val getCurrentStation: GetCurrentStationUC
): ViewModel(), MviHost<MapIntent, State<MapState, MapSideEffect>> {

    private val reducer: Reducer<MapIntent, State<MapState, MapSideEffect>> = Reducer(
        coroutineScope = viewModelScope,
        defaultDispatcher = Dispatchers.Default,
        initialInnerState = MapState.Loading,
        logger = null,
        intentExecutor = this::executeIntent
    )
    override val state = reducer.state
    override fun execute(intent: MapIntent) {
        reducer.executeIntent(intent)
    }
    private fun executeIntent(intent: MapIntent) =
        when(intent) {
            MapIntent.Close -> executeClose()
            MapIntent.Load -> executeLoad()
        }

    fun consumeSideEffect(
        sideEffect: MapSideEffect,
        navController: NavController
    ) {
        when(sideEffect) {
            MapSideEffect.Close -> {
                android.util.Log.e(TAG, "consumeSideEffect:Close----------------------------")
                navController.navigate(Page.Home.route)
            }
            MapSideEffect.Start -> {
                android.util.Log.e(TAG, "consumeSideEffect:Start----------------------------")
            }
        }
    }

    private fun executeClose() = flow {
        emit(MapTransform.AddSideEffect(MapSideEffect.Close))
    }

    private fun executeLoad() = flow {
        emit(fetch())
    }

    private suspend fun fetch(): MapTransform.GoInit {
        val stations = getCurrentStation().getOrNull()?.let { listOf(it) } ?: listOf()
        //TODO: Or filter, for multiple stations
        android.util.Log.e(TAG, "fetch---------------------- ${stations[0]}")
        android.util.Log.e(TAG, "fetch---------------------- ${stations[0].location}")
        return MapTransform.GoInit(stations)
    }

    companion object {
        private const val TAG = "MapVM"
    }
}
