package com.uade.huellitas.presentation.profile.alerts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uade.huellitas.domain.usecase.alert.GetMyAlertsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class MyAlertsViewModel(
    private val getMyAlertsUseCase: GetMyAlertsUseCase
) : ViewModel() {

    private val _retryTrigger = MutableStateFlow(0)

    val uiState: StateFlow<MyAlertsUiState> = _retryTrigger
        .flatMapLatest {
            getMyAlertsUseCase()
                .map { alerts ->
                    if (alerts.isEmpty()) MyAlertsUiState.Empty
                    else MyAlertsUiState.Success(alerts)
                }
                .catch { e ->
                    emit(MyAlertsUiState.Error(e.message ?: "Error al cargar tus avisos"))
                }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = MyAlertsUiState.Loading
        )

    fun retry() {
        _retryTrigger.value++
    }
}
