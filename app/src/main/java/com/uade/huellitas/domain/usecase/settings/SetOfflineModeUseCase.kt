package com.uade.huellitas.domain.usecase.settings

import com.uade.huellitas.domain.repository.SettingsRepository

class SetOfflineModeUseCase(
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(enabled: Boolean) =
        settingsRepository.setOfflineModeEnabled(enabled)
}
