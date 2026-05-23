package com.desarrolloaplicaciones1.patitasperdidas.data.repository

import com.desarrolloaplicaciones1.patitasperdidas.data.local.dao.UserDao
import com.desarrolloaplicaciones1.patitasperdidas.data.mapper.toDomain
import com.desarrolloaplicaciones1.patitasperdidas.data.mapper.toEntity
import com.desarrolloaplicaciones1.patitasperdidas.data.network.FirebaseAuthDataSource
import com.desarrolloaplicaciones1.patitasperdidas.domain.model.User
import com.desarrolloaplicaciones1.patitasperdidas.domain.repository.UserRepository as UserRepositoryContract
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserRepository(
    private val userDao: UserDao,
    private val authDataSource: FirebaseAuthDataSource
) : UserRepositoryContract {
    override val currentUserId: String? get() = authDataSource.currentUserId

    override fun isLoggedIn(): Boolean = authDataSource.isLoggedIn()

    override fun getUser(uid: String): Flow<User?> =
        userDao.getUser(uid).map { it?.toDomain() }

    override suspend fun register(email: String, password: String): String =
        authDataSource.register(email, password)

    override suspend fun login(email: String, password: String): String =
        authDataSource.login(email, password)

    override fun logout() = authDataSource.logout()

    override suspend fun saveUserProfile(user: User) = userDao.insert(user.toEntity())

    override suspend fun updateUserProfile(user: User) = userDao.update(user.toEntity())

    companion object {
        @Volatile private var INSTANCE: UserRepository? = null

        fun getInstance(userDao: UserDao, authDataSource: FirebaseAuthDataSource): UserRepository =
            INSTANCE ?: synchronized(this) {
                UserRepository(userDao, authDataSource).also { INSTANCE = it }
            }
    }
}
