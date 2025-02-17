package com.cesoft.data.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map


private const val PREFERENCE_NAME = "CesGasPrefDataStore"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = PREFERENCE_NAME)

suspend fun Context.writeString(key: String, value: String) {
    dataStore.edit { pref -> pref[stringPreferencesKey(key)] = value }
}
suspend fun Context.readString(key: String): String {
    return dataStore.data.map { pref ->
        pref[stringPreferencesKey(key)] ?: ""
    }.first()
}
//fun Context.readStringFlow(key: String): Flow<String> {
//    return dataStore.data.map { pref ->
//        pref[stringPreferencesKey(key)] ?: ""
//    }
//}

suspend fun Context.writeDouble(key: String, value: Double) {
    dataStore.edit { pref -> pref[doublePreferencesKey(key)] = value }
}
suspend fun Context.readDouble(key: String): Double? {
    return dataStore.data.map { pref ->
        pref[doublePreferencesKey(key)]
    }.firstOrNull()
}

suspend fun Context.writeFloat(key: String, value: Float) {
    dataStore.edit { pref -> pref[floatPreferencesKey(key)] = value }
}
suspend fun Context.readFloat(key: String): Float? {
    return dataStore.data.map { pref ->
        pref[floatPreferencesKey(key)]
    }.firstOrNull()
}

suspend fun Context.writeInt(key: String, value: Int) {
    dataStore.edit { pref -> pref[intPreferencesKey(key)] = value }
}
suspend fun Context.readInt(key: String): Int? {
    return dataStore.data.map { pref ->
        pref[intPreferencesKey(key)]
    }.firstOrNull()
}
//fun Context.readIntFlow(key: String): Flow<Int> {
//    return dataStore.data.map { pref ->
//        pref[intPreferencesKey(key)] ?: 0
//    }
//}

//suspend fun Context.writeDouble(key: String, value: Double) {
//    dataStore.edit { pref -> pref[doublePreferencesKey(key)] = value }
//}
//fun Context.readDoubleFlow(key: String): Flow<Double> {
//    return dataStore.data.map { pref ->
//        pref[doublePreferencesKey(key)] ?: 0.0
//    }
//}

suspend fun Context.writeLong(key: String, value: Long) {
    dataStore.edit { pref -> pref[longPreferencesKey(key)] = value }
}
suspend fun Context.readLong(key: String): Long {
    return dataStore.data.map { pref ->
        pref[longPreferencesKey(key)] ?: 0L
    }.first()
}

//fun Context.readLongFlow(key: String): Flow<Long> {
//    return dataStore.data.map { pref ->
//        pref[longPreferencesKey(key)] ?: 0L
//    }
//}

suspend fun Context.writeBool(key: String, value: Boolean) {
    dataStore.edit { pref -> pref[booleanPreferencesKey(key)] = value }
}
suspend fun Context.readBool(key: String, default: Boolean = false): Boolean {
    return dataStore.data.map { pref ->
        pref[booleanPreferencesKey(key)] ?: default
    }.first()
}
//fun Context.readBoolFlow(key: String): Flow<Boolean> {
//    return dataStore.data.map { pref ->
//        pref[booleanPreferencesKey(key)] ?: false
//    }
//}
