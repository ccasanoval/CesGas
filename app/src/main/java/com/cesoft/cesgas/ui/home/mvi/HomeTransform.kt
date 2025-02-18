package com.cesoft.cesgas.ui.home.mvi

import com.adidas.mvi.sideeffects.SideEffects
import com.adidas.mvi.transform.SideEffectTransform
import com.adidas.mvi.transform.ViewTransform
import com.cesoft.domain.entity.Filter
import com.cesoft.domain.entity.Station

internal object HomeTransform {

    data object Wait: ViewTransform<HomeState, HomeSideEffect>() {
        override fun mutate(currentState: HomeState): HomeState {
            if(currentState is HomeState.Init)
                return currentState.copy(wait = true)
            else
                return currentState
        }
    }

    data class GoInit(
        val stations: List<Station> = listOf(),
        val filter: Filter = Filter.Empty,
        val masters: Masters = Masters.Empty,
        val error: Throwable? = null,
    ): ViewTransform<HomeState, HomeSideEffect>() {
        override fun mutate(currentState: HomeState): HomeState {
            return HomeState.Init(
                stations = stations,
                masters = masters,
                filter = filter,
                error = error
            )
        }
    }

    data class AddSideEffect(
        val sideEffect: HomeSideEffect
    ): SideEffectTransform<HomeState, HomeSideEffect>() {
        override fun mutate(sideEffects: SideEffects<HomeSideEffect>): SideEffects<HomeSideEffect> {
            return sideEffects.add(sideEffect)
        }
    }
}