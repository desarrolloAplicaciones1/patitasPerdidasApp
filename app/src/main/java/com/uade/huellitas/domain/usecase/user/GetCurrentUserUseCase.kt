package com.uade.huellitas.domain.usecase.user

import com.uade.huellitas.domain.model.User
import com.uade.huellitas.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class GetCurrentUserUseCase(
    private val userRepository: UserRepository
) {
    operator fun invoke(): Flow<User?> =
        userRepository.currentUserId?.let(userRepository::getUser) ?: flowOf(null)
}
