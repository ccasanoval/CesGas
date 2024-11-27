package com.cesoft.domain.entity

data class Settings(
    val idProduct: Int,
    val idState: Int,
    val idProvince: Int,
    val idCity: Int,
) {
    companion object {
        val Empty = Settings(0,0,0, 0)
    }
}