package com.uade.huellitas.presentation.profile.edit

sealed class EditProfileUiState {
    object Loading : EditProfileUiState()
    object Editing : EditProfileUiState()
    object Success : EditProfileUiState()
    data class Error(val message: String) : EditProfileUiState()
}

data class EditProfileFormState(
    val name: String = "",
    val email: String = "",
    val location: String = "",
    val password: String = "",
    val confirmPassword: String = ""
)
