package com.cesoft.data.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

private const val PREFERENCE_NAME = "CesGasPrefDataStore"
internal val Context.prefDataStore: DataStore<Preferences> by preferencesDataStore(name = PREFERENCE_NAME)
