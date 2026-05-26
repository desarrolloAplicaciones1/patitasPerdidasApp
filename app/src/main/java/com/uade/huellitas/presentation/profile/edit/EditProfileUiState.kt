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
    val phone: String = "",
    val location: String = "",
    val avatarUrl: String? = null,
    val selectedAvatarUri: String? = null,
    val password: String = "",
    val confirmPassword: String = ""
) {
    val avatarPreview: String?
        get() = selectedAvatarUri ?: avatarUrl
}
