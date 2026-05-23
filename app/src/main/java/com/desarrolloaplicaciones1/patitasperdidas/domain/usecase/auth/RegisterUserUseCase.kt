package com.desarrolloaplicaciones1.patitasperdidas.domain.usecase.auth

import com.desarrolloaplicaciones1.patitasperdidas.domain.model.User
import com.desarrolloaplicaciones1.patitasperdidas.domain.repository.UserRepository

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
