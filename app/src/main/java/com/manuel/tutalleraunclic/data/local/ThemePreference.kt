package com.manuel.tutalleraunclic.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.themeDataStore: DataStore<Preferences>
    by preferencesDataStore("theme_prefs")

@Singleton
class ThemePreference @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val DARK_MODE = booleanPreferencesKey("dark_mode")

    val isDarkMode: Flow<Boolean> = context.themeDataStore.data
        .map { prefs -> prefs[DARK_MODE] ?: false }

    suspend fun setDarkMode(enabled: Boolean) {
        context.themeDataStore.edit { prefs ->
            prefs[DARK_MODE] = enabled
        }
    }
}
