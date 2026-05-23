package com.desarrolloaplicaciones1.patitasperdidas.domain.usecase.pet

import com.desarrolloaplicaciones1.patitasperdidas.domain.model.Pet
import com.desarrolloaplicaciones1.patitasperdidas.domain.repository.PetRepository
import com.desarrolloaplicaciones1.patitasperdidas.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class GetMyPetsUseCase(
    private val petRepository: PetRepository,
    private val userRepository: UserRepository
) {
    operator fun invoke(): Flow<List<Pet>> =
        userRepository.currentUserId?.let(petRepository::getPetsByOwner) ?: flowOf(emptyList())
}
