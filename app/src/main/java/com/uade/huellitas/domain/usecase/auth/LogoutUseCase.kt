package com.uade.huellitas.domain.usecase.auth

import com.uade.huellitas.domain.repository.UserRepository

class LogoutUseCase(
    private val userRepository: UserRepository
) {
    operator fun invoke() = userRepository.logout()
}
