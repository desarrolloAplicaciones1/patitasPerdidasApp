package com.desarrolloaplicaciones1.patitasperdidas.presentation.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.desarrolloaplicaciones1.patitasperdidas.data.local.AppDatabase
import com.desarrolloaplicaciones1.patitasperdidas.data.network.FirebaseAuthDataSource
import com.desarrolloaplicaciones1.patitasperdidas.data.network.FirestoreAlertDataSource
import com.desarrolloaplicaciones1.patitasperdidas.data.repository.AlertRepository
import com.desarrolloaplicaciones1.patitasperdidas.data.repository.UserRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class MyAlertsViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)

    private val userRepository = UserRepository.getInstance(
        db.userDao(), FirebaseAuthDataSource()
    )
    private val alertRepository = AlertRepository.getInstance(
        db.alertDao(), FirestoreAlertDataSource()
    )

    val uiState: StateFlow<MyAlertsUiState> =
        alertRepository.getMyAlerts(userRepository.currentUserId ?: "")
            .map { alerts -> MyAlertsUiState.Success(alerts) as MyAlertsUiState }
            .catch { e -> emit(MyAlertsUiState.Error(e.message ?: "Error al cargar tus avisos")) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = MyAlertsUiState.Loading
            )
}
