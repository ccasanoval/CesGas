package com.cesoft.cesgas.ui.map

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.cesoft.domain.AppError
import com.cesoft.domain.entity.Filter
import com.cesoft.domain.entity.Station
import com.cesoft.domain.usecase.FilterStationsUC
import com.cesoft.domain.usecase.GetCurrentStationUC
import com.cesoft.domain.usecase.GetFilterUC
import com.slack.circuit.retained.rememberRetained
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Everything the screen shows once loaded */
private data class MapData(
    val stations: List<Station> = listOf(),
    val filter: Filter = Filter.Empty,
    val error: Throwable? = null,
)

class MapPresenter @AssistedInject constructor(
    @Assisted private val navigator: Navigator,
    private val getCurrentStation: GetCurrentStationUC,
    private val getFilter: GetFilterUC,
    private val filterStations: FilterStationsUC,
) : Presenter<MapState> {

    @AssistedFactory
    interface Factory {
        fun create(navigator: Navigator): MapPresenter
    }

    @Composable
    override fun present(): MapState {
        // Retained: survives configuration changes, so rotating the device doesn't call the API again
        var isLoading by rememberRetained { mutableStateOf(true) }
        var data by rememberRetained { mutableStateOf(MapData()) }

        // Setting isLoading = true is what triggers a (re)load
        if(isLoading) {
            LaunchedEffect(Unit) {
                data = withContext(Dispatchers.IO) { load() }
                isLoading = false
            }
        }

        val eventSink: (MapIntent) -> Unit = { event ->
            when (event) {
                is MapIntent.Close -> navigator.pop()
                is MapIntent.Load -> isLoading = true
            }
        }

        return if(isLoading) {
            MapState.Loading(onEvent = eventSink)
        } else {
            MapState.Success(
                stations = data.stations,
                filter = data.filter,
                error = data.error,
                onEvent = eventSink
            )
        }
    }

    /** The station selected in the list, or every station matching the filter if there is none */
    private suspend fun load(): MapData {
        val filter = getFilter().getOrNull() ?: Filter()
        val current = getCurrentStation().getOrNull()
        // A station without location can't be shown, so it counts as "no station selected"
        if(current != null && (current.location.latitude != 0.0 || current.location.longitude != 0.0)) {
            return MapData(stations = listOf(current), filter = filter)
        }
        return filterStations(filter).fold(
            onSuccess = { stations ->
                val error = if(stations.isEmpty()) AppError.NotFound() else null
                MapData(stations = stations, filter = filter, error = error)
            },
            onFailure = { MapData(filter = filter, error = it) }
        )
    }
}
