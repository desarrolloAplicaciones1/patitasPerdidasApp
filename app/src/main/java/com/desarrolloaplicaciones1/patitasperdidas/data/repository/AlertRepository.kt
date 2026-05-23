package com.desarrolloaplicaciones1.patitasperdidas.data.repository

import com.desarrolloaplicaciones1.patitasperdidas.data.local.dao.AlertDao
import com.desarrolloaplicaciones1.patitasperdidas.data.mapper.toDomain
import com.desarrolloaplicaciones1.patitasperdidas.data.mapper.toEntity
import com.desarrolloaplicaciones1.patitasperdidas.data.network.FirestoreAlertDataSource
import com.desarrolloaplicaciones1.patitasperdidas.domain.model.Alert
import com.desarrolloaplicaciones1.patitasperdidas.domain.model.AlertStatus
import com.desarrolloaplicaciones1.patitasperdidas.domain.repository.AlertRepository as AlertRepositoryContract
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AlertRepository(
    private val alertDao: AlertDao,
    private val remoteDataSource: FirestoreAlertDataSource
) : AlertRepositoryContract {
    override fun getActiveAlerts(): Flow<List<Alert>> =
        alertDao.getActiveAlerts().map { it.map { entity -> entity.toDomain() } }

    override fun getMyAlerts(uid: String): Flow<List<Alert>> =
        alertDao.getMyAlerts(uid).map { it.map { entity -> entity.toDomain() } }

    override suspend fun getById(id: String): Alert? =
        alertDao.getById(id)?.toDomain()

    override suspend fun saveAlert(alert: Alert) {
        alertDao.insert(alert.toEntity(pendingSync = true))
        try {
            remoteDataSource.saveAlert(alert)
            alertDao.insert(alert.toEntity(pendingSync = false))
        } catch (e: Exception) {
            // queda pendingSync = true; se sincroniza al reconectar
        }
    }

    override suspend fun updateAlert(alert: Alert) {
        alertDao.update(alert.toEntity(pendingSync = true))
        try {
            remoteDataSource.updateAlert(alert)
            alertDao.update(alert.toEntity(pendingSync = false))
        } catch (e: Exception) { }
    }

    override suspend fun resolveAlert(alert: Alert) {
        val resolved = alert.copy(
            status = AlertStatus.RESOLVED,
            updatedAt = System.currentTimeMillis()
        )
        updateAlert(resolved)
    }

    override suspend fun deleteAlert(alert: Alert) {
        alertDao.delete(alert.toEntity())
        try {
            remoteDataSource.deleteAlert(alert.id)
        } catch (e: Exception) { }
    }

    override suspend fun syncFromFirestore() {
        try {
            remoteDataSource.getActiveAlerts()
                .forEach { alertDao.insert(it.toEntity(pendingSync = false)) }
        } catch (e: Exception) { }
    }

    companion object {
        @Volatile private var INSTANCE: AlertRepository? = null

        fun getInstance(alertDao: AlertDao, remoteDataSource: FirestoreAlertDataSource): AlertRepository =
            INSTANCE ?: synchronized(this) {
                AlertRepository(alertDao, remoteDataSource).also { INSTANCE = it }
            }
    }
}
