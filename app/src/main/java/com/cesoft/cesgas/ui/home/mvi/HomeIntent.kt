package com.cesoft.cesgas.ui.home.mvi

import com.adidas.mvi.Intent
import com.cesoft.cesgas.ui.common.FilterOptions

sealed class HomeIntent: Intent {
    data object Close: HomeIntent()
    data object Load: HomeIntent()
    data class Map(val idStation: Int): HomeIntent()
    data class ChangeProduct(val filters: FilterOptions): HomeIntent()
    data class ChangeAddressState(val filters: FilterOptions): HomeIntent()
    data class ChangeAddressProvince(val filters: FilterOptions): HomeIntent()
    data class ChangeAddressCounty(val filters: FilterOptions): HomeIntent()
}