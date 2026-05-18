package com.desarrolloaplicaciones1.patitasperdidas.presentation.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.desarrolloaplicaciones1.patitasperdidas.data.local.AppDatabase
import com.desarrolloaplicaciones1.patitasperdidas.data.network.FirestoreAlertDataSource
import com.desarrolloaplicaciones1.patitasperdidas.data.repository.AlertRepository
import com.desarrolloaplicaciones1.patitasperdidas.domain.model.AlertType
import com.desarrolloaplicaciones1.patitasperdidas.domain.model.PetType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val alertRepository = AlertRepository.getInstance(
        AppDatabase.getInstance(application).alertDao(),
        FirestoreAlertDataSource()
    )

    private val _filterState = MutableStateFlow(HomeFilterState())
    val filterState: StateFlow<HomeFilterState> = _filterState.asStateFlow()

    val uiState: StateFlow<HomeUiState> = alertRepository.getActiveAlerts()
        .map { alerts ->
            val filter = _filterState.value
            val filtered = alerts.filter { alert ->
                (filter.petType == null || alert.petType == filter.petType) &&
                (filter.alertType == null || alert.type == filter.alertType)
            }
            HomeUiState.Success(filtered) as HomeUiState
        }
        .catch { e -> emit(HomeUiState.Error(e.message ?: "Error al cargar avisos")) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HomeUiState.Loading
        )

    fun setFilter(petType: PetType? = null, alertType: AlertType? = null, radiusKm: Int = 10) {
        _filterState.value = HomeFilterState(petType, alertType, radiusKm)
    }

    fun clearFilter() {
        _filterState.value = HomeFilterState()
    }
}
