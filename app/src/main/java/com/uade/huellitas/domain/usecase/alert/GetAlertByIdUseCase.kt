package com.uade.huellitas.domain.usecase.alert

import com.uade.huellitas.domain.model.Alert
import com.uade.huellitas.domain.repository.AlertRepository

class GetAlertByIdUseCase(
    private val alertRepository: AlertRepository
) {
    suspend operator fun invoke(alertId: String): Alert? =
        alertRepository.getById(alertId)
}
