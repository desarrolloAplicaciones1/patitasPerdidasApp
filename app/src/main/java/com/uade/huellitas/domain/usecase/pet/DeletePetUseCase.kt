package com.uade.huellitas.domain.usecase.pet

import com.uade.huellitas.domain.model.Pet
import com.uade.huellitas.domain.repository.PetRepository

class DeletePetUseCase(
    private val petRepository: PetRepository
) {
    suspend operator fun invoke(pet: Pet) =
        petRepository.deletePet(pet)
}
