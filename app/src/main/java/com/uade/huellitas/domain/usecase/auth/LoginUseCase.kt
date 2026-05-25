package com.uade.huellitas.domain.usecase.auth

import com.uade.huellitas.domain.repository.UserRepository

class LoginUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(email: String, password: String): String =
        userRepository.login(email, password)
}
