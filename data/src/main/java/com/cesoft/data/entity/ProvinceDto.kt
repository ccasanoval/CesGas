package com.cesoft.data.entity

import com.google.gson.annotations.SerializedName

data class ProvinceDto(
    @SerializedName("IDPovincia")
    val id: Int,
    @SerializedName("Provincia")
    val name: String,
) {
}