package com.cesoft.data.prefs

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import com.cesoft.domain.entity.Favorites
import com.cesoft.domain.entity.Filter
import com.cesoft.domain.entity.ProductType
import kotlinx.coroutines.flow.first

/** Each set is a single DataStore transaction: a cancelled save can't leave a half-written filter */
class PrefDataSource(
    private val dataStore: DataStore<Preferences>
) {
    //----------------------------------------------------------------------------------------------
    // FILTER
    suspend fun setFilter(filter: Filter) {
        dataStore.edit { pref ->
            pref[PREFS_PRODUCT] = filter.productType?.name ?: ""
            // Otherwise getFilter() would fall back to it when the product is cleared
            pref.remove(PREFS_ID_PRODUCT)
            pref[PREFS_ID_STATE] = filter.state ?: -1
            pref[PREFS_ID_PROVINCE] = filter.province ?: -1
            pref[PREFS_ID_COUNTY] = filter.county ?: -1
            pref[PREFS_ZIP_CODE] = filter.zipCode
        }
    }
    suspend fun getFilter(): Filter {
        val pref = dataStore.data.first()
        val product = pref[PREFS_PRODUCT] ?: ""
        // Before it was saved by ordinal: keep reading it until the filter is saved again
        val legacyProduct = pref[PREFS_ID_PRODUCT] ?: -1
        val state = pref[PREFS_ID_STATE] ?: -1
        val province = pref[PREFS_ID_PROVINCE] ?: -1
        val county = pref[PREFS_ID_COUNTY] ?: -1
        return Filter(
            productType = ProductType.entries.firstOrNull { it.name == product }
                ?: ProductType.entries.getOrNull(legacyProduct),
            state = if(state > -1) state else null,
            province = if(province > -1) province else null,
            county = if(county > -1) county else null,
            zipCode = pref[PREFS_ZIP_CODE] ?: ""
        )
    }

    //----------------------------------------------------------------------------------------------
    // FAVORITES
    suspend fun setFavorites(favorites: Favorites) {
        dataStore.edit { pref ->
            pref[PREFS_FAV_PRODUCTS] = favorites.products.map { it.name }.toSet()
            pref[PREFS_FAV_STATES] = favorites.states.map { it.toString() }.toSet()
            pref[PREFS_FAV_PROVINCES] = favorites.provinces.map { it.toString() }.toSet()
            pref[PREFS_FAV_COUNTIES] = favorites.counties.map { it.toString() }.toSet()
        }
    }
    suspend fun getFavorites(): Favorites {
        val pref = dataStore.data.first()
        return Favorites(
            products = pref[PREFS_FAV_PRODUCTS].orEmpty()
                .mapNotNull { name -> ProductType.entries.firstOrNull { it.name == name } }.toSet(),
            states = pref[PREFS_FAV_STATES].toIds(),
            provinces = pref[PREFS_FAV_PROVINCES].toIds(),
            counties = pref[PREFS_FAV_COUNTIES].toIds(),
        )
    }
    private fun Set<String>?.toIds() = orEmpty().mapNotNull { it.toIntOrNull() }.toSet()

    //----------------------------------------------------------------------------------------------
    // Constants
    companion object {
        // FILTER
        private val PREFS_PRODUCT = stringPreferencesKey("PREFS_PRODUCT")
        private val PREFS_ID_PRODUCT = intPreferencesKey("PREFS_ID_PRODUCT")// Legacy: ProductType ordinal
        private val PREFS_ID_STATE = intPreferencesKey("PREFS_ID_STATE")
        private val PREFS_ID_PROVINCE = intPreferencesKey("PREFS_ID_PROVINCE")
        private val PREFS_ID_COUNTY = intPreferencesKey("PREFS_ID_COUNTY")
        private val PREFS_ZIP_CODE = stringPreferencesKey("PREFS_ZIP_CODE")
        // FAVORITES
        private val PREFS_FAV_PRODUCTS = stringSetPreferencesKey("PREFS_FAV_PRODUCTS")
        private val PREFS_FAV_STATES = stringSetPreferencesKey("PREFS_FAV_STATES")
        private val PREFS_FAV_PROVINCES = stringSetPreferencesKey("PREFS_FAV_PROVINCES")
        private val PREFS_FAV_COUNTIES = stringSetPreferencesKey("PREFS_FAV_COUNTIES")
    }
}
