package com.desarrolloaplicaciones1.patitasperdidas.domain.usecase.auth

import com.desarrolloaplicaciones1.patitasperdidas.domain.repository.UserRepository

class GetCurrentUserIdUseCase(
    private val userRepository: UserRepository
) {
    operator fun invoke(): String? = userRepository.currentUserId
}
