package com.uade.huellitas.domain.model

data class Alert(
    val id: String,
    val ownerId: String,
    val petId: String? = null,
    val type: AlertType,
    val status: AlertStatus,
    val petName: String,
    val petType: PetType,
    val breed: String? = null,
    val color: String? = null,
    val size: String? = null,
    val hasCollar: Boolean? = null,
    val isCastrated: Boolean? = null,
    val description: String,
    val photoUrls: List<String> = emptyList(),
    val location: Location,
    val contactPhone: String?,
    val createdAt: Long,
    val updatedAt: Long
)

data class Location(
    val latitude: Double,
    val longitude: Double,
    val address: String? = null
)
