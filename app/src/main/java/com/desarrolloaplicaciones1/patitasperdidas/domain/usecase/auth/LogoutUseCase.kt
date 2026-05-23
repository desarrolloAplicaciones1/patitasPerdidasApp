package com.desarrolloaplicaciones1.patitasperdidas.domain.usecase.auth

import com.desarrolloaplicaciones1.patitasperdidas.domain.repository.UserRepository

class LogoutUseCase(
    private val userRepository: UserRepository
) {
    operator fun invoke() = userRepository.logout()
}
