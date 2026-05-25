package com.uade.huellitas.presentation.profile.alerts

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.uade.huellitas.HuellitasApplication
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class MyAlertsViewModel(application: Application) : AndroidViewModel(application) {

    private val getMyAlertsUseCase =
        (application as HuellitasApplication).appContainer.getMyAlertsUseCase

    val uiState: StateFlow<MyAlertsUiState> =
        getMyAlertsUseCase()
            .map { alerts -> MyAlertsUiState.Success(alerts) as MyAlertsUiState }
            .catch { e -> emit(MyAlertsUiState.Error(e.message ?: "Error al cargar tus avisos")) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = MyAlertsUiState.Loading
            )
}
