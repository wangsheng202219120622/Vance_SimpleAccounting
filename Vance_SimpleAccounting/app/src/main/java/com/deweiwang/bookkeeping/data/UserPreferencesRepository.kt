package com.deweiwang.bookkeeping.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferencesRepository @Inject constructor(
    private val userPreferences: UserPreferences
) {
    val showValuesFlow: Flow<Boolean> = userPreferences.showValuesFlow

    suspend fun setShowValues(show: Boolean) {
        userPreferences.setShowValues(show)
    }

    suspend fun setAlarmEnabled(enabled: Boolean) {
        userPreferences.setAlarmEnabled(enabled)
    }

    suspend fun setAlarmTime(time: String) {
        userPreferences.setAlarmTime(time)
    }

    suspend fun isAlarmEnabled(): Boolean {
        return userPreferences.alarmEnabledFlow
            .catch { emit(false) }
            .first()
    }

    suspend fun getAlarmTime(): String? {
        return userPreferences.alarmTimeFlow
            .catch { emit(null) }
            .first()
    }
}