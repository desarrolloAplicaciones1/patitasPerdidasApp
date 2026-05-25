package com.uade.huellitas.domain.repository

import com.uade.huellitas.domain.model.Pet
import kotlinx.coroutines.flow.Flow

interface PetRepository {
    fun getPetsByOwner(ownerId: String): Flow<List<Pet>>
    suspend fun getById(id: String): Pet?
    suspend fun savePet(pet: Pet)
    suspend fun updatePet(pet: Pet)
    suspend fun deletePet(pet: Pet)
}
