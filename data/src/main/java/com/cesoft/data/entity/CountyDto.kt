package com.cesoft.data.entity

import com.cesoft.domain.entity.AddressCounty
import com.google.gson.annotations.SerializedName

data class CountyDto(
    @SerializedName("IDMunicipio")
    val id: Int,
    @SerializedName("Municipio")
    val name: String,
) {
    fun toEntity() = AddressCounty(id = id, name = name)
}