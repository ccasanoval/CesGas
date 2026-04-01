package com.cesoft.cesgas.ui.map

import com.cesoft.cesgas.ui.map.MapIntent
import com.cesoft.domain.entity.Filter
import com.cesoft.domain.entity.Station
import com.slack.circuit.runtime.CircuitUiState

sealed class MapState: CircuitUiState {
    data class Loading(val onEvent: (MapIntent) -> Unit) : MapState()
    data class Success(
        val stations: List<Station> = listOf(),
        val filter: Filter,
        val error: Throwable? = null,
        val onEvent: (MapIntent) -> Unit = {}
    ): MapState()
}