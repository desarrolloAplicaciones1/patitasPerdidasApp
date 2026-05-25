package com.uade.huellitas.domain.usecase.pet

import com.uade.huellitas.domain.model.Pet
import com.uade.huellitas.domain.repository.PetRepository

class GetPetByIdUseCase(
    private val petRepository: PetRepository
) {
    suspend operator fun invoke(id: String): Pet? =
        petRepository.getById(id)
}
