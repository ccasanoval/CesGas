package com.cesoft.data.entity

/**
"C.P.": "46500",
"Dirección": "CARRETERA V-23 KM. 0,5",
"Horario": "L-D: 06:00-22:00",
"Latitud": "39,632944",
"Localidad": "SAGUNTO",
"Longitud (WGS84)": "-0,296750",
"Margen": "D",
"Municipio": "Sagunto/Sagunt",
"PrecioProducto": "1,639",
"Provincia": "VALENCIA / VALÈNCIA",
"Remisión": "dm",
"Rótulo": "REPSOL",
"Tipo Venta": "P",
"IDEESS": "4086",
"IDMunicipio": "7183",
"IDProvincia": "46",
"IDCCAA": "10"*/
data class StationDto(
    val zipCode: String,
    val address: String,
    val latitude: String,
    val longitude: String,
) {
}