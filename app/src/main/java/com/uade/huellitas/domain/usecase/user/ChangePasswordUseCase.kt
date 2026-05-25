package com.uade.huellitas.domain.usecase.user

import com.uade.huellitas.domain.repository.UserRepository

class ChangePasswordUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(newPassword: String) =
        userRepository.updatePassword(newPassword)
}
