package com.cesoft.data.prefs

import android.content.Context
import com.cesoft.domain.entity.Filter
import com.cesoft.domain.entity.Location
import com.cesoft.domain.entity.Prices
import com.cesoft.domain.entity.ProductType
import com.cesoft.domain.entity.Station
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.reflect.Type

class PrefDataSource(
    private val context: Context
) {
    //----------------------------------------------------------------------------------------------
    // FILTER
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
    // CURRENT STATION
    suspend fun setCurrentStation(station: Station) {
        withContext(Dispatchers.IO) {
            context.writeString(PREFS_CS_TITLE, station.title)
            context.writeString(PREFS_CS_ADDRESS, station.address)
            context.writeDouble(PREFS_CS_LATITUDE, station.location.latitude)
            context.writeDouble(PREFS_CS_LONGITUDE, station.location.longitude)
            context.writeString(PREFS_CS_HOURS, station.hours)
            context.writeFloat(PREFS_CS_G95, station.prices.G95 ?: 0f)
            context.writeFloat(PREFS_CS_G98, station.prices.G98 ?: 0f)
            context.writeFloat(PREFS_CS_GOA, station.prices.GOA ?: 0f)
            context.writeFloat(PREFS_CS_GOB, station.prices.GOB ?: 0f)
            context.writeFloat(PREFS_CS_GOC, station.prices.GOC ?: 0f)
            context.writeFloat(PREFS_CS_GOAP, station.prices.GOAP ?: 0f)
            context.writeFloat(PREFS_CS_GLP, station.prices.GLP ?: 0f)
        }
    }
    suspend fun getCurrentStation(): Station {
        return withContext(Dispatchers.IO) {
            val title = context.readString(PREFS_CS_TITLE)
            val address = context.readString(PREFS_CS_ADDRESS)
            val latitude = context.readDouble(PREFS_CS_LATITUDE) ?: 0.0
            val longitude = context.readDouble(PREFS_CS_LONGITUDE) ?: 0.0
            val hours = context.readString(PREFS_CS_HOURS)
            val g95 = context.readFloat(PREFS_CS_G95)
            val g98 = context.readFloat(PREFS_CS_G98)
            val goa = context.readFloat(PREFS_CS_GOA)
            val gob = context.readFloat(PREFS_CS_GOB)
            val goc = context.readFloat(PREFS_CS_GOC)
            val goap = context.readFloat(PREFS_CS_GOAP)
            val glp = context.readFloat(PREFS_CS_GLP)
            Station(
                id = 0, // Not used, as API doesn't use it...
                title = title,
                zipCode = "",
                address = address,
                city = "",
                county = "",
                state = "",
                location = Location(latitude, longitude),
                hours = hours,
                prices = Prices(
                    G95 = g95,
                    G98 = g98,
                    GOA = goa,
                    GOB = gob,
                    GOC = goc,
                    GOAP = goap,
                    GLP = glp
                )
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
        // FILTER
        private const val PREFS_ID_PRODUCT = "PREFS_ID_PRODUCT"
        private const val PREFS_ID_STATE = "PREFS_ID_STATE"
        private const val PREFS_ID_PROVINCE = "PREFS_ID_PROVINCE"
        private const val PREFS_ID_COUNTY = "PREFS_ID_COUNTY"
        private const val PREFS_ZIP_CODE = "PREFS_ZIP_CODE"
        // CURRENT STATION
        private const val PREFS_CS_TITLE = "PREFS_CS_TITLE"
        private const val PREFS_CS_ADDRESS = "PREFS_CS_ADDRESS"
        private const val PREFS_CS_LATITUDE = ""
        private const val PREFS_CS_LONGITUDE = "PREFS_CS_LATITUDE"
        private const val PREFS_CS_HOURS = "PREFS_CS_HOURS"
        private const val PREFS_CS_G95 = "PREFS_CS_G95"
        private const val PREFS_CS_G98 = "PREFS_CS_G98"
        private const val PREFS_CS_GOA = "PREFS_CS_GOA"
        private const val PREFS_CS_GOB = "PREFS_CS_GOB"
        private const val PREFS_CS_GOC = "PREFS_CS_GOC"
        private const val PREFS_CS_GOAP = "PREFS_CS_GOAP"
        private const val PREFS_CS_GLP = "PREFS_CS_GLP"
    }
}
