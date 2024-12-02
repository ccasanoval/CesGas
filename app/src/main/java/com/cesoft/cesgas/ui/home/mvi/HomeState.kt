package com.cesoft.cesgas.ui.home.mvi

import com.adidas.mvi.LoggableState
import com.cesoft.domain.AppError
import com.cesoft.domain.entity.AddressCounty
import com.cesoft.domain.entity.AddressProvince
import com.cesoft.domain.entity.AddressState
import com.cesoft.domain.entity.Product
import com.cesoft.domain.entity.Station

data class Masters(
    val products: List<Product>,
    val states: List<AddressState>,
    val provinces: List<AddressProvince>,
    val counties: List<AddressCounty>,
) {
    companion object {
        val Empty = Masters(listOf(), listOf(), listOf(), listOf())
    }
}

data class Filter(
//    val states: List<Int>,
//    val provinces: List<Int>,
//    val counties: List<Int>,
//    val products: List<Int>,
    val state: Int,
    val province: Int,
    val county: Int,
    val product: Int,
) {
    companion object {
        val Empty = Filter(0,0,0,0)
    }
}

sealed class HomeState: LoggableState {
    data object Loading: HomeState()
    data class Init(
        val stations: List<Station> = listOf(),
        val filter: Filter = Filter.Empty,
        val masters: Masters = Masters.Empty,
        val error: AppError? = null
    ): HomeState()
}