package com.uade.huellitas.presentation.profile.edit

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.uade.huellitas.HuellitasApplication
import com.uade.huellitas.domain.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class EditProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val appContainer = (application as HuellitasApplication).appContainer
    private val getCurrentUserUseCase = appContainer.getCurrentUserUseCase
    private val updateUserProfileUseCase = appContainer.updateUserProfileUseCase
    private val changePasswordUseCase = appContainer.changePasswordUseCase
    private val deletePhotoUseCase = appContainer.deletePhotoUseCase
    private val uploadProfilePhotoUseCase = appContainer.uploadProfilePhotoUseCase

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
                        phone = user.phone.orEmpty(),
                        location = user.location.orEmpty(),
                        avatarUrl = user.avatarUrl
                    )
                    _uiState.value = EditProfileUiState.Editing
                }
            }
        }
    }

    fun onNameChange(value: String) {
        _formState.value = _formState.value.copy(name = value)
    }

    fun onPhoneChange(value: String) {
        _formState.value = _formState.value.copy(phone = value)
    }

    fun onLocationChange(value: String) {
        _formState.value = _formState.value.copy(location = value)
    }

    fun onAvatarSelected(uri: String?) {
        _formState.value = _formState.value.copy(selectedAvatarUri = uri)
    }

    fun onPhotoSelected(uri: Uri) {
        val user = currentUser ?: return
        viewModelScope.launch {
            val previousAvatarUrl = user.avatarUrl
            try {
                _uiState.value = EditProfileUiState.Loading
                val downloadUrl = uploadProfilePhotoUseCase(user.uid, uri.toString())
                val updatedUser = user.copy(avatarUrl = downloadUrl)
                try {
                    updateUserProfileUseCase(updatedUser)
                } catch (e: Exception) {
                    rollbackUploadedAvatar(downloadUrl)
                    throw e
                }
                currentUser = updatedUser
                _formState.value = _formState.value.copy(
                    avatarUrl = downloadUrl,
                    selectedAvatarUri = null
                )
                cleanupPreviousAvatar(previousAvatarUrl, downloadUrl)
                _uiState.value = EditProfileUiState.Editing
            } catch (e: Exception) {
                _uiState.value = EditProfileUiState.Error(e.message ?: "Error al subir la foto")
            }
        }
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
                _uiState.value = EditProfileUiState.Error("La contraseña debe tener al menos 6 caracteres")
                return
            }
            if (form.password != form.confirmPassword) {
                _uiState.value = EditProfileUiState.Error("Las contraseñas no coinciden")
                return
            }
        }

        viewModelScope.launch {
            try {
                _uiState.value = EditProfileUiState.Loading
                val previousAvatarUrl = user.avatarUrl

                val uploadedAvatarUrl = form.selectedAvatarUri
                    ?.takeIf { it.isNotBlank() }
                    ?.let { selectedUri -> uploadProfilePhotoUseCase(user.uid, selectedUri) }
                val resolvedAvatarUrl = uploadedAvatarUrl ?: user.avatarUrl

                val updatedUser = user.copy(
                    name = form.name.trim(),
                    phone = form.phone.trim().ifBlank { null },
                    location = form.location.trim().ifBlank { null },
                    avatarUrl = resolvedAvatarUrl
                )

                try {
                    updateUserProfileUseCase(updatedUser)
                } catch (e: Exception) {
                    uploadedAvatarUrl?.let { rollbackUploadedAvatar(it) }
                    throw e
                }
                currentUser = updatedUser
                uploadedAvatarUrl?.let { cleanupPreviousAvatar(previousAvatarUrl, it) }

                if (form.password.isNotBlank()) {
                    changePasswordUseCase(form.password)
                }

                _formState.value = EditProfileFormState(
                    name = updatedUser.name,
                    email = updatedUser.email,
                    phone = updatedUser.phone.orEmpty(),
                    location = updatedUser.location.orEmpty(),
                    avatarUrl = updatedUser.avatarUrl
                )
                _uiState.value = EditProfileUiState.Success
            } catch (e: Exception) {
                _uiState.value = EditProfileUiState.Error(e.message ?: "Error al guardar el perfil")
            }
        }
    }

    fun consumeSuccess() {
        _uiState.value = EditProfileUiState.Editing
    }

    private suspend fun cleanupPreviousAvatar(previousAvatarUrl: String?, currentAvatarUrl: String) {
        val obsoleteAvatarUrl = previousAvatarUrl
            ?.takeIf { it.isNotBlank() && it != currentAvatarUrl }
            ?: return

        runCatching { deletePhotoUseCase(obsoleteAvatarUrl) }
            .onFailure { error ->
                Log.w(TAG, "No se pudo borrar el avatar anterior", error)
            }
    }

    private suspend fun rollbackUploadedAvatar(uploadedAvatarUrl: String) {
        runCatching { deletePhotoUseCase(uploadedAvatarUrl) }
            .onFailure { error ->
                Log.w(TAG, "No se pudo revertir el avatar subido tras un fallo de perfil", error)
            }
    }

    companion object {
        private const val TAG = "EditProfileViewModel"
    }
}

