package com.desarrolloaplicaciones1.patitasperdidas.domain.repository

import com.desarrolloaplicaciones1.patitasperdidas.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    val currentUserId: String?

    fun isLoggedIn(): Boolean
    fun getUser(uid: String): Flow<User?>
    suspend fun syncCurrentUserProfile(): User?
    suspend fun register(email: String, password: String): String
    suspend fun login(email: String, password: String): String
    fun logout()
    suspend fun saveUserProfile(user: User)
    suspend fun updateUserProfile(user: User)
    suspend fun updatePassword(newPassword: String)
}
