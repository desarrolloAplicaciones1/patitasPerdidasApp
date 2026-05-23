package com.desarrolloaplicaciones1.patitasperdidas.domain.usecase.auth

import com.desarrolloaplicaciones1.patitasperdidas.domain.repository.UserRepository

class LoginUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(email: String, password: String): String =
        userRepository.login(email, password)
}
