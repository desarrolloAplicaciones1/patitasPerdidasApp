package com.desarrolloaplicaciones1.patitasperdidas.domain.usecase.user

import com.desarrolloaplicaciones1.patitasperdidas.domain.repository.UserRepository

class ChangePasswordUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(newPassword: String) =
        userRepository.updatePassword(newPassword)
}
