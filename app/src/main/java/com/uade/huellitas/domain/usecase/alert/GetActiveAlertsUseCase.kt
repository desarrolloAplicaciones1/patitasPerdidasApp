package com.uade.huellitas.domain.usecase.alert

import com.uade.huellitas.domain.repository.AlertRepository

class GetActiveAlertsUseCase(
    private val alertRepository: AlertRepository
) {
    operator fun invoke() = alertRepository.getActiveAlerts()
}
