package com.desarrolloaplicaciones1.patitasperdidas.data.repository

import com.desarrolloaplicaciones1.patitasperdidas.data.local.dao.UserDao
import com.desarrolloaplicaciones1.patitasperdidas.data.mapper.toDomain
import com.desarrolloaplicaciones1.patitasperdidas.data.mapper.toEntity
import com.desarrolloaplicaciones1.patitasperdidas.data.network.AuthUserProfile
import com.desarrolloaplicaciones1.patitasperdidas.data.network.FirebaseAuthDataSource
import com.desarrolloaplicaciones1.patitasperdidas.data.network.FirestoreUserDataSource
import com.desarrolloaplicaciones1.patitasperdidas.domain.model.User
import com.desarrolloaplicaciones1.patitasperdidas.domain.repository.UserRepository as UserRepositoryContract
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserRepository(
    private val userDao: UserDao,
    private val authDataSource: FirebaseAuthDataSource,
    private val remoteDataSource: FirestoreUserDataSource
) : UserRepositoryContract {
    override val currentUserId: String? get() = authDataSource.currentUserId

    override fun isLoggedIn(): Boolean = authDataSource.isLoggedIn()

    override fun getUser(uid: String): Flow<User?> =
        userDao.getUser(uid).map { it?.toDomain() }

    override suspend fun syncCurrentUserProfile(): User? {
        val authUser = authDataSource.currentUserProfile ?: return null
        val localUser = userDao.getById(authUser.uid)?.toDomain()
        val remoteUser = runCatching { remoteDataSource.getUser(authUser.uid) }.getOrNull()

        val resolvedUser = remoteUser
            ?: localUser
            ?: authUser.toDomainUser()

        userDao.insert(resolvedUser.toEntity())

        if (remoteUser == null) {
            runCatching { remoteDataSource.saveUser(resolvedUser) }
        }

        return resolvedUser
    }

    override suspend fun register(email: String, password: String): String =
        authDataSource.register(email, password)

    override suspend fun login(email: String, password: String): String {
        val uid = authDataSource.login(email, password)
        syncCurrentUserProfile()
        return uid
    }

    override fun logout() = authDataSource.logout()

    override suspend fun saveUserProfile(user: User) {
        remoteDataSource.saveUser(user)
        userDao.insert(user.toEntity())
        authDataSource.updateDisplayName(user.name)
    }

    override suspend fun updateUserProfile(user: User) {
        remoteDataSource.saveUser(user)
        userDao.insert(user.toEntity())
        authDataSource.updateDisplayName(user.name)
    }

    override suspend fun updatePassword(newPassword: String) =
        authDataSource.updatePassword(newPassword)

    companion object {
        @Volatile private var INSTANCE: UserRepository? = null

        fun getInstance(
            userDao: UserDao,
            authDataSource: FirebaseAuthDataSource,
            remoteDataSource: FirestoreUserDataSource
        ): UserRepository =
            INSTANCE ?: synchronized(this) {
                UserRepository(userDao, authDataSource, remoteDataSource).also { INSTANCE = it }
            }
    }
}

private fun AuthUserProfile.toDomainUser(): User {
    val resolvedEmail = email ?: error("No hay email disponible para el usuario autenticado")
    val fallbackName = resolvedEmail.substringBefore("@").ifBlank { "Usuario" }

    return User(
        uid = uid,
        name = displayName.orEmpty().ifBlank { fallbackName },
        email = resolvedEmail,
        phone = phoneNumber,
        avatarUrl = photoUrl,
        createdAt = System.currentTimeMillis()
    )
}
