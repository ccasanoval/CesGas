package com.cesoft.cesgas.ui.map.mvi

import com.adidas.mvi.sideeffects.SideEffects
import com.adidas.mvi.transform.SideEffectTransform
import com.adidas.mvi.transform.ViewTransform
import com.cesoft.cesgas.ui.home.mvi.HomeState
import com.cesoft.domain.entity.Station

internal object MapTransform {

    data class GoInit(
        val stations: List<Station> = listOf(),
        val error: Throwable? = null,
    ): ViewTransform<MapState, MapSideEffect>() {
        override fun mutate(currentState: MapState): MapState {
            return MapState.Init(
                stations = stations,
                error = error
            )
        }
    }

    data class AddSideEffect(
        val sideEffect: MapSideEffect
    ): SideEffectTransform<MapState, MapSideEffect>() {
        override fun mutate(sideEffects: SideEffects<MapSideEffect>): SideEffects<MapSideEffect> {
            return sideEffects.add(sideEffect)
        }
    }
}