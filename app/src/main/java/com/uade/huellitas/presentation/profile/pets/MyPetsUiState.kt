package com.uade.huellitas.presentation.profile.pets

import com.uade.huellitas.domain.model.Pet

sealed class MyPetsUiState {
    object Loading : MyPetsUiState()
    object Empty : MyPetsUiState()
    data class Success(val pets: List<Pet>) : MyPetsUiState()
    data class Error(val message: String) : MyPetsUiState()
}
