package com.desarrolloaplicaciones1.patitasperdidas.domain.usecase.pet

import com.desarrolloaplicaciones1.patitasperdidas.domain.model.Pet
import com.desarrolloaplicaciones1.patitasperdidas.domain.repository.PetRepository

class DeletePetUseCase(
    private val petRepository: PetRepository
) {
    suspend operator fun invoke(pet: Pet) =
        petRepository.deletePet(pet)
}
