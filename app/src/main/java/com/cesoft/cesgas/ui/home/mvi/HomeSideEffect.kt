package com.cesoft.cesgas.ui.home.mvi

sealed class HomeSideEffect {
    data object Start: HomeSideEffect()
    data object Close: HomeSideEffect()
    data object GoMap: HomeSideEffect()
}
