package com.cesoft.domain.entity

import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

class StationTest {

    @Test
    fun `a station survives serialization, as the map screen argument in the saved back stack`() {
        val station = Station(
            id = 15218, zipCode = "46500", address = "AUTOPISTA AP-7 KM. 478", city = "SAGUNTO",
            county = "Sagunto/Sagunt", state = "VALENCIA", location = Location(39.648944, -0.301806),
            hours = "L-D: 24H", title = "HAM",
            prices = Prices(G95 = 1.5f, G98 = null, GOA = 1.4f, GOB = null, GOC = null, GOAP = null, GLP = null),
        )

        val bytes = ByteArrayOutputStream().also { ObjectOutputStream(it).use { out -> out.writeObject(station) } }
        val restored = ObjectInputStream(ByteArrayInputStream(bytes.toByteArray())).use { it.readObject() }

        assertEquals(station, restored)
    }
}
