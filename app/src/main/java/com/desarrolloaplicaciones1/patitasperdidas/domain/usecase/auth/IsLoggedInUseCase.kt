package com.desarrolloaplicaciones1.patitasperdidas.domain.usecase.auth

import com.desarrolloaplicaciones1.patitasperdidas.domain.repository.UserRepository

class IsLoggedInUseCase(
    private val userRepository: UserRepository
) {
    operator fun invoke(): Boolean = userRepository.isLoggedIn()
}
