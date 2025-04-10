package com.cesoft.cesgas.ui.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.adidas.mvi.MviHost
import com.adidas.mvi.Reducer
import com.adidas.mvi.State
import com.adidas.mvi.reducer.Reducer
import com.cesoft.cesgas.Page
import com.cesoft.cesgas.ui.map.mvi.MapIntent
import com.cesoft.cesgas.ui.map.mvi.MapSideEffect
import com.cesoft.cesgas.ui.map.mvi.MapState
import com.cesoft.cesgas.ui.map.mvi.MapTransform
import com.cesoft.domain.entity.Filter
import com.cesoft.domain.entity.Station
import com.cesoft.domain.usecase.FilterStationsUC
import com.cesoft.domain.usecase.GetByCountyUC
import com.cesoft.domain.usecase.GetByProvinceUC
import com.cesoft.domain.usecase.GetByStateUC
import com.cesoft.domain.usecase.GetCurrentStationUC
import com.cesoft.domain.usecase.GetFilterUC
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val getCurrentStation: GetCurrentStationUC,
    private val getFilter: GetFilterUC,
    private val filterStations: FilterStationsUC,
    private val getByState: GetByStateUC,
    private val getByProvince: GetByProvinceUC,
    private val getByCounty: GetByCountyUC,
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
        var filter: Filter = Filter.Empty
        var stations = getCurrentStation().getOrNull()?.let { listOf(it) } ?: listOf()
        if(stations.isEmpty() || stations[0] == Station.Empty) {
            filter = getFilter().getOrNull() ?: Filter()
            stations = filterStations(filter)
        }
        //android.util.Log.e(TAG, "fetch---------------------- ${stations.size} / $stations ///" + (stations[0] == Station.Empty))
        //android.util.Log.e(TAG, "fetch---------------------- ${stations[0].location}")
        return MapTransform.GoInit(stations, filter)
    }

    companion object {
        private const val TAG = "MapVM"
    }
}
