package com.uade.huellitas.data.repository

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.uade.huellitas.BuildConfig
import com.uade.huellitas.domain.repository.PhotoStorageRepository
import kotlinx.coroutines.tasks.await
import java.util.UUID

class FirebasePhotoStorageRepository(
    private val firebaseStorage: FirebaseStorage = createFirebaseStorage()
) : PhotoStorageRepository {

    override suspend fun uploadAlertPhoto(ownerId: String, localUri: String): String {
        return uploadPhoto(ownerId, localUri, "alerts")
    }

    override suspend fun uploadProfilePhoto(userId: String, localUri: String): String {
        return uploadPhoto(userId, localUri, "avatars")
    }

    private suspend fun uploadPhoto(userId: String, localUri: String, folder: String): String {
        require(userId.isNotBlank()) { "No hay un usuario autenticado para subir la imagen" }
        require(localUri.isNotBlank()) { "La foto seleccionada no es valida" }

        return try {
            val remoteRef = firebaseStorage.reference.child("$folder/$userId/${UUID.randomUUID()}.jpg")
            remoteRef.putFile(Uri.parse(localUri)).await()
            remoteRef.downloadUrl.await().toString()
        } catch (e: Exception) {
            throw IllegalStateException(
                "No se pudo subir la imagen a Firebase Storage. Revisá el bucket configurado y tus reglas de acceso.",
                e
            )
        }
    }

    companion object {
        private fun createFirebaseStorage(): FirebaseStorage {
            val bucket = BuildConfig.FIREBASE_STORAGE_BUCKET.trim()
            return if (bucket.isNotEmpty()) {
                FirebaseStorage.getInstance(bucket.toStorageUrl())
            } else {
                FirebaseStorage.getInstance()
            }
        }

        private fun String.toStorageUrl(): String =
            if (startsWith("gs://")) this else "gs://$this"
    }
}
