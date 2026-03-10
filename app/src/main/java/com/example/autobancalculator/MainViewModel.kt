package com.example.autobancalculator

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MainViewModel : ViewModel() {
    private val IS_BANNED_KEY = booleanPreferencesKey("is_banned")

    suspend fun setBanned(dataStore: DataStore<Preferences>, isBanned: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_BANNED_KEY] = isBanned
        }
    }

    fun isBanned(dataStore: DataStore<Preferences>): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[IS_BANNED_KEY] ?: false
        }
    }
}