package com.cesoft.data.entity

import com.cesoft.domain.entity.Product
import com.google.gson.annotations.SerializedName

/*
"IDProducto": "1",
"NombreProducto": "Gasolina 95 E5",
"NombreProductoAbreviatura": "G95E5"
*/
data class ProductDto(
    @SerializedName("IDProducto")
    val id: Int,
    @SerializedName("NombreProducto")
    val name: String,
    @SerializedName("NombreProductoAbreviatura")
    val acronym: String,
) {
    //fun is95() = acronym.contains("95")
    val isG95: Boolean
        get() = acronym.contains("G95")
    val isGOA: Boolean
        get() = acronym == "GOA"
    val isGOAP: Boolean
        get() = acronym == "GOA+"

    fun toEntity() = Product(
        id = id,
        name = name,
        acronym = acronym
    )
}