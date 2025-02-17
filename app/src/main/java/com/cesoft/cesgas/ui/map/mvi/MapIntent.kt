package com.cesoft.cesgas.ui.map.mvi

import com.adidas.mvi.Intent

sealed class MapIntent: Intent {
    data object Close : MapIntent()
    data object Load : MapIntent()
}