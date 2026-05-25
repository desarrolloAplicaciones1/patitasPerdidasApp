package com.uade.huellitas.domain.usecase.settings

import com.uade.huellitas.domain.repository.SettingsRepository

class GetAppSettingsUseCase(
    private val settingsRepository: SettingsRepository
) {
    operator fun invoke() = settingsRepository.getSettings()
}
