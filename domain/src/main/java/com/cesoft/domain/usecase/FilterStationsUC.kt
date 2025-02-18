package com.cesoft.domain.usecase

import com.cesoft.domain.entity.Filter
import com.cesoft.domain.entity.Station
import javax.inject.Inject

class FilterStationsUC @Inject constructor(
    private val getByState: GetByStateUC,
    private val getByProvince: GetByProvinceUC,
    private val getByCounty: GetByCountyUC,
) {
    //TODO: May return Result so client can see eventual Error
    suspend operator fun invoke(filter: Filter): List<Station> {
        val county = filter.county
        val province = filter.province
        val state = filter.state
        val productType = filter.productType
        val zipCode = filter.zipCode

        var stations = if (county != null && province != null && state != null) {
            getByCounty(county, productType).getOrNull() ?: listOf()
        } else if (province != null && state != null) {
            getByProvince(province, productType).getOrNull() ?: listOf()
        } else if (state != null) {
            getByState(state, productType).getOrNull() ?: listOf()
        } else listOf()
        if (zipCode.isNotBlank()) {
            stations = stations.filter { s -> s.zipCode == zipCode }
        }
        return stations
    }
}
