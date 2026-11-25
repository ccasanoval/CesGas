package com.cesoft.data.prefs

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import com.cesoft.domain.entity.Favorites
import com.cesoft.domain.entity.Filter
import com.cesoft.domain.entity.ProductType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class PrefDataSourceTest {

    @get:Rule
    val folder = TemporaryFolder()

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    /** A real file-backed DataStore that counts the write transactions */
    private class CountingDataStore(private val delegate: DataStore<Preferences>) : DataStore<Preferences> {
        var writes = 0
        override val data: Flow<Preferences> = delegate.data
        override suspend fun updateData(transform: suspend (t: Preferences) -> Preferences): Preferences {
            writes++
            return delegate.updateData(transform)
        }
    }

    private val dataStore = CountingDataStore(
        PreferenceDataStoreFactory.create(scope = scope) { folder.newFile("test.preferences_pb") }
    )
    private val prefs = PrefDataSource(dataStore)

    @After
    fun tearDown() {
        scope.cancel()
    }

    @Test
    fun `filter round trip`() = runTest {
        val filter = Filter(productType = ProductType.GOA, state = 10, province = 46, county = 7183, zipCode = "46500")

        prefs.setFilter(filter)

        assertEquals(filter, prefs.getFilter())
    }

    @Test
    fun `clearing the product is not undone by the legacy ordinal pref`() = runTest {
        // Installs from before the product was saved by name still have its ordinal
        dataStore.edit { it[intPreferencesKey("PREFS_ID_PRODUCT")] = ProductType.G95.ordinal }
        assertEquals(ProductType.G95, prefs.getFilter().productType)

        prefs.setFilter(Filter(productType = null, state = 10))

        assertNull(prefs.getFilter().productType)
    }

    @Test
    fun `the filter is saved in a single atomic write`() = runTest {
        prefs.setFilter(Filter(productType = ProductType.G95, state = 10, province = 46))

        assertEquals(1, dataStore.writes)
    }

    @Test
    fun `favorites are saved in a single atomic write`() = runTest {
        val favorites = Favorites(products = setOf(ProductType.GLP), states = setOf(10), provinces = setOf(46), counties = setOf(7183))

        prefs.setFavorites(favorites)

        assertEquals(1, dataStore.writes)
        assertEquals(favorites, prefs.getFavorites())
    }
}
