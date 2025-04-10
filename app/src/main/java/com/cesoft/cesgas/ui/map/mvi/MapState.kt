package com.cesoft.cesgas.ui.map.mvi

import com.adidas.mvi.LoggableState
import com.cesoft.domain.entity.Filter
import com.cesoft.domain.entity.Station

sealed class MapState: LoggableState {
    data object Loading : MapState()
    data class Init(
        val stations: List<Station> = listOf(),
        val filter: Filter,
        val error: Throwable? = null
    ): MapState()
}