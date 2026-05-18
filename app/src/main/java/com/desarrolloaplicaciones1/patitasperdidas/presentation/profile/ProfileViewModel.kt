package com.desarrolloaplicaciones1.patitasperdidas.presentation.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.desarrolloaplicaciones1.patitasperdidas.data.local.AppDatabase
import com.desarrolloaplicaciones1.patitasperdidas.data.network.FirebaseAuthDataSource
import com.desarrolloaplicaciones1.patitasperdidas.data.repository.UserRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val userRepository = UserRepository.getInstance(
        AppDatabase.getInstance(application).userDao(),
        FirebaseAuthDataSource()
    )

    val uiState: StateFlow<ProfileUiState> = userRepository
        .getUser(userRepository.currentUserId ?: "")
        .filterNotNull()
        .map { user -> ProfileUiState.Success(user) as ProfileUiState }
        .catch { e -> emit(ProfileUiState.Error(e.message ?: "Error al cargar perfil")) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ProfileUiState.Loading
        )

    fun logout() {
        userRepository.logout()
    }
}
