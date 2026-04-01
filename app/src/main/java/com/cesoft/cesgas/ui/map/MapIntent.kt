package com.cesoft.cesgas.ui.map

sealed class MapIntent {
    data object Close : MapIntent()
    data object Load : MapIntent()
}