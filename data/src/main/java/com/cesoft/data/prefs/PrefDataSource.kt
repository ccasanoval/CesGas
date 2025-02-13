package com.cesoft.data.prefs

import android.content.Context
import com.cesoft.domain.entity.Filter
import com.cesoft.domain.entity.ProductType
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.reflect.Type

class PrefDataSource(
    private val context: Context
) {
    suspend fun setFilter(filter: Filter) {
        withContext(Dispatchers.IO) {
            context.writeInt(PREFS_ID_PRODUCT, filter.productType?.ordinal ?: -1)
            context.writeInt(PREFS_ID_STATE, filter.state ?: -1)
            context.writeInt(PREFS_ID_PROVINCE, filter.province ?: -1)
            context.writeInt(PREFS_ID_COUNTY, filter.county ?: -1)
            context.writeString(PREFS_ZIP_CODE, filter.zipCode ?: "")
        }
    }
    suspend fun getFilter(): Filter {
        return withContext(Dispatchers.IO) {
            val product = context.readInt(PREFS_ID_PRODUCT) ?: -1
            val state = context.readInt(PREFS_ID_STATE) ?: -1
            val province = context.readInt(PREFS_ID_PROVINCE) ?: -1
            val county = context.readInt(PREFS_ID_COUNTY) ?: -1
            val zipCode = context.readString(PREFS_ZIP_CODE)
            return@withContext Filter(
                productType = if(product > -1) ProductType.entries[product] else null,
                state = if(state > -1) state else null,
                province = if(province > -1) province else null,
                county = if(county > -1) county else null,
                zipCode = zipCode
            )
        }
    }

    //----------------------------------------------------------------------------------------------
    private fun zoningToModel(data: String): List<Long>? {
        val gson = Gson()
        val listType: Type = object : TypeToken<List<Long>>() {}.type
        return gson.fromJson(data, listType)
    }

    private fun zoningToDto(zoning: List<Long>): String {
        val gson = Gson()
        return gson.toJson(zoning)
    }

    //----------------------------------------------------------------------------------------------
    // Constants
    companion object {
        private const val PREFS_ID_PRODUCT = "PREFS_ID_PRODUCT"
        private const val PREFS_ID_STATE = "PREFS_ID_STATE"
        private const val PREFS_ID_PROVINCE = "PREFS_ID_PROVINCE"
        private const val PREFS_ID_COUNTY = "PREFS_ID_COUNTY"
        private const val PREFS_ZIP_CODE = "PREFS_ZIP_CODE"
    }
}
