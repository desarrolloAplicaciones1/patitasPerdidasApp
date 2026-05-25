package com.uade.huellitas.domain.usecase.alert

import com.uade.huellitas.domain.repository.AlertRepository

class SyncAlertsUseCase(
    private val alertRepository: AlertRepository
) {
    suspend operator fun invoke() =
        alertRepository.syncFromFirestore()
}
