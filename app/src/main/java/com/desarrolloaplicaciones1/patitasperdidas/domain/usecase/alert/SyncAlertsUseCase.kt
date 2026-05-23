package com.desarrolloaplicaciones1.patitasperdidas.domain.usecase.alert

import com.desarrolloaplicaciones1.patitasperdidas.domain.repository.AlertRepository

class SyncAlertsUseCase(
    private val alertRepository: AlertRepository
) {
    suspend operator fun invoke() =
        alertRepository.syncFromFirestore()
}
