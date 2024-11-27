package com.cesoft.data.entity

import com.google.gson.annotations.SerializedName

/*
*
"C.P.": "46500",
"Dirección": "CALLE VALENCIA, 110",
"Horario": "L-D: 24H",
"Latitud": "39,672806",
"Localidad": "SAGUNTO",
"Longitud (WGS84)": "-0,275639",
"Margen": "D",
"Municipio": "Sagunto/Sagunt",
"Precio Biodiesel": "",
"Precio Bioetanol": "",
"Precio Gas Natural Comprimido": "",
"Precio Gas Natural Licuado": "",
"Precio Gases licuados del petróleo": "",
"Precio Gasoleo A": "1,339",
"Precio Gasoleo B": "",
"Precio Gasoleo Premium": "",
"Precio Gasolina 95 E10": "",
"Precio Gasolina 95 E5": "1,459",
"Precio Gasolina 95 E5 Premium": "",
"Precio Gasolina 98 E10": "",
"Precio Gasolina 98 E5": "",
"Precio Hidrogeno": "",
"Provincia": "VALENCIA / VALÈNCIA",
"Remisión": "dm",
"Rótulo": "OPTYME",
"Tipo Venta": "P",
"% BioEtanol": "0,0",
"% Éster metílico": "0,0",
"IDEESS": "14540",
"IDMunicipio": "7183",
"IDProvincia": "46",
"IDCCAA": "10"
*/
data class ByCityDto(
    @SerializedName("C.P.")
    val zipCode: String,
    @SerializedName("Dirección")
    val address: String,
    @SerializedName("Horario")
    val hours: String,
    @SerializedName("Latitud")
    val latitude: String,
    @SerializedName("Longitud (WGS84)")
    val longitude: String,
    @SerializedName("Localidad")
    val city: String,
    @SerializedName("Municipio")
    val county: String,

    @SerializedName("Precio Gasoleo A")
    val goA: String,
    @SerializedName("Precio Gasoleo B")
    val goB: String,
    @SerializedName("Precio Gasoleo Premium")
    val goAP: String,

    @SerializedName("Precio Gasolina 95 E10")
    val g95e10: String,
    @SerializedName("Precio Gasolina 95 E5")
    val g95e5: String,
    @SerializedName("Precio Gasolina 95 E5 Premium")
    val g95e5P: String,

    @SerializedName("Precio Gasolina 98 E10")
    val g98e10: String,
    @SerializedName("Precio Gasolina 98 E5")
    val g98e5: String,

    @SerializedName("Provincia")
    val state: String,
    @SerializedName("Rótulo")
    val title: String,
    //"Tipo Venta": "P",
    @SerializedName("IDEESS")
    val idStation: String,
    @SerializedName("IDMunicipio")
    val idCity: String,
    @SerializedName("IDProvincia")
    val idProvince: String,
    @SerializedName("IDCCAA")
    val idState: String,
) {
}