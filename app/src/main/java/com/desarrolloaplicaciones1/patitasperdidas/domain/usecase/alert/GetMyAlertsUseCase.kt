package com.desarrolloaplicaciones1.patitasperdidas.domain.usecase.alert

import com.desarrolloaplicaciones1.patitasperdidas.domain.model.Alert
import com.desarrolloaplicaciones1.patitasperdidas.domain.repository.AlertRepository
import com.desarrolloaplicaciones1.patitasperdidas.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class GetMyAlertsUseCase(
    private val alertRepository: AlertRepository,
    private val userRepository: UserRepository
) {
    operator fun invoke(): Flow<List<Alert>> =
        userRepository.currentUserId?.let(alertRepository::getMyAlerts) ?: flowOf(emptyList())
}
