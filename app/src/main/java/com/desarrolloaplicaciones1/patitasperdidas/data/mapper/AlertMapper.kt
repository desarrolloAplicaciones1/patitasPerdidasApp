package com.desarrolloaplicaciones1.patitasperdidas.data.mapper

import com.desarrolloaplicaciones1.patitasperdidas.data.local.entity.AlertEntity
import com.desarrolloaplicaciones1.patitasperdidas.domain.model.Alert
import com.desarrolloaplicaciones1.patitasperdidas.domain.model.AlertStatus
import com.desarrolloaplicaciones1.patitasperdidas.domain.model.AlertType
import com.desarrolloaplicaciones1.patitasperdidas.domain.model.Location
import com.desarrolloaplicaciones1.patitasperdidas.domain.model.PetType

fun AlertEntity.toDomain(): Alert = Alert(
    id = id,
    ownerId = ownerId,
    petId = petId,
    type = AlertType.valueOf(type),
    status = AlertStatus.valueOf(status),
    petType = PetType.valueOf(petType),
    petName = petName,
    breed = breed,
    color = color,
    size = size,
    hasCollar = hasCollar,
    isCastrated = isCastrated,
    description = description,
    photoUrls = if (photoUrlsJson.isEmpty()) emptyList() else photoUrlsJson.split("|"),
    location = Location(latitude, longitude, address),
    contactPhone = contactPhone,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun Alert.toEntity(pendingSync: Boolean = false): AlertEntity = AlertEntity(
    id = id,
    ownerId = ownerId,
    petId = petId,
    type = type.name,
    status = status.name,
    petType = petType.name,
    petName = petName,
    breed = breed,
    color = color,
    size = size,
    hasCollar = hasCollar,
    isCastrated = isCastrated,
    description = description,
    photoUrlsJson = photoUrls.joinToString("|"),
    latitude = location.latitude,
    longitude = location.longitude,
    address = location.address,
    contactPhone = contactPhone,
    createdAt = createdAt,
    updatedAt = updatedAt,
    pendingSync = pendingSync
)
