package com.deweiwang.bookkeeping.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deweiwang.bookkeeping.data.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _alarmEnabled = MutableStateFlow(false)
    val alarmEnabled: StateFlow<Boolean> = _alarmEnabled

    private val _alarmTime = MutableStateFlow<String?>(null)
    val alarmTime: StateFlow<String?> = _alarmTime

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            _alarmEnabled.value = userPreferencesRepository.isAlarmEnabled()
            _alarmTime.value = userPreferencesRepository.getAlarmTime()
        }
    }

    fun setAlarmEnabled(enabled: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.setAlarmEnabled(enabled)
            _alarmEnabled.value = enabled
        }
    }

    fun setAlarmTime(hour: Int, minute: Int) {
        val timeString = String.format("%02d:%02d", hour, minute)
        viewModelScope.launch {
            userPreferencesRepository.setAlarmTime(timeString)
            _alarmTime.value = timeString
        }
    }
}