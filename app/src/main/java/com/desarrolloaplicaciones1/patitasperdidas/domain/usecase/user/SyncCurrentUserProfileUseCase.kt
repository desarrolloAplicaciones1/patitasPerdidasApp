package com.desarrolloaplicaciones1.patitasperdidas.domain.usecase.user

import com.desarrolloaplicaciones1.patitasperdidas.domain.repository.UserRepository

class SyncCurrentUserProfileUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke() = userRepository.syncCurrentUserProfile()
}
