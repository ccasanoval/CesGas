package com.cesoft.data.entity

import com.cesoft.domain.entity.Location
import com.google.gson.Gson
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class StationDataDtoTest {

    private fun dto(
        idStation: String? = "15218",
        latitude: String? = "39,648944",
        longitude: String? = "-0,301806",
        g95e5: String? = "",
        g95e10: String? = "",
        g95e5P: String? = "",
        g98e5: String? = "",
        g98e10: String? = "",
        goA: String? = "",
        goAP: String? = "",
        glp: String? = "",
    ) = StationDataDto(
        zipCode = "46500", address = "AUTOPISTA AP-7 KM. 478", hours = "L-D: 24H",
        latitude = latitude, longitude = longitude, city = "SAGUNTO", county = "Sagunto/Sagunt",
        goA = goA, goB = "", goC = "", goAP = goAP,
        g95e10 = g95e10, g95e5 = g95e5, g95e5P = g95e5P, g98e10 = g98e10, g98e5 = g98e5,
        glp = glp,
        state = "VALENCIA / VALÈNCIA", title = "HAM",
        idStation = idStation, idCity = "7183", idProvince = "46", idState = "10",
    )

    @Test
    fun `parses decimal comma prices`() {
        val prices = dto(g95e5 = "1,599", goA = "1,459", glp = "0,899").toEntity().prices

        assertEquals(1.599f, prices.G95!!, 0.0001f)
        assertEquals(1.459f, prices.GOA!!, 0.0001f)
        assertEquals(0.899f, prices.GLP!!, 0.0001f)
    }

    @Test
    fun `empty prices are null`() {
        val prices = dto().toEntity().prices

        assertNull(prices.G95)
        assertNull(prices.G98)
        assertNull(prices.GOA)
        assertNull(prices.GOB)
        assertNull(prices.GOC)
        assertNull(prices.GOAP)
        assertNull(prices.GLP)
    }

    @Test
    fun `G95 is the cheapest of E5, E10 and E5 premium`() {
        assertEquals(1.549f, dto(g95e5 = "1,599", g95e10 = "1,549", g95e5P = "1,699").toEntity().prices.G95!!, 0.0001f)
        assertEquals(1.499f, dto(g95e5 = "1,599", g95e10 = "1,549", g95e5P = "1,499").toEntity().prices.G95!!, 0.0001f)
    }

    @Test
    fun `G95 uses any available variant when E5 is missing`() {
        assertEquals(1.549f, dto(g95e10 = "1,549").toEntity().prices.G95!!, 0.0001f)
        assertEquals(1.699f, dto(g95e5P = "1,699").toEntity().prices.G95!!, 0.0001f)
    }

    @Test
    fun `G98 is the cheapest of E5 and E10`() {
        assertEquals(1.689f, dto(g98e5 = "1,789", g98e10 = "1,689").toEntity().prices.G98!!, 0.0001f)
        assertEquals(1.789f, dto(g98e5 = "1,789").toEntity().prices.G98!!, 0.0001f)
    }

    @Test
    fun `parses location with decimal comma`() {
        val location = dto().toEntity().location

        assertEquals(39.648944, location.latitude, 0.000001)
        assertEquals(-0.301806, location.longitude, 0.000001)
    }

    @Test
    fun `invalid or missing location becomes 0,0`() {
        assertEquals(Location(0.0, 0.0), dto(latitude = "abc").toEntity().location)
        assertEquals(Location(0.0, 0.0), dto(latitude = null, longitude = null).toEntity().location)
    }

    @Test
    fun `invalid station id becomes 0 instead of crashing`() {
        assertEquals(15218, dto().toEntity().id)
        assertEquals(0, dto(idStation = "X-1").toEntity().id)
        assertEquals(0, dto(idStation = null).toEntity().id)
    }

    @Test
    fun `deserializes the government API json`() {
        val json = """
            {
              "Fecha": "28/11/2024 9:59:27",
              "ListaEESSPrecio": [{
                "C.P.": "46500",
                "Dirección": "AUTOPISTA AP-7 KM. 478",
                "Horario": "L-D: 24H",
                "Latitud": "39,648944",
                "Localidad": "SAGUNTO",
                "Longitud (WGS84)": "-0,301806",
                "Municipio": "Sagunto/Sagunt",
                "Precio Gases licuados del petróleo": "",
                "Precio Gasoleo A": "1,459",
                "Precio Gasoleo Premium": "1,529",
                "Precio Gasolina 95 E5": "1,599",
                "Precio Gasolina 98 E5": "1,739",
                "Provincia": "VALENCIA / VALÈNCIA",
                "Rótulo": "HAM",
                "IDEESS": "15218",
                "IDMunicipio": "7183",
                "IDProvincia": "46",
                "IDCCAA": "10"
              }]
            }
        """.trimIndent()

        val station = Gson().fromJson(json, StationDto::class.java).list!!.single().toEntity()

        assertEquals(15218, station.id)
        assertEquals("46500", station.zipCode)
        assertEquals("HAM", station.title)
        assertEquals("SAGUNTO", station.city)
        assertEquals("Sagunto/Sagunt", station.county)
        assertEquals("VALENCIA / VALÈNCIA", station.state)
        assertEquals(1.599f, station.prices.G95!!, 0.0001f)
        assertEquals(1.739f, station.prices.G98!!, 0.0001f)
        assertEquals(1.459f, station.prices.GOA!!, 0.0001f)
        assertEquals(1.529f, station.prices.GOAP!!, 0.0001f)
        assertNull(station.prices.GLP)
        assertNull(station.prices.GOB)
    }
}
