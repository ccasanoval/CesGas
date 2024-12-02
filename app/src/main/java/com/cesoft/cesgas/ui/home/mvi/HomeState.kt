package com.cesoft.cesgas.ui.home.mvi

import com.adidas.mvi.LoggableState
import com.cesoft.domain.AppError
import com.cesoft.domain.entity.Product
import com.cesoft.domain.entity.Station

data class Masters(
    val products: List<Product>,
    val states: List<State>
)

sealed class HomeState: LoggableState {
    data object Loading: HomeState()
    data class Init(
        val stations: List<Station>,
        val products: List<Product>,
        val error: AppError? = null
    ): HomeState()
}