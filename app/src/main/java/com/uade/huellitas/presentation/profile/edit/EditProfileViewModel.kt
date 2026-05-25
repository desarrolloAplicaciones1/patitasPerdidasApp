package com.uade.huellitas.presentation.profile.edit

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.uade.huellitas.PatitasPerdidasApplication
import com.uade.huellitas.domain.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class EditProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val appContainer = (application as PatitasPerdidasApplication).appContainer
    private val getCurrentUserUseCase = appContainer.getCurrentUserUseCase
    private val updateUserProfileUseCase = appContainer.updateUserProfileUseCase
    private val changePasswordUseCase = appContainer.changePasswordUseCase

    private val _uiState = MutableStateFlow<EditProfileUiState>(EditProfileUiState.Loading)
    val uiState: StateFlow<EditProfileUiState> = _uiState.asStateFlow()

    private val _formState = MutableStateFlow(EditProfileFormState())
    val formState: StateFlow<EditProfileFormState> = _formState.asStateFlow()

    private var currentUser: User? = null

    init {
        viewModelScope.launch {
            getCurrentUserUseCase().collectLatest { user ->
                currentUser = user
                if (user == null) {
                    _uiState.value = EditProfileUiState.Error("No hay un usuario autenticado")
                } else {
                    _formState.value = EditProfileFormState(
                        name = user.name,
                        email = user.email,
                        location = user.location.orEmpty()
                    )
                    _uiState.value = EditProfileUiState.Editing
                }
            }
        }
    }

    fun onNameChange(value: String) {
        _formState.value = _formState.value.copy(name = value)
    }

    fun onLocationChange(value: String) {
        _formState.value = _formState.value.copy(location = value)
    }

    fun onPasswordChange(value: String) {
        _formState.value = _formState.value.copy(password = value)
    }

    fun onConfirmPasswordChange(value: String) {
        _formState.value = _formState.value.copy(confirmPassword = value)
    }

    fun saveProfile() {
        val form = _formState.value
        val user = currentUser

        if (user == null) {
            _uiState.value = EditProfileUiState.Error("No hay un usuario autenticado")
            return
        }
        if (form.name.isBlank()) {
            _uiState.value = EditProfileUiState.Error("El nombre es obligatorio")
            return
        }
        if (form.password.isNotBlank()) {
            if (form.password.length < 6) {
                _uiState.value = EditProfileUiState.Error("La contrasena debe tener al menos 6 caracteres")
                return
            }
            if (form.password != form.confirmPassword) {
                _uiState.value = EditProfileUiState.Error("Las contrasenas no coinciden")
                return
            }
        }

        viewModelScope.launch {
            try {
                _uiState.value = EditProfileUiState.Loading
                val updatedUser = user.copy(
                    name = form.name.trim(),
                    location = form.location.trim().ifBlank { null }
                )
                updateUserProfileUseCase(updatedUser)
                currentUser = updatedUser
                if (form.password.isNotBlank()) {
                    changePasswordUseCase(form.password)
                }
                _formState.value = _formState.value.copy(password = "", confirmPassword = "")
                _uiState.value = EditProfileUiState.Success
            } catch (e: Exception) {
                _uiState.value = EditProfileUiState.Error(e.message ?: "Error al guardar el perfil")
            }
        }
    }

    fun consumeSuccess() {
        _uiState.value = EditProfileUiState.Editing
    }
}
