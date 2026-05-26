package com.uade.huellitas.data.repository

import android.net.Uri
import com.uade.huellitas.domain.repository.PhotoStorageRepository
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.UUID

class FirebasePhotoStorageRepository(
    private val firebaseStorage: FirebaseStorage = FirebaseStorage.getInstance()
) : PhotoStorageRepository {

    override suspend fun uploadAlertPhoto(localUri: String): String {
        return uploadPhoto(localUri, "alerts")
    }

    override suspend fun uploadProfilePhoto(localUri: String): String {
        return uploadPhoto(localUri, "avatars")
    }

    private suspend fun uploadPhoto(localUri: String, folder: String): String {
        require(localUri.isNotBlank()) { "La foto seleccionada no es valida" }

        val remoteRef = firebaseStorage.reference.child("$folder/${UUID.randomUUID()}.jpg")
        remoteRef.putFile(Uri.parse(localUri)).await()
        return remoteRef.downloadUrl.await().toString()
    }
}
