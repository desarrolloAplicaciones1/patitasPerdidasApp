package com.uade.huellitas.domain.usecase.settings

import com.uade.huellitas.domain.repository.SettingsRepository

class SetFollowSystemThemeUseCase(
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(enabled: Boolean) =
        settingsRepository.setFollowSystemTheme(enabled)
}
