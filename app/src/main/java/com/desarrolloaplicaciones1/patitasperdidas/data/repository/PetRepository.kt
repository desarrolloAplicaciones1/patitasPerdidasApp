package com.desarrolloaplicaciones1.patitasperdidas.data.repository

import com.desarrolloaplicaciones1.patitasperdidas.data.local.dao.PetDao
import com.desarrolloaplicaciones1.patitasperdidas.data.mapper.toDomain
import com.desarrolloaplicaciones1.patitasperdidas.data.mapper.toEntity
import com.desarrolloaplicaciones1.patitasperdidas.domain.model.Pet
import com.desarrolloaplicaciones1.patitasperdidas.domain.repository.PetRepository as PetRepositoryContract
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PetRepository(private val petDao: PetDao) : PetRepositoryContract {
    override fun getPetsByOwner(ownerId: String): Flow<List<Pet>> =
        petDao.getPetsByOwner(ownerId).map { it.map { entity -> entity.toDomain() } }

    override suspend fun getById(id: String): Pet? = petDao.getById(id)?.toDomain()

    override suspend fun savePet(pet: Pet) = petDao.insert(pet.toEntity())

    override suspend fun updatePet(pet: Pet) = petDao.update(pet.toEntity())

    override suspend fun deletePet(pet: Pet) = petDao.delete(pet.toEntity())

    companion object {
        @Volatile private var INSTANCE: PetRepository? = null

        fun getInstance(petDao: PetDao): PetRepository =
            INSTANCE ?: synchronized(this) {
                PetRepository(petDao).also { INSTANCE = it }
            }
    }
}
