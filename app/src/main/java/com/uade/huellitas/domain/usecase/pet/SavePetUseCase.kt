package com.uade.huellitas.domain.usecase.pet

import com.uade.huellitas.domain.model.Pet
import com.uade.huellitas.domain.repository.PetRepository

class SavePetUseCase(
    private val petRepository: PetRepository
) {
    suspend operator fun invoke(pet: Pet) =
        petRepository.savePet(pet)
}
