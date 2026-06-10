package com.nabil.ahmed.waterfilter.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nabil.ahmed.waterfilter.data.repository.SettingsRepository
import com.nabil.ahmed.waterfilter.data.repository.UserSettings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: SettingsRepository
) : ViewModel() {

    val settings: StateFlow<UserSettings> = repository.settingsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserSettings(
                enableAll = true,
                earlyWarnings = true,
                dueDateReminders = true,
                sound = true,
                vibration = true,
                quietHours = false,
                quietHoursStart = "22:00",
                quietHoursEnd = "08:00"
            )
        )

    fun toggleEnableAll(enabled: Boolean) = viewModelScope.launch {
        repository.updateEnableAll(enabled)
    }

    fun toggleEarlyWarnings(enabled: Boolean) = viewModelScope.launch {
        repository.updateEarlyWarnings(enabled)
    }

    fun toggleDueDateReminders(enabled: Boolean) = viewModelScope.launch {
        repository.updateDueDateReminders(enabled)
    }

    fun toggleSound(enabled: Boolean) = viewModelScope.launch {
        repository.updateSound(enabled)
    }

    fun toggleVibration(enabled: Boolean) = viewModelScope.launch {
        repository.updateVibration(enabled)
    }

    fun toggleQuietHours(enabled: Boolean) = viewModelScope.launch {
        repository.updateQuietHours(enabled)
    }

    fun updateQuietHoursStart(time: String) = viewModelScope.launch {
        repository.updateQuietHoursStart(time)
    }

    fun updateQuietHoursEnd(time: String) = viewModelScope.launch {
        repository.updateQuietHoursEnd(time)
    }
}
