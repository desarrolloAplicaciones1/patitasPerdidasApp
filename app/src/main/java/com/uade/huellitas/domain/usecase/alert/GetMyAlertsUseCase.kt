package com.uade.huellitas.domain.usecase.alert

import com.uade.huellitas.domain.model.Alert
import com.uade.huellitas.domain.repository.AlertRepository
import com.uade.huellitas.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class GetMyAlertsUseCase(
    private val alertRepository: AlertRepository,
    private val userRepository: UserRepository
) {
    operator fun invoke(): Flow<List<Alert>> =
        userRepository.currentUserId?.let(alertRepository::getMyAlerts) ?: flowOf(emptyList())
}
