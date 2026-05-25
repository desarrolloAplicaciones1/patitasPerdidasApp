package com.uade.huellitas.domain.repository

interface PhotoStorageRepository {
    suspend fun uploadAlertPhoto(localUri: String): String
}
