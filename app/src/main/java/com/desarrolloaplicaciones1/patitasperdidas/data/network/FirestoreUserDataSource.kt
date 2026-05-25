package com.desarrolloaplicaciones1.patitasperdidas.data.network

import com.desarrolloaplicaciones1.patitasperdidas.domain.model.User
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirestoreUserDataSource {
    private val db = FirebaseFirestore.getInstance()
    private val usersRef = db.collection("users")

    suspend fun getUser(uid: String): User? =
        usersRef.document(uid)
            .get()
            .await()
            .toUser()

    suspend fun saveUser(user: User) {
        usersRef.document(user.uid).set(user.toMap()).await()
    }

    private fun DocumentSnapshot.toUser(): User? = runCatching {
        val email = getString("email") ?: return null
        val fallbackName = email.substringBefore("@").ifBlank { "Usuario" }

        User(
            uid = id,
            name = getString("name").orEmpty().ifBlank { fallbackName },
            email = email,
            phone = getString("phone"),
            avatarUrl = getString("avatarUrl"),
            location = getString("location"),
            createdAt = getLong("createdAt") ?: System.currentTimeMillis()
        )
    }.getOrNull()

    private fun User.toMap(): Map<String, Any?> = mapOf(
        "uid" to uid,
        "name" to name,
        "email" to email,
        "phone" to phone,
        "avatarUrl" to avatarUrl,
        "location" to location,
        "createdAt" to createdAt
    )
}
