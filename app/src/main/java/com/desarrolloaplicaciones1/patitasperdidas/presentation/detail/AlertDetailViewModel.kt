package com.desarrolloaplicaciones1.patitasperdidas.presentation.detail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.desarrolloaplicaciones1.patitasperdidas.PatitasPerdidasApplication
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AlertDetailViewModel(application: Application) : AndroidViewModel(application) {

    private val appContainer = (application as PatitasPerdidasApplication).appContainer
    private val getAlertByIdUseCase = appContainer.getAlertByIdUseCase
    private val resolveAlertUseCase = appContainer.resolveAlertUseCase
    private val deleteAlertUseCase = appContainer.deleteAlertUseCase

    private val _uiState = MutableStateFlow<AlertDetailUiState>(AlertDetailUiState.Loading)
    val uiState: StateFlow<AlertDetailUiState> = _uiState.asStateFlow()

    fun loadAlert(alertId: String) {
        viewModelScope.launch {
            _uiState.value = AlertDetailUiState.Loading
            try {
                val alert = getAlertByIdUseCase(alertId)
                if (alert != null) {
                    _uiState.value = AlertDetailUiState.Success(alert)
                } else {
                    _uiState.value = AlertDetailUiState.Error("Aviso no encontrado")
                }
            } catch (e: Exception) {
                _uiState.value = AlertDetailUiState.Error(e.message ?: "Error al cargar aviso")
            }
        }
    }

    fun resolveAlert() {
        val current = (_uiState.value as? AlertDetailUiState.Success)?.alert ?: return
        viewModelScope.launch {
            try {
                resolveAlertUseCase(current)
                _uiState.value = AlertDetailUiState.Resolved
            } catch (e: Exception) {
                _uiState.value = AlertDetailUiState.Error(e.message ?: "Error al resolver aviso")
            }
        }
    }

    fun deleteAlert() {
        val current = (_uiState.value as? AlertDetailUiState.Success)?.alert ?: return
        viewModelScope.launch {
            try {
                deleteAlertUseCase(current)
                _uiState.value = AlertDetailUiState.Deleted
            } catch (e: Exception) {
                _uiState.value = AlertDetailUiState.Error(e.message ?: "Error al eliminar aviso")
            }
        }
    }
}
