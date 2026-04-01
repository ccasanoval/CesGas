package com.cesoft.cesgas.ui.map

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.cesoft.cesgas.ui.map.MapIntent
import com.cesoft.domain.entity.AddressCounty
import com.cesoft.domain.entity.AddressProvince
import com.cesoft.domain.entity.AddressState
import com.cesoft.domain.entity.Filter
import com.cesoft.domain.entity.ProductType
import com.cesoft.domain.entity.Station
import com.cesoft.domain.usecase.FilterStationsUC
import com.cesoft.domain.usecase.GetByCountyUC
import com.cesoft.domain.usecase.GetByProvinceUC
import com.cesoft.domain.usecase.GetByStateUC
import com.cesoft.domain.usecase.GetCurrentStationUC
import com.cesoft.domain.usecase.GetFilterUC
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MapPresenter @Inject constructor(
    private val getCurrentStation: GetCurrentStationUC,
    private val getFilter: GetFilterUC,
    private val filterStations: FilterStationsUC,
    private val getByState: GetByStateUC,
    private val getByProvince: GetByProvinceUC,
    private val getByCounty: GetByCountyUC,
    //private val navigator: Navigator
) : Presenter<MapState> {
    private var error: Throwable? = null
    private var stations = listOf<Station>()
    private var filter = Filter()

    var navigator: Navigator ?= null

    @Composable
    override fun present(): MapState {
        val error: Throwable? = null
        val coroutineScope = rememberCoroutineScope()
        var isLoading by remember { mutableStateOf(true) }

        val eventSink: (MapIntent) -> Unit = { event ->
            when (event) {
                is MapIntent.Close -> {
                    navigator?.pop()
                }
                is MapIntent.Load -> {
                    coroutineScope.launch(Dispatchers.IO) {
                        isLoading = true
                        executeLoad()
                        isLoading = false
                    }
                }
            }
        }

//        LaunchedEffect(Unit) {
//            lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
//                coroutineScope.launch(Dispatchers.IO) {
//                    isLoading = true
//                    state = fetch()
//                }
//            }
//        }

        return when {
            isLoading -> MapState.Loading(onEvent = eventSink)

            error != null -> MapState.Success(filter = filter, error = error, onEvent = eventSink)

            else -> {
                MapState.Success(
                    stations = stations,
                    filter = filter,
                    error = error,
                    onEvent = eventSink
                )
            }
        }
    }

    private suspend fun executeLoad() {
        stations = getCurrentStation().getOrNull()?.let { listOf(it) } ?: listOf()
        if(stations.isEmpty() || stations[0] == Station.Empty) {
            filter = getFilter().getOrNull() ?: Filter()
            stations = filterStations(filter)
        }
    }
}