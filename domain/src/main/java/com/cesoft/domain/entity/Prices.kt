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
    companion object {
        val Empty = Prices(0f, 0f, 0f, 0f, 0f, 0f, 0f)
    }
}
