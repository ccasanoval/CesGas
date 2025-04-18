package com.cesoft.domain.entity

data class Station(
    val id: Int,
    val zipCode: String,
    val address: String,
    val city: String,
    val county: String,
    val state: String,
    val location: Location,
    val hours: String,
    val title: String,
    val prices: Prices,
    val workingPrice: Float = 0f
//    val idStation: String,
//    val idCity: String,
//    val idProvince: String,
//    val idState: String,
) {
    companion object {
        val Empty = Station(0, "", "", "", "", "",
            Location(0.0, 0.0), "", "", Prices.Empty)
    }
}

