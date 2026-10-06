package com.cesoft.domain.entity

data class Prices(
    val G95: Float?,
    val G98: Float?,
    val GOA: Float?,
    val GOB: Float?,
    val GOC: Float?,
    val GOAP: Float?,
    val GLP: Float?,
) {
    /** Price of the given product, or null if not sold or the type is not a single product (ALL, UNKNOWN) */
    fun of(productType: ProductType?): Float? = when(productType) {
        ProductType.G95 -> G95
        ProductType.G98 -> G98
        ProductType.GOA -> GOA
        ProductType.GOB -> GOB
        ProductType.GOC -> GOC
        ProductType.GOAP -> GOAP
        ProductType.GLP -> GLP
        ProductType.ALL, ProductType.UNKNOWN, null -> null
    }

    companion object {
        val Empty = Prices(0f, 0f, 0f, 0f, 0f, 0f, 0f)
    }
}
