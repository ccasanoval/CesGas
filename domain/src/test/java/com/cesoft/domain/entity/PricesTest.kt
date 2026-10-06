package com.cesoft.domain.entity

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class PricesTest {

    private val prices = Prices(G95 = 1.1f, G98 = 1.2f, GOA = 1.3f, GOB = 1.4f, GOC = 1.5f, GOAP = 1.6f, GLP = 1.7f)

    @Test
    fun `returns the price of each product`() {
        assertEquals(1.1f, prices.of(ProductType.G95))
        assertEquals(1.2f, prices.of(ProductType.G98))
        assertEquals(1.3f, prices.of(ProductType.GOA))
        assertEquals(1.4f, prices.of(ProductType.GOB))
        assertEquals(1.5f, prices.of(ProductType.GOC))
        assertEquals(1.6f, prices.of(ProductType.GOAP))
        assertEquals(1.7f, prices.of(ProductType.GLP))
    }

    @Test
    fun `no single product has no price`() {
        assertNull(prices.of(ProductType.ALL))
        assertNull(prices.of(ProductType.UNKNOWN))
        assertNull(prices.of(null))
    }

    @Test
    fun `product not sold has no price`() {
        assertNull(prices.copy(GLP = null).of(ProductType.GLP))
    }
}
