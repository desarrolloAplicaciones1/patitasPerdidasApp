package com.uade.huellitas.domain.usecase.alert

import com.uade.huellitas.domain.model.Alert
import com.uade.huellitas.domain.repository.AlertRepository

class UpdateAlertUseCase(
    private val alertRepository: AlertRepository
) {
    suspend operator fun invoke(alert: Alert) =
        alertRepository.updateAlert(alert)
}
