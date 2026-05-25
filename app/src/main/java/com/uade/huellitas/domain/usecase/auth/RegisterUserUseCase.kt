package com.uade.huellitas.domain.usecase.auth

import com.uade.huellitas.domain.model.User
import com.uade.huellitas.domain.repository.UserRepository

class RegisterUserUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(name: String, email: String, password: String): String {
        val uid = userRepository.register(email, password)
        userRepository.saveUserProfile(
            User(uid = uid, name = name, email = email)
        )
        return uid
    }
}
