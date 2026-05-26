package com.uade.huellitas.domain.repository

interface PhotoStorageRepository {
    suspend fun uploadAlertPhoto(localUri: String): String
    suspend fun uploadProfilePhoto(localUri: String): String
}
