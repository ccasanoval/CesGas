package com.cesoft.cesgas.ui.common

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Locale

class ExtensionsTest {

    private val spain = Locale.forLanguageTag("es-ES")

    @Test
    fun `formats price with three decimals using locale separator`() {
        assertEquals("1,599 €", 1.599f.toMoneyFormat(spain))
        assertEquals("1.599 €", 1.599f.toMoneyFormat(Locale.US))
    }

    @Test
    fun `pads to three decimals`() {
        assertEquals("1,500 €", 1.5f.toMoneyFormat(spain))
    }

    @Test
    fun `null price is shown as a dash, not as zero`() {
        assertEquals("— €", (null as Float?).toMoneyFormat(spain))
    }
}
