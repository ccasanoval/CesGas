package com.cesoft.cesgas.ui.home

import com.cesoft.cesgas.ui.common.FilterOptions
import com.cesoft.domain.entity.Station

sealed class HomeIntent {
    data object Close: HomeIntent()
    data object Load: HomeIntent()
    data class GoMap(val station: Station?= null): HomeIntent()
    data class ChangeProduct(val filters: FilterOptions): HomeIntent()
    data class ChangeAddressState(val filters: FilterOptions): HomeIntent()
    data class ChangeAddressProvince(val filters: FilterOptions): HomeIntent()
    data class ChangeAddressCounty(val filters: FilterOptions): HomeIntent()
    data class ChangeAddressZipCode(val zipCode: String): HomeIntent()
}