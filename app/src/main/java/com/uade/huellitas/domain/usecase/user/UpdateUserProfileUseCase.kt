package com.uade.huellitas.domain.usecase.user

import com.uade.huellitas.domain.model.User
import com.uade.huellitas.domain.repository.UserRepository

class UpdateUserProfileUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(user: User) =
        userRepository.updateUserProfile(user)
}
