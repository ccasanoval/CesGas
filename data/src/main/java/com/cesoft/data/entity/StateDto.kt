package com.cesoft.data.entity

import com.cesoft.domain.entity.AddressState
import com.google.gson.annotations.SerializedName

data class StateDto(
    @SerializedName("IDCCAA")
    val id: Int,
    @SerializedName("CCAA")
    val name: String,
) {
    fun toEntity() = AddressState(id = id, name = name)
}
