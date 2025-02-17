package com.cesoft.cesgas.ui.map.mvi

sealed class MapSideEffect {
    data object Start: MapSideEffect()
    data object Close: MapSideEffect()
}
