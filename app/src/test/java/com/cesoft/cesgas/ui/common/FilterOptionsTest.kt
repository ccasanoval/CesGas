package com.cesoft.cesgas.ui.common

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class FilterOptionsTest {

    private val options = FilterOptions(listOf(
        FilterField(5, "A"),
        FilterField(7, "B", selected = true),
        FilterField(11, "C", selected = true, favorite = true),
    ))

    @Test
    fun `unique select deselects every other field`() {
        val result = options.select(5, selected = true, unique = true)

        assertEquals(listOf(5), result.getSelected().fields.map { it.id })
    }

    @Test
    fun `non unique select keeps previous selection`() {
        val result = options.select(5, selected = true, unique = false)

        assertEquals(listOf(5, 7, 11), result.getSelected().fields.map { it.id })
    }

    @Test
    fun `deselect removes the field from selection`() {
        val result = options.select(7, selected = false, unique = false)

        assertEquals(listOf(11), result.getSelected().fields.map { it.id })
    }

    @Test
    fun `unique deselect leaves nothing selected`() {
        val result = options.select(7, selected = false, unique = true)

        assertNull(result.getSelectedId())
    }

    @Test
    fun `getSelectedId returns the first selected field`() {
        assertEquals(7, options.getSelectedId())
    }

    @Test
    fun `favorite only changes the given field`() {
        val result = options.favorite(5, true).favorite(11, false)

        assertEquals(listOf(true, false, false), result.fields.map { it.favorite })
        assertEquals(options.fields.map { it.selected }, result.fields.map { it.selected })
    }

    @Test
    fun `operations do not mutate the original options`() {
        options.select(5, selected = true, unique = true)
        options.favorite(5, true)

        assertEquals(listOf(7, 11), options.getSelected().fields.map { it.id })
        assertEquals(listOf(false, false, true), options.fields.map { it.favorite })
    }
}
