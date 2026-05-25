package com.uade.huellitas.domain.usecase.settings

import com.uade.huellitas.domain.repository.SettingsRepository

class SetAlertRadiusUseCase(
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(radiusKm: Int) =
        settingsRepository.setAlertRadiusKm(radiusKm)
}
