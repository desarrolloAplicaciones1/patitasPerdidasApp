package com.uade.huellitas.data.repository

import android.content.Context
import com.uade.huellitas.domain.model.AppSettings
import com.uade.huellitas.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class PreferencesSettingsRepository(context: Context) : SettingsRepository {
    private val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val settingsState = MutableStateFlow(readSettings())

    override fun getSettings(): Flow<AppSettings> = settingsState.asStateFlow()

    override suspend fun setFollowSystemTheme(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_FOLLOW_SYSTEM_THEME, enabled).apply()
        settingsState.update { it.copy(followSystemTheme = enabled) }
    }

    override suspend fun setDarkModeEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_DARK_MODE, enabled).apply()
        settingsState.update { it.copy(darkModeEnabled = enabled) }
    }

    override suspend fun setAlertRadiusKm(radiusKm: Int) {
        prefs.edit().putInt(KEY_ALERT_RADIUS_KM, radiusKm).apply()
        settingsState.update { it.copy(alertRadiusKm = radiusKm) }
    }

    override suspend fun setOfflineModeEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_OFFLINE_MODE, enabled).apply()
        settingsState.update { it.copy(offlineModeEnabled = enabled) }
    }

    private fun readSettings(): AppSettings = AppSettings(
        followSystemTheme = prefs.getBoolean(KEY_FOLLOW_SYSTEM_THEME, true),
        darkModeEnabled = prefs.getBoolean(KEY_DARK_MODE, false),
        alertRadiusKm = prefs.getInt(KEY_ALERT_RADIUS_KM, 3),
        offlineModeEnabled = prefs.getBoolean(KEY_OFFLINE_MODE, true)
    )

    private companion object {
        const val PREFS_NAME = "huellitas_prefs"
        const val KEY_FOLLOW_SYSTEM_THEME = "follow_system_theme"
        const val KEY_DARK_MODE = "dark_mode"
        const val KEY_ALERT_RADIUS_KM = "alert_radius_km"
        const val KEY_OFFLINE_MODE = "offline_mode"
    }
}
