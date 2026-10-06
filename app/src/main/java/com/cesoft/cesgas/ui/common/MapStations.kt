package com.cesoft.cesgas.ui.common

import com.cesoft.domain.entity.ProductType
import com.cesoft.domain.entity.Station

const val MAX_MAP_STATIONS = 10

enum class PriceTier { CHEAP, MEDIUM, EXPENSIVE }

/** Product whose price is shown on the map: G95 when the filter has no single product */
fun mapProductType(productType: ProductType?): ProductType =
    productType?.takeIf { it != ProductType.ALL && it != ProductType.UNKNOWN } ?: ProductType.G95

/**
 * The cheapest [max] stations that can be drawn: they need a location and a price for the product.
 * Each one gets that price in [Station.workingPrice].
 */
fun stationsForMap(stations: List<Station>, productType: ProductType?, max: Int = MAX_MAP_STATIONS): List<Station> {
    val type = mapProductType(productType)
    return stations
        .filter { it.location.latitude != 0.0 && it.location.longitude != 0.0 }
        .map { it.copy(workingPrice = it.prices.of(type) ?: 0f) }
        .filter { it.workingPrice > 0 }
        .sortedBy { it.workingPrice }
        .take(max)
}

/** Splits the [min]..[max] price range in three equal parts */
fun priceTier(price: Float, min: Float, max: Float): PriceTier {
    val third = (max - min) / 3
    return when {
        price <= min + third -> PriceTier.CHEAP
        price >= max - third -> PriceTier.EXPENSIVE
        else -> PriceTier.MEDIUM
    }
}
