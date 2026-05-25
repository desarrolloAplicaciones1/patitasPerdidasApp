package com.uade.huellitas.domain.usecase.auth

import com.uade.huellitas.domain.repository.UserRepository

class IsLoggedInUseCase(
    private val userRepository: UserRepository
) {
    operator fun invoke(): Boolean = userRepository.isLoggedIn()
}
