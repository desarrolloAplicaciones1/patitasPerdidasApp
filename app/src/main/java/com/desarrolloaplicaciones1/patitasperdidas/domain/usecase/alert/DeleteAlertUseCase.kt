package com.desarrolloaplicaciones1.patitasperdidas.domain.usecase.alert

import com.desarrolloaplicaciones1.patitasperdidas.domain.model.Alert
import com.desarrolloaplicaciones1.patitasperdidas.domain.repository.AlertRepository

class DeleteAlertUseCase(
    private val alertRepository: AlertRepository
) {
    suspend operator fun invoke(alert: Alert) =
        alertRepository.deleteAlert(alert)
}
