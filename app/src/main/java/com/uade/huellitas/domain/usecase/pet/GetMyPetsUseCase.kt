package com.uade.huellitas.domain.usecase.pet

import com.uade.huellitas.domain.model.Pet
import com.uade.huellitas.domain.repository.PetRepository
import com.uade.huellitas.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class GetMyPetsUseCase(
    private val petRepository: PetRepository,
    private val userRepository: UserRepository
) {
    operator fun invoke(): Flow<List<Pet>> =
        userRepository.currentUserId?.let(petRepository::getPetsByOwner) ?: flowOf(emptyList())
}
