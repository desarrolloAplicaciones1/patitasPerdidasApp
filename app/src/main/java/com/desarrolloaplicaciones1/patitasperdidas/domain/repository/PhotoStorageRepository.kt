package com.desarrolloaplicaciones1.patitasperdidas.domain.repository

interface PhotoStorageRepository {
    suspend fun uploadAlertPhoto(localUri: String): String
}
