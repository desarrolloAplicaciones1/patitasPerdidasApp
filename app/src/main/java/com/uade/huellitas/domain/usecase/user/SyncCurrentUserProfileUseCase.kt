package com.uade.huellitas.domain.usecase.user

import com.uade.huellitas.domain.repository.UserRepository

class SyncCurrentUserProfileUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke() = userRepository.syncCurrentUserProfile()
}
