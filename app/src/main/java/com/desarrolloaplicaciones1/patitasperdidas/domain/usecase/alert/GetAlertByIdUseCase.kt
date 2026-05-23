package com.desarrolloaplicaciones1.patitasperdidas.domain.usecase.alert

import com.desarrolloaplicaciones1.patitasperdidas.domain.model.Alert
import com.desarrolloaplicaciones1.patitasperdidas.domain.repository.AlertRepository

class GetAlertByIdUseCase(
    private val alertRepository: AlertRepository
) {
    suspend operator fun invoke(alertId: String): Alert? =
        alertRepository.getById(alertId)
}
