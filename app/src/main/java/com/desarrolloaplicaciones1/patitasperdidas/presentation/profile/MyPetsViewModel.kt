package com.desarrolloaplicaciones1.patitasperdidas.presentation.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.desarrolloaplicaciones1.patitasperdidas.data.local.AppDatabase
import com.desarrolloaplicaciones1.patitasperdidas.data.network.FirebaseAuthDataSource
import com.desarrolloaplicaciones1.patitasperdidas.data.repository.PetRepository
import com.desarrolloaplicaciones1.patitasperdidas.data.repository.UserRepository
import com.desarrolloaplicaciones1.patitasperdidas.domain.model.Pet
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MyPetsViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)

    private val userRepository = UserRepository.getInstance(
        db.userDao(), FirebaseAuthDataSource()
    )
    private val petRepository = PetRepository.getInstance(db.petDao())

    val uiState: StateFlow<MyPetsUiState> =
        petRepository.getPetsByOwner(userRepository.currentUserId ?: "")
            .map { pets -> MyPetsUiState.Success(pets) as MyPetsUiState }
            .catch { e -> emit(MyPetsUiState.Error(e.message ?: "Error al cargar mascotas")) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = MyPetsUiState.Loading
            )

    fun deletePet(pet: Pet) {
        viewModelScope.launch {
            petRepository.deletePet(pet)
        }
    }
}
