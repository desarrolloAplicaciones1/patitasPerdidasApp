package com.uade.huellitas.domain.repository

import com.uade.huellitas.domain.model.AppSettings
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun getSettings(): Flow<AppSettings>
    suspend fun setDarkModeEnabled(enabled: Boolean)
    suspend fun setAlertRadiusKm(radiusKm: Int)
    suspend fun setOfflineModeEnabled(enabled: Boolean)
}
