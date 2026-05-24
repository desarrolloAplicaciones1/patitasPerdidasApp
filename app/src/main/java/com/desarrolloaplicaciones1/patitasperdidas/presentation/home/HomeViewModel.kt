package com.desarrolloaplicaciones1.patitasperdidas.presentation.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.desarrolloaplicaciones1.patitasperdidas.PatitasPerdidasApplication
import com.desarrolloaplicaciones1.patitasperdidas.domain.model.AlertType
import com.desarrolloaplicaciones1.patitasperdidas.domain.model.PetType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val appContainer = (application as PatitasPerdidasApplication).appContainer

    private val getActiveAlertsUseCase = appContainer.getActiveAlertsUseCase
    private val getCurrentUserUseCase = appContainer.getCurrentUserUseCase

    private val _filterState = MutableStateFlow(HomeFilterState())
    val filterState: StateFlow<HomeFilterState> = _filterState.asStateFlow()

    val uiState: StateFlow<HomeUiState> = combine(
        getActiveAlertsUseCase(),
        _filterState,
        getCurrentUserUseCase()
    ) { alerts, filter, user ->
        val filtered = alerts.filter { alert ->
            (filter.petType == null || alert.petType == filter.petType) &&
                (filter.alertType == null || alert.type == filter.alertType)
        }
        HomeUiState.Success(
            alerts = filtered,
            currentUserName = user?.name
        ) as HomeUiState
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
