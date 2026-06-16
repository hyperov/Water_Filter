package com.nabil.ahmed.waterfilter.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class SettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object PreferencesKeys {
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val ENABLE_ALL = booleanPreferencesKey("enable_all")
        val EARLY_WARNINGS = booleanPreferencesKey("early_warnings")
        val DUE_DATE_REMINDERS = booleanPreferencesKey("due_date_reminders")
        val SOUND = booleanPreferencesKey("sound")
        val VIBRATION = booleanPreferencesKey("vibration")
        val QUIET_HOURS = booleanPreferencesKey("quiet_hours")
        val QUIET_HOURS_START = stringPreferencesKey("quiet_hours_start")
        val QUIET_HOURS_END = stringPreferencesKey("quiet_hours_end")
    }

    val settingsFlow: Flow<UserSettings> = context.dataStore.data
        .map { preferences ->
            UserSettings(
                onboardingCompleted = preferences[PreferencesKeys.ONBOARDING_COMPLETED] ?: false,
                enableAll = preferences[PreferencesKeys.ENABLE_ALL] ?: true,
                earlyWarnings = preferences[PreferencesKeys.EARLY_WARNINGS] ?: true,
                dueDateReminders = preferences[PreferencesKeys.DUE_DATE_REMINDERS] ?: true,
                sound = preferences[PreferencesKeys.SOUND] ?: true,
                vibration = preferences[PreferencesKeys.VIBRATION] ?: true,
                quietHours = preferences[PreferencesKeys.QUIET_HOURS] ?: false,
                quietHoursStart = preferences[PreferencesKeys.QUIET_HOURS_START] ?: "22:00",
                quietHoursEnd = preferences[PreferencesKeys.QUIET_HOURS_END] ?: "08:00",
                isLoaded = true
            )
        }

    suspend fun updateEnableAll(enabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.ENABLE_ALL] = enabled }
    }

    suspend fun updateOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.ONBOARDING_COMPLETED] = completed }
    }

    suspend fun updateEarlyWarnings(enabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.EARLY_WARNINGS] = enabled }
    }

    suspend fun updateDueDateReminders(enabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.DUE_DATE_REMINDERS] = enabled }
    }

    suspend fun updateSound(enabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.SOUND] = enabled }
    }

    suspend fun updateVibration(enabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.VIBRATION] = enabled }
    }

    suspend fun updateQuietHours(enabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.QUIET_HOURS] = enabled }
    }

    suspend fun updateQuietHoursStart(time: String) {
        context.dataStore.edit { it[PreferencesKeys.QUIET_HOURS_START] = time }
    }

    suspend fun updateQuietHoursEnd(time: String) {
        context.dataStore.edit { it[PreferencesKeys.QUIET_HOURS_END] = time }
    }
}

data class UserSettings(
    val onboardingCompleted: Boolean,
    val enableAll: Boolean,
    val earlyWarnings: Boolean,
    val dueDateReminders: Boolean,
    val sound: Boolean,
    val vibration: Boolean,
    val quietHours: Boolean,
    val quietHoursStart: String,
    val quietHoursEnd: String,
    val isLoaded: Boolean = false
)
