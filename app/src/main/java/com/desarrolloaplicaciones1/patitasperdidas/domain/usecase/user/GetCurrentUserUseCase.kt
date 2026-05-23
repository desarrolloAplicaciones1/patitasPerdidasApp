package com.desarrolloaplicaciones1.patitasperdidas.domain.usecase.user

import com.desarrolloaplicaciones1.patitasperdidas.domain.model.User
import com.desarrolloaplicaciones1.patitasperdidas.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class GetCurrentUserUseCase(
    private val userRepository: UserRepository
) {
    operator fun invoke(): Flow<User?> =
        userRepository.currentUserId?.let(userRepository::getUser) ?: flowOf(null)
}
