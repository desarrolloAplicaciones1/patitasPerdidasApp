package com.desarrolloaplicaciones1.patitasperdidas.domain.model

data class Pet(
    val id: String,
    val ownerId: String,
    val name: String,
    val petType: PetType,
    val breed: String? = null,
    val color: String? = null,
    val description: String? = null,
    val photoUrls: List<String> = emptyList(),
    val microchipId: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
