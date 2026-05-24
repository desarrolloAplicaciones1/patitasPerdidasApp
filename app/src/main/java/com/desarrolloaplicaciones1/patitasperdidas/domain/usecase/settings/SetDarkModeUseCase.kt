package com.desarrolloaplicaciones1.patitasperdidas.domain.usecase.settings

import com.desarrolloaplicaciones1.patitasperdidas.domain.repository.SettingsRepository

class SetDarkModeUseCase(
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(enabled: Boolean) =
        settingsRepository.setDarkModeEnabled(enabled)
}
