package com.desarrolloaplicaciones1.patitasperdidas.data.mapper

import com.desarrolloaplicaciones1.patitasperdidas.data.local.entity.PetEntity
import com.desarrolloaplicaciones1.patitasperdidas.domain.model.Pet
import com.desarrolloaplicaciones1.patitasperdidas.domain.model.PetType

fun PetEntity.toDomain(): Pet = Pet(
    id = id,
    ownerId = ownerId,
    name = name,
    petType = PetType.valueOf(petType),
    breed = breed,
    color = color,
    description = description,
    photoUrls = if (photoUrlsJson.isEmpty()) emptyList() else photoUrlsJson.split("|"),
    microchipId = microchipId,
    createdAt = createdAt
)

fun Pet.toEntity(): PetEntity = PetEntity(
    id = id,
    ownerId = ownerId,
    name = name,
    petType = petType.name,
    breed = breed,
    color = color,
    description = description,
    photoUrlsJson = photoUrls.joinToString("|"),
    microchipId = microchipId,
    createdAt = createdAt
)
