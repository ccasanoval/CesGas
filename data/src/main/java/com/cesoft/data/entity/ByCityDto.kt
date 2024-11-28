package com.cesoft.data.entity

import com.cesoft.domain.entity.Location
import com.cesoft.domain.entity.Prices
import com.cesoft.domain.entity.Station
import com.google.gson.annotations.SerializedName
import javax.microedition.khronos.opengles.GL
import kotlin.math.max
import kotlin.math.min

/**
{
"Fecha": "28/11/2024 9:59:27",
"ListaEESSPrecio": [
{
"C.P.": "46500",
"Dirección": "AUTOPISTA AP-7 KM. 478",
"Horario": "L-D: 24H",
"Latitud": "39,648944",
"Localidad": "SAGUNTO",
"Longitud (WGS84)": "-0,301806",
"Margen": "D",
"Municipio": "Sagunto/Sagunt",
"Precio Biodiesel": "",
"Precio Bioetanol": "",
"Precio Gas Natural Comprimido": "1,299",
"Precio Gas Natural Licuado": "1,239",
"Precio Gases licuados del petróleo": "",
"Precio Gasoleo A": "",
"Precio Gasoleo B": "",
"Precio Gasoleo Premium": "",
"Precio Gasolina 95 E10": "",
"Precio Gasolina 95 E5": "",
"Precio Gasolina 95 E5 Premium": "",
"Precio Gasolina 98 E10": "",
"Precio Gasolina 98 E5": "",
"Precio Hidrogeno": "",
"Provincia": "VALENCIA / VALÈNCIA",
"Remisión": "dm",
"Rótulo": "HAM",
"Tipo Venta": "P",
"% BioEtanol": "0,0",
"% Éster metílico": "0,0",
"IDEESS": "15218",
"IDMunicipio": "7183",
"IDProvincia": "46",
"IDCCAA": "10"
},...
 */
//{
//    "Fecha": "28/11/2024 9:59:27",
//    "ListaEESSPrecio": [
//    {
//
//    }

data class ByCityDto(
    @SerializedName("ListaEESSPrecio")
    val list: List<ByCityDataDto>
)
data class ByCityDataDto(
    @SerializedName("C.P.")
    val zipCode: String?,
    @SerializedName("Dirección")
    val address: String?,
    @SerializedName("Horario")
    val hours: String?,
    @SerializedName("Latitud")
    val latitude: String?,
    @SerializedName("Longitud (WGS84)")
    val longitude: String?,
    @SerializedName("Localidad")
    val city: String?,
    @SerializedName("Municipio")
    val county: String?,

    @SerializedName("Precio Gasoleo A")
    val goA: String?,
    @SerializedName("Precio Gasoleo B")
    val goB: String?,
    @SerializedName("Precio Gasoleo C")
    val goC: String?,
    @SerializedName("Precio Gasoleo Premium")
    val goAP: String?,
    @SerializedName("Precio Gasolina 95 E10")
    val g95e10: String?,
    @SerializedName("Precio Gasolina 95 E5")
    val g95e5: String?,
    @SerializedName("Precio Gasolina 95 E5 Premium")
    val g95e5P: String?,
    @SerializedName("Precio Gasolina 98 E10")
    val g98e10: String?,
    @SerializedName("Precio Gasolina 98 E5")
    val g98e5: String?,
    //@SerializedName("Precio Gas Natural Licuado")
    //@SerializedName("Precio Gas Natural Comprimido")
    //@SerializedName("Precio Hidrogeno")
    //@SerializedName("Precio Biodiesel")
    @SerializedName("Precio Gases licuados del petróleo")
    val glp: String?,

    @SerializedName("Provincia")
    val state: String?,
    @SerializedName("Rótulo")
    val title: String?,
    @SerializedName("IDEESS")
    val idStation: String?,
    @SerializedName("IDMunicipio")
    val idCity: String?,
    @SerializedName("IDProvincia")
    val idProvince: String?,
    @SerializedName("IDCCAA")
    val idState: String?,
) {
    fun toEntity() = Station(
        zipCode = zipCode ?: "",
        address = address ?: "",
        city = city ?: "",
        county = county ?: "",
        state = state ?: "",
        location = toLocation(),
        hours = hours ?: "",
        title = title ?: "",
        prices = toPrices(),
    )

    private fun toLocation(): Location {
        try {
            val lat = latitude?.toDouble() ?: 0.0
            val lon = longitude?.toDouble() ?: 0.0
            return Location(lat, lon)
        }
        catch (e: Exception) {
            return Location(0.0, 0.0)
        }
    }

    private fun toPrices(): Prices {
        var tmp: Float? = null

        var g95: Float? = g95e5?.replace(',','.')?.toFloatOrNull()
        tmp = g95e10?.replace(',','.')?.toFloatOrNull()
        if(g95 == null) g95 = tmp
        else if(tmp != null && tmp < g95) g95 = tmp
        tmp = g95e5P?.replace(',','.')?.toFloatOrNull()
        if(g95 == null) g95 = tmp
        else if(tmp != null && tmp < g95) g95 = tmp

        var g98: Float? = g98e10?.replace(',','.')?.toFloatOrNull()
        tmp = g98e5?.replace(',','.')?.toFloatOrNull()
        if(g98 == null) g98 = tmp
        else if(tmp != null && tmp < g98) g98 = tmp

        val goa = goA?.replace(',','.')?.toFloatOrNull()
        val gob = goB?.replace(',','.')?.toFloatOrNull()
        val goc = goC?.replace(',','.')?.toFloatOrNull()
        val goap = goAP?.replace(',','.')?.toFloatOrNull()
        val glp = glp?.replace(',','.')?.toFloatOrNull()

        //android.util.Log.e("Prices", "Prices------------${this.title} : ${this.g95e5} / ${this.g95e10} / ${this.g95e5P} == $g95 \n\n")
        return Prices(G95 = g95, G98 = g98, GOA = goa, GOB = gob, GOC = goc, GOAP = goap, GLP = glp)
    }

    companion object {

    }
}