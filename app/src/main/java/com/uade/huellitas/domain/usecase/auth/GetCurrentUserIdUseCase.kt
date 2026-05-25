package com.uade.huellitas.domain.usecase.auth

import com.uade.huellitas.domain.repository.UserRepository

class GetCurrentUserIdUseCase(
    private val userRepository: UserRepository
) {
    operator fun invoke(): String? = userRepository.currentUserId
}
