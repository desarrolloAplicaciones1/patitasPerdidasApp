package com.desarrolloaplicaciones1.patitasperdidas.data.network

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class FirebaseAuthDataSource {
    private val auth = FirebaseAuth.getInstance()

    val currentUserId: String? get() = auth.currentUser?.uid

    fun isLoggedIn(): Boolean = auth.currentUser != null

    suspend fun register(email: String, password: String): String {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        return result.user?.uid ?: error("UID nulo tras registro")
    }

    suspend fun login(email: String, password: String): String {
        val result = auth.signInWithEmailAndPassword(email, password).await()
        return result.user?.uid ?: error("UID nulo tras login")
    }

    suspend fun updatePassword(newPassword: String) {
        val currentUser = auth.currentUser ?: error("No hay usuario autenticado")
        currentUser.updatePassword(newPassword).await()
    }

    fun logout() = auth.signOut()
}
