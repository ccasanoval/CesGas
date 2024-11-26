package com.cesoft.cesgas.ui.home.mvi

import com.adidas.mvi.LoggableState
import kotlinx.coroutines.flow.StateFlow

sealed class HomeState: LoggableState {
    data object Loading: HomeState()
    data class Init(
        val a: Int = 0
        //val trackFlow: StateFlow<TrackDto?>,
        //val error: AppError? = null
    ): HomeState()
}