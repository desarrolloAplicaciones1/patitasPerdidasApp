package com.uade.huellitas.data.repository

import com.uade.huellitas.data.local.dao.AlertDao
import com.uade.huellitas.data.mapper.toDomain
import com.uade.huellitas.data.mapper.toEntity
import com.uade.huellitas.data.remote.FirestoreAlertDataSource
import com.uade.huellitas.domain.model.Alert
import com.uade.huellitas.domain.model.AlertStatus
import com.uade.huellitas.domain.repository.AlertRepository as AlertRepositoryContract
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class AlertRepository(
    private val alertDao: AlertDao,
    private val remoteDataSource: FirestoreAlertDataSource
) : AlertRepositoryContract {

    /**
     * Offline-first: emite Room inmediatamente, luego mantiene Room actualizado
     * con un listener reactivo de Firestore. Cada vez que Room cambia, la UI
     * se actualiza automáticamente sin polling.
     */
    override fun getActiveAlerts(): Flow<List<Alert>> = channelFlow {
        // 1. Emite Room al instante (respuesta inmediata, funciona offline)
        launch {
            alertDao.getActiveAlerts()
                .map { list -> list.map { it.toDomain() } }
                .collect { send(it) }
        }

        // 2. Escucha Firestore en tiempo real y sincroniza Room
        //    El Flow de Room arriba re-emite automáticamente con datos frescos
        try {
            remoteDataSource.observeActiveAlerts().collect { remoteAlerts ->
                remoteAlerts.forEach { alertDao.insert(it.toEntity(pendingSync = false)) }
            }
        } catch (_: Exception) {
            // Sin red — Room sigue emitiendo sus datos locales
        }
    }

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
        remoteDataSource.updateAlert(alert)
        alertDao.update(alert.toEntity(pendingSync = false))
    }

    override suspend fun resolveAlert(alert: Alert) {
        val resolved = alert.copy(
            status = AlertStatus.RESOLVED,
            updatedAt = System.currentTimeMillis()
        )
        updateAlert(resolved)
    }

    override suspend fun deleteAlert(alert: Alert) {
        remoteDataSource.deleteAlert(alert.id)
        alertDao.delete(alert.toEntity())
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
