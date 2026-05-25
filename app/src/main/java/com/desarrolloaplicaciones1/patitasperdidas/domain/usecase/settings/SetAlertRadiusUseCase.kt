package com.desarrolloaplicaciones1.patitasperdidas.domain.usecase.settings

import com.desarrolloaplicaciones1.patitasperdidas.domain.repository.SettingsRepository

class SetAlertRadiusUseCase(
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(radiusKm: Int) =
        settingsRepository.setAlertRadiusKm(radiusKm)
}
