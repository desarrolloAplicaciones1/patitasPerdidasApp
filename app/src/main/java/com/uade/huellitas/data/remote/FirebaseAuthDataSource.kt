package com.uade.huellitas.data.remote

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.tasks.await

class FirebaseAuthDataSource {
    private val auth = FirebaseAuth.getInstance()

    val currentUserId: String? get() = auth.currentUser?.uid
    val currentUserProfile: AuthUserProfile? get() = auth.currentUser?.toAuthUserProfile()

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

    suspend fun updateDisplayName(displayName: String) {
        updateProfile(displayName = displayName, photoUrl = auth.currentUser?.photoUrl?.toString())
    }

    suspend fun updateProfile(displayName: String, photoUrl: String?) {
        val currentUser = auth.currentUser ?: error("No hay usuario autenticado")
        val requestBuilder = UserProfileChangeRequest.Builder()
            .setDisplayName(displayName)

        photoUrl
            ?.takeIf { it.isNotBlank() }
            ?.let { requestBuilder.setPhotoUri(Uri.parse(it)) }

        val request = requestBuilder.build()
        currentUser.updateProfile(request).await()
    }

    suspend fun sendPasswordResetEmail(email: String): Result<Unit> = runCatching {
        auth.sendPasswordResetEmail(email).await()
    }

    fun logout() = auth.signOut()

    private fun FirebaseUser.toAuthUserProfile(): AuthUserProfile = AuthUserProfile(
        uid = uid,
        email = email,
        displayName = displayName,
        phoneNumber = phoneNumber,
        photoUrl = photoUrl?.toString()
    )
}

data class AuthUserProfile(
    val uid: String,
    val email: String?,
    val displayName: String?,
    val phoneNumber: String?,
    val photoUrl: String?
)

