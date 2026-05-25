package com.desarrolloaplicaciones1.patitasperdidas.data.network

import com.desarrolloaplicaciones1.patitasperdidas.domain.model.Alert
import com.desarrolloaplicaciones1.patitasperdidas.domain.model.AlertStatus
import com.desarrolloaplicaciones1.patitasperdidas.domain.model.AlertType
import com.desarrolloaplicaciones1.patitasperdidas.domain.model.Location
import com.desarrolloaplicaciones1.patitasperdidas.domain.model.PetType
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirestoreAlertDataSource {
    private val db = FirebaseFirestore.getInstance()
    private val alertsRef = db.collection("alerts")

    suspend fun getActiveAlerts(): List<Alert> =
        alertsRef.whereEqualTo("status", "ACTIVE")
            .get().await()
            .documents.mapNotNull { it.toAlert() }

    suspend fun saveAlert(alert: Alert) {
        alertsRef.document(alert.id).set(alert.toMap()).await()
    }

    suspend fun updateAlert(alert: Alert) {
        alertsRef.document(alert.id).set(alert.toMap()).await()
    }

    suspend fun deleteAlert(alertId: String) {
        alertsRef.document(alertId).delete().await()
    }

    private fun DocumentSnapshot.toAlert(): Alert? = runCatching {
        Alert(
            id = id,
            ownerId = getString("ownerId")!!,
            petId = getString("petId"),
            type = AlertType.valueOf(getString("type")!!),
            status = AlertStatus.valueOf(getString("status")!!),
            petName = getString("petName")!!,
            petType = PetType.valueOf(getString("petType")!!),
            breed = getString("breed"),
            color = getString("color"),
            size = getString("size"),
            hasCollar = getBoolean("hasCollar"),
            isCastrated = getBoolean("isCastrated"),
            description = getString("description")!!,
            photoUrls = (get("photoUrls") as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
            location = Location(
                latitude = getDouble("latitude")!!,
                longitude = getDouble("longitude")!!,
                address = getString("address")
            ),
            contactPhone = getString("contactPhone"),
            createdAt = getLong("createdAt")!!,
            updatedAt = getLong("updatedAt")!!
        )
    }.getOrNull()

    private fun Alert.toMap(): Map<String, Any?> = mapOf(
        "ownerId" to ownerId,
        "petId" to petId,
        "type" to type.name,
        "status" to status.name,
        "petName" to petName,
        "petType" to petType.name,
        "breed" to breed,
        "color" to color,
        "size" to size,
        "hasCollar" to hasCollar,
        "isCastrated" to isCastrated,
        "description" to description,
        "photoUrls" to photoUrls,
        "latitude" to location.latitude,
        "longitude" to location.longitude,
        "address" to location.address,
        "contactPhone" to contactPhone,
        "createdAt" to createdAt,
        "updatedAt" to updatedAt
    )
}
