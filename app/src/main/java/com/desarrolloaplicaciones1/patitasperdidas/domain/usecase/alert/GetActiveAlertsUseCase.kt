package com.desarrolloaplicaciones1.patitasperdidas.domain.usecase.alert

import com.desarrolloaplicaciones1.patitasperdidas.domain.repository.AlertRepository

class GetActiveAlertsUseCase(
    private val alertRepository: AlertRepository
) {
    operator fun invoke() = alertRepository.getActiveAlerts()
}
