package com.cesoft.cesgas.ui.common

import com.cesoft.cesgas.ui.station
import com.cesoft.domain.entity.Location
import com.cesoft.domain.entity.ProductType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MapStationsTest {

    // station(id) has G95 = 1.5 + id/100 and no other prices
    private val stations = (1..15).map { station(it) }.shuffled()

    @Test
    fun `keeps the cheapest stations sorted by price`() {
        val result = stationsForMap(stations, ProductType.G95)

        assertEquals((1..MAX_MAP_STATIONS).toList(), result.map { it.id })
    }

    @Test
    fun `sets the product price as working price`() {
        val result = stationsForMap(listOf(station(3)), ProductType.G95)

        assertEquals(1.53f, result.single().workingPrice, 0.0001f)
    }

    @Test
    fun `drops stations without price for the product`() {
        assertTrue(stationsForMap(stations, ProductType.GOA).isEmpty())
    }

    @Test
    fun `drops stations without location`() {
        val noLatitude = station(1, Location(0.0, -0.3))
        val noLongitude = station(2, Location(39.6, 0.0))
        val ok = station(3)

        assertEquals(listOf(3), stationsForMap(listOf(noLatitude, noLongitude, ok), ProductType.G95).map { it.id })
    }

    @Test
    fun `without a single product uses G95`() {
        listOf(null, ProductType.ALL, ProductType.UNKNOWN).forEach {
            assertEquals(ProductType.G95, mapProductType(it))
            assertEquals("productType=$it", 2, stationsForMap(listOf(station(1), station(2)), it).size)
        }
        assertEquals(ProductType.GOA, mapProductType(ProductType.GOA))
    }

    @Test
    fun `price tiers split the range in three`() {
        assertEquals(PriceTier.CHEAP, priceTier(1.00f, 1.00f, 1.30f))
        assertEquals(PriceTier.CHEAP, priceTier(1.05f, 1.00f, 1.30f))
        assertEquals(PriceTier.MEDIUM, priceTier(1.15f, 1.00f, 1.30f))
        assertEquals(PriceTier.EXPENSIVE, priceTier(1.25f, 1.00f, 1.30f))
        assertEquals(PriceTier.EXPENSIVE, priceTier(1.30f, 1.00f, 1.30f))
    }

    @Test
    fun `same price everywhere is cheap`() {
        assertEquals(PriceTier.CHEAP, priceTier(1.5f, 1.5f, 1.5f))
    }
}
