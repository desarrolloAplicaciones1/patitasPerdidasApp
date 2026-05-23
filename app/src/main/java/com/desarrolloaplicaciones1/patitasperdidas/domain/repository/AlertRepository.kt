package com.desarrolloaplicaciones1.patitasperdidas.domain.repository

import com.desarrolloaplicaciones1.patitasperdidas.domain.model.Alert
import kotlinx.coroutines.flow.Flow

interface AlertRepository {
    fun getActiveAlerts(): Flow<List<Alert>>
    fun getMyAlerts(uid: String): Flow<List<Alert>>
    suspend fun getById(id: String): Alert?
    suspend fun saveAlert(alert: Alert)
    suspend fun updateAlert(alert: Alert)
    suspend fun resolveAlert(alert: Alert)
    suspend fun deleteAlert(alert: Alert)
    suspend fun syncFromFirestore()
}
