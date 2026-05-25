package com.uade.huellitas.domain.usecase.auth

import com.uade.huellitas.domain.repository.UserRepository

class SendPasswordResetEmailUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(email: String): Result<Unit> {
        if (email.isBlank())
            return Result.failure(IllegalArgumentException("El email no puede estar vacío"))
        if (!email.contains("@"))
            return Result.failure(IllegalArgumentException("El email no tiene un formato válido"))
        return userRepository.sendPasswordResetEmail(email)
    }
}
