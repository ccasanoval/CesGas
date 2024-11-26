package com.cesoft.cesgas.ui.home.mvi

import com.adidas.mvi.sideeffects.SideEffects
import com.adidas.mvi.transform.SideEffectTransform
import com.adidas.mvi.transform.ViewTransform
import kotlinx.coroutines.flow.StateFlow


internal object HomeTransform {

    data object GoLoading: ViewTransform<HomeState, HomeSideEffect>() {
        override fun mutate(currentState: HomeState): HomeState {
            return HomeState.Loading
        }
    }

    data class GoInit(
        val a: Int = 0
        //val trackFlow: StateFlow<TrackDto?>,
        //val error: AppError?,
    ): ViewTransform<HomeState, HomeSideEffect>() {
        override fun mutate(currentState: HomeState): HomeState {
            return HomeState.Init()
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