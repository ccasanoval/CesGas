package com.cesoft.cesgas.ui.home

import com.cesoft.cesgas.ui.home.HomeIntent
import com.cesoft.domain.entity.AddressCounty
import com.cesoft.domain.entity.AddressProvince
import com.cesoft.domain.entity.AddressState
import com.cesoft.domain.entity.Filter
import com.cesoft.domain.entity.ProductType
import com.cesoft.domain.entity.Station
import com.slack.circuit.runtime.CircuitUiState

data class Masters(
    val products: List<ProductType>,
    val states: List<AddressState>,
    val provinces: List<AddressProvince>,
    val counties: List<AddressCounty>,
) {
    companion object {
        val Empty = Masters(listOf(), listOf(), listOf(), listOf())
    }
}

sealed class HomeState/*(val eventSink: (HomeIntent) -> Unit)*/: CircuitUiState {
    data class Loading(val onEvent: (HomeIntent) -> Unit) : HomeState()
    data class Success(
        val stations: List<Station> = listOf(),
        val filter: Filter = Filter.Empty,
        val masters: Masters = Masters.Empty,
        //val wait: Boolean = false,
        val error: Throwable? = null,
        val onEvent: (HomeIntent) -> Unit = {}
    ): HomeState()
}
