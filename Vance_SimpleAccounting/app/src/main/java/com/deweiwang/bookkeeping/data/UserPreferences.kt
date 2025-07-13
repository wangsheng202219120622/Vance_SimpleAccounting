package com.deweiwang.bookkeeping.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.dataStore by preferencesDataStore(name = "user_preferences")

class UserPreferences @Inject constructor(@ApplicationContext context: Context) {

    private val dataStore = context.dataStore

    companion object {
        val SHOW_VALUES_KEY = booleanPreferencesKey("show_values")
        val ALARM_ENABLED_KEY = booleanPreferencesKey("alarm_enabled")
        val ALARM_TIME_KEY = stringPreferencesKey("alarm_time")
    }

    val showValuesFlow: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[SHOW_VALUES_KEY] ?: true
        }

    suspend fun setShowValues(show: Boolean) {
        dataStore.edit { preferences ->
            preferences[SHOW_VALUES_KEY] = show
        }
    }

    val alarmEnabledFlow: Flow<Boolean> = dataStore.data
        .map { preferences -> preferences[ALARM_ENABLED_KEY] ?: false }

    val alarmTimeFlow: Flow<String?> = dataStore.data
        .map { preferences -> preferences[ALARM_TIME_KEY] }

    suspend fun setAlarmEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[ALARM_ENABLED_KEY] = enabled
        }
    }

    suspend fun setAlarmTime(time: String) {
        dataStore.edit { preferences ->
            preferences[ALARM_TIME_KEY] = time
        }
    }
}