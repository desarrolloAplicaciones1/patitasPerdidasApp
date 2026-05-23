package com.desarrolloaplicaciones1.patitasperdidas.domain.usecase.pet

import com.desarrolloaplicaciones1.patitasperdidas.domain.model.Pet
import com.desarrolloaplicaciones1.patitasperdidas.domain.repository.PetRepository

class GetPetByIdUseCase(
    private val petRepository: PetRepository
) {
    suspend operator fun invoke(id: String): Pet? =
        petRepository.getById(id)
}
