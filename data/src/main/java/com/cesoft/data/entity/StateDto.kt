package com.cesoft.data.entity

import com.google.gson.annotations.SerializedName

data class StateDto(
    @SerializedName("IDCCAA")
    val id: Int,
    @SerializedName("CCAA")
    val name: String,
) {
}