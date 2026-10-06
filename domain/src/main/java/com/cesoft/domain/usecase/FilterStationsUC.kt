package com.cesoft.domain.usecase

import com.cesoft.domain.entity.Filter
import com.cesoft.domain.entity.Station
import javax.inject.Inject

class FilterStationsUC @Inject constructor(
    private val getByState: GetByStateUC,
    private val getByProvince: GetByProvinceUC,
    private val getByCounty: GetByCountyUC,
) {
    /** Failures (network, server...) are returned so the client can tell them apart from "no stations" */
    suspend operator fun invoke(filter: Filter): Result<List<Station>> {
        val county = filter.county
        val province = filter.province
        val state = filter.state
        val productType = filter.productType
        val zipCode = filter.zipCode

        val stations = if (county != null && province != null && state != null) {
            getByCounty(county, productType)
        } else if (province != null && state != null) {
            getByProvince(province, productType)
        } else if (state != null) {
            getByState(state, productType)
        } else Result.success(listOf())
        return if (zipCode.isNotBlank()) {
            stations.map { list -> list.filter { s -> s.zipCode == zipCode } }
        } else stations
    }
}
