package com.desarrolloaplicaciones1.patitasperdidas.domain.usecase.user

import com.desarrolloaplicaciones1.patitasperdidas.domain.model.User
import com.desarrolloaplicaciones1.patitasperdidas.domain.repository.UserRepository

class UpdateUserProfileUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(user: User) =
        userRepository.updateUserProfile(user)
}
