package com.desarrolloaplicaciones1.patitasperdidas.domain.usecase.settings

import com.desarrolloaplicaciones1.patitasperdidas.domain.repository.SettingsRepository

class GetAppSettingsUseCase(
    private val settingsRepository: SettingsRepository
) {
    operator fun invoke() = settingsRepository.getSettings()
}
