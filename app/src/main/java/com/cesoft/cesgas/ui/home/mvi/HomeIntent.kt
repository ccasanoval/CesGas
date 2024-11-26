package com.cesoft.cesgas.ui.home.mvi

import com.adidas.mvi.Intent

sealed class HomeIntent: Intent {
    data object Close: HomeIntent()
    data object Load: HomeIntent()
}