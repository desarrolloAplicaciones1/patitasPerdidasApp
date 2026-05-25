package com.uade.huellitas.domain.usecase.settings

import com.uade.huellitas.domain.repository.SettingsRepository

class SetDarkModeUseCase(
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(enabled: Boolean) =
        settingsRepository.setDarkModeEnabled(enabled)
}
