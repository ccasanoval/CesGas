package com.cesoft.data.prefs

import android.content.Context
import com.cesoft.domain.entity.Settings
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.reflect.Type
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class PrefDataSource(
    private val context: Context
) {
    suspend fun saveSettings(settings: Settings) {
        withContext(Dispatchers.IO) {
            context.writeInt(PREFS_ID_CITY, settings.idCity)
            context.writeInt(PREFS_ID_PROVINCE, settings.idProvince)
            context.writeInt(PREFS_ID_STATE, settings.idState)
            context.writeInt(PREFS_ID_PRODUCT, settings.idProduct)
        }
    }
    suspend fun readSettings(): Settings {
        return withContext(Dispatchers.IO) {
            val idCity = context.readInt(PREFS_ID_CITY) ?: 0
            val idProvince = context.readInt(PREFS_ID_PROVINCE) ?: 0
            val idState = context.readInt(PREFS_ID_STATE) ?: 0
            val idProduct = context.readInt(PREFS_ID_PRODUCT) ?: 0
            return@withContext Settings(
                idProduct = idProduct,
                idState = idState,
                idProvince = idProvince,
                idCity = idCity
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
        private const val PREFS_ID_CITY = "PREFS_ID_CITY"
        private const val PREFS_ID_PROVINCE = "PREFS_ID_PROVINCE"
        private const val PREFS_ID_STATE = "PREFS_ID_STATE"
        private const val PREFS_ID_PRODUCT = "PREFS_ID_PRODUCT"
    }
}
