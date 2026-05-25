package com.uade.huellitas.domain.usecase.alert

import com.uade.huellitas.domain.model.Alert
import com.uade.huellitas.domain.repository.AlertRepository

class CreateAlertUseCase(
    private val alertRepository: AlertRepository
) {
    suspend operator fun invoke(alert: Alert) =
        alertRepository.saveAlert(alert)
}
