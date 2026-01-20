package com.onedeepath.balanccapp.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsPreferences(private val context: Context) {

    // DARK MODE
    private val Context.dataStore by preferencesDataStore(name = "settings")
    private val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")
    val isDarkMode: Flow<Boolean> = context.dataStore.data
        .map { preferences -> preferences[DARK_MODE_KEY] ?: false }
    suspend fun setDarkMode(enabled: Boolean)  {
        context.dataStore.edit { preferences ->
            preferences[DARK_MODE_KEY] = enabled
        }
    }

    // LANGUAGE
    private val LANGUAGE_KEY = stringPreferencesKey("language_code")

    val languageCode: Flow<String> = context.dataStore.data
        .map { it[LANGUAGE_KEY] ?: "es" }

    suspend fun setLanguage(langCode: String) {
        context.dataStore.edit { it[LANGUAGE_KEY] = langCode }
    }
}
