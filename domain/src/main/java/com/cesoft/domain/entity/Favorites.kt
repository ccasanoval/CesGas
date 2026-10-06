package com.cesoft.domain.entity

/** Filter options the user starred: they are listed first */
data class Favorites(
    val products: Set<ProductType> = setOf(),
    val states: Set<Int> = setOf(),
    val provinces: Set<Int> = setOf(),
    val counties: Set<Int> = setOf(),
) {
    companion object {
        val Empty = Favorites()
    }
}
