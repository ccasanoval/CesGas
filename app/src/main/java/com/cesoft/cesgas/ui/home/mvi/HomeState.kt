package com.cesoft.cesgas.ui.home.mvi

import com.adidas.mvi.LoggableState
import com.cesoft.domain.AppError
import com.cesoft.domain.entity.AddressCounty
import com.cesoft.domain.entity.AddressProvince
import com.cesoft.domain.entity.AddressState
import com.cesoft.domain.entity.Filter
import com.cesoft.domain.entity.Product
import com.cesoft.domain.entity.ProductType
import com.cesoft.domain.entity.Station

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

sealed class HomeState: LoggableState {
    data object Loading: HomeState()
    data class Init(
        val stations: List<Station> = listOf(),
        val filter: Filter = Filter.Empty,
        val masters: Masters = Masters.Empty,
        val error: Throwable? = null
    ): HomeState()
}