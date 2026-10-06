package com.cesoft.domain.entity

import org.junit.Assert.assertEquals
import org.junit.Test

class ProductTest {

    private fun typeOf(acronym: String) = Product(id = 0, name = "", acronym = acronym).type

    @Test
    fun `gasoline 95 variants map to G95`() {
        assertEquals(ProductType.G95, typeOf("G95E5"))
        assertEquals(ProductType.G95, typeOf("G95E10"))
        assertEquals(ProductType.G95, typeOf("G95E5+"))
    }

    @Test
    fun `gasoline 98 variants map to G98`() {
        assertEquals(ProductType.G98, typeOf("G98E5"))
        assertEquals(ProductType.G98, typeOf("G98E10"))
    }

    @Test
    fun `diesel acronyms map to their type`() {
        assertEquals(ProductType.GOA, typeOf("GOA"))
        assertEquals(ProductType.GOAP, typeOf("GOA+"))
        assertEquals(ProductType.GOB, typeOf("GOB"))
        assertEquals(ProductType.GOC, typeOf("GOC"))
    }

    @Test
    fun `GLP maps to GLP`() {
        assertEquals(ProductType.GLP, typeOf("GLP"))
    }

    @Test
    fun `unsupported products map to UNKNOWN`() {
        listOf("BIE", "BIO", "GNC", "GNL", "H2", "JETA1", "").forEach {
            assertEquals("acronym=$it", ProductType.UNKNOWN, typeOf(it))
        }
    }
}
