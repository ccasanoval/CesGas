package com.cesoft.domain.entity

data class Filter(
    val productType: ProductType? = null,
    val state: Int? = null,
    val province: Int? = null,
    val county: Int? = null,
    val zipCode: String = ""
) {
    companion object {
        val Empty = Filter()
    }
}