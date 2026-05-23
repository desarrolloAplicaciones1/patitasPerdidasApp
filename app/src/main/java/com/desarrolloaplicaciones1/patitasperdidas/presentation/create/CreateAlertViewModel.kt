package com.desarrolloaplicaciones1.patitasperdidas.presentation.create

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.desarrolloaplicaciones1.patitasperdidas.PatitasPerdidasApplication
import com.desarrolloaplicaciones1.patitasperdidas.domain.model.Alert
import com.desarrolloaplicaciones1.patitasperdidas.domain.model.AlertStatus
import com.desarrolloaplicaciones1.patitasperdidas.domain.model.AlertType
import com.desarrolloaplicaciones1.patitasperdidas.domain.model.Location
import com.desarrolloaplicaciones1.patitasperdidas.domain.model.PetType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class CreateAlertViewModel(application: Application) : AndroidViewModel(application) {

    private val appContainer = (application as PatitasPerdidasApplication).appContainer
    private val createAlertUseCase = appContainer.createAlertUseCase
    private val getCurrentUserIdUseCase = appContainer.getCurrentUserIdUseCase

    private val _uiState = MutableStateFlow<CreateAlertUiState>(CreateAlertUiState.Idle)
    val uiState: StateFlow<CreateAlertUiState> = _uiState.asStateFlow()

    private val _formState = MutableStateFlow(CreateAlertFormState())
    val formState: StateFlow<CreateAlertFormState> = _formState.asStateFlow()

    fun onAlertTypeChange(type: AlertType) { _formState.value = _formState.value.copy(alertType = type) }
    fun onPetNameChange(value: String) { _formState.value = _formState.value.copy(petName = value) }
    fun onPetTypeChange(type: PetType) { _formState.value = _formState.value.copy(petType = type) }
    fun onBreedChange(value: String) { _formState.value = _formState.value.copy(breed = value) }
    fun onColorChange(value: String) { _formState.value = _formState.value.copy(color = value) }
    fun onDescriptionChange(value: String) { _formState.value = _formState.value.copy(description = value) }
    fun onContactPhoneChange(value: String) { _formState.value = _formState.value.copy(contactPhone = value) }
    fun onLocationChange(lat: Double, lng: Double, address: String) {
        _formState.value = _formState.value.copy(latitude = lat, longitude = lng, address = address)
    }

    fun submitAlert() {
        val form = _formState.value
        val ownerId = getCurrentUserIdUseCase() ?: run {
            _uiState.value = CreateAlertUiState.Error("No hay sesion activa")
            return
        }

        viewModelScope.launch {
            _uiState.value = CreateAlertUiState.Loading
            try {
                val now = System.currentTimeMillis()
                val alert = Alert(
                    id = UUID.randomUUID().toString(),
                    ownerId = ownerId,
                    type = form.alertType,
                    status = AlertStatus.ACTIVE,
                    petName = form.petName,
                    petType = form.petType,
                    breed = form.breed.ifBlank { null },
                    color = form.color.ifBlank { null },
                    description = form.description,
                    location = Location(
                        latitude = form.latitude ?: 0.0,
                        longitude = form.longitude ?: 0.0,
                        address = form.address.ifBlank { null }
                    ),
                    contactPhone = form.contactPhone.ifBlank { null },
                    createdAt = now,
                    updatedAt = now
                )
                createAlertUseCase(alert)
                _uiState.value = CreateAlertUiState.Success
            } catch (e: Exception) {
                _uiState.value = CreateAlertUiState.Error(e.message ?: "Error al publicar aviso")
            }
        }
    }

    fun resetState() {
        _uiState.value = CreateAlertUiState.Idle
    }
}
