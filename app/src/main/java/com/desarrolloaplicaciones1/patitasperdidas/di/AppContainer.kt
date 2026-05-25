package com.desarrolloaplicaciones1.patitasperdidas.di

import android.content.Context
import com.desarrolloaplicaciones1.patitasperdidas.data.local.AppDatabase
import com.desarrolloaplicaciones1.patitasperdidas.data.network.FirebaseAuthDataSource
import com.desarrolloaplicaciones1.patitasperdidas.data.network.FirestoreAlertDataSource
import com.desarrolloaplicaciones1.patitasperdidas.data.network.FirestoreUserDataSource
import com.desarrolloaplicaciones1.patitasperdidas.data.repository.AndroidGeocodingRepository
import com.desarrolloaplicaciones1.patitasperdidas.data.repository.AlertRepository as AlertRepositoryImpl
import com.desarrolloaplicaciones1.patitasperdidas.data.repository.FirebasePhotoStorageRepository
import com.desarrolloaplicaciones1.patitasperdidas.data.repository.PetRepository as PetRepositoryImpl
import com.desarrolloaplicaciones1.patitasperdidas.data.repository.PreferencesSettingsRepository
import com.desarrolloaplicaciones1.patitasperdidas.data.repository.UserRepository as UserRepositoryImpl
import com.desarrolloaplicaciones1.patitasperdidas.domain.repository.GeocodingRepository as GeocodingRepositoryContract
import com.desarrolloaplicaciones1.patitasperdidas.domain.repository.AlertRepository as AlertRepositoryContract
import com.desarrolloaplicaciones1.patitasperdidas.domain.repository.PetRepository as PetRepositoryContract
import com.desarrolloaplicaciones1.patitasperdidas.domain.repository.PhotoStorageRepository as PhotoStorageRepositoryContract
import com.desarrolloaplicaciones1.patitasperdidas.domain.repository.SettingsRepository as SettingsRepositoryContract
import com.desarrolloaplicaciones1.patitasperdidas.domain.repository.UserRepository as UserRepositoryContract
import com.desarrolloaplicaciones1.patitasperdidas.domain.usecase.alert.CreateAlertUseCase
import com.desarrolloaplicaciones1.patitasperdidas.domain.usecase.alert.DeleteAlertUseCase
import com.desarrolloaplicaciones1.patitasperdidas.domain.usecase.alert.GetActiveAlertsUseCase
import com.desarrolloaplicaciones1.patitasperdidas.domain.usecase.alert.GetAlertByIdUseCase
import com.desarrolloaplicaciones1.patitasperdidas.domain.usecase.alert.GetMyAlertsUseCase
import com.desarrolloaplicaciones1.patitasperdidas.domain.usecase.alert.ResolveAlertUseCase
import com.desarrolloaplicaciones1.patitasperdidas.domain.usecase.alert.SyncAlertsUseCase
import com.desarrolloaplicaciones1.patitasperdidas.domain.usecase.alert.UpdateAlertUseCase
import com.desarrolloaplicaciones1.patitasperdidas.domain.usecase.auth.GetCurrentUserIdUseCase
import com.desarrolloaplicaciones1.patitasperdidas.domain.usecase.auth.IsLoggedInUseCase
import com.desarrolloaplicaciones1.patitasperdidas.domain.usecase.auth.LoginUseCase
import com.desarrolloaplicaciones1.patitasperdidas.domain.usecase.auth.LogoutUseCase
import com.desarrolloaplicaciones1.patitasperdidas.domain.usecase.auth.RegisterUserUseCase
import com.desarrolloaplicaciones1.patitasperdidas.domain.usecase.location.GeocodeAddressUseCase
import com.desarrolloaplicaciones1.patitasperdidas.domain.usecase.media.UploadAlertPhotoUseCase
import com.desarrolloaplicaciones1.patitasperdidas.domain.usecase.pet.DeletePetUseCase
import com.desarrolloaplicaciones1.patitasperdidas.domain.usecase.pet.GetMyPetsUseCase
import com.desarrolloaplicaciones1.patitasperdidas.domain.usecase.pet.GetPetByIdUseCase
import com.desarrolloaplicaciones1.patitasperdidas.domain.usecase.pet.SavePetUseCase
import com.desarrolloaplicaciones1.patitasperdidas.domain.usecase.pet.UpdatePetUseCase
import com.desarrolloaplicaciones1.patitasperdidas.domain.usecase.settings.GetAppSettingsUseCase
import com.desarrolloaplicaciones1.patitasperdidas.domain.usecase.settings.SetAlertRadiusUseCase
import com.desarrolloaplicaciones1.patitasperdidas.domain.usecase.settings.SetDarkModeUseCase
import com.desarrolloaplicaciones1.patitasperdidas.domain.usecase.settings.SetOfflineModeUseCase
import com.desarrolloaplicaciones1.patitasperdidas.domain.usecase.user.ChangePasswordUseCase
import com.desarrolloaplicaciones1.patitasperdidas.domain.usecase.user.GetCurrentUserUseCase
import com.desarrolloaplicaciones1.patitasperdidas.domain.usecase.user.SyncCurrentUserProfileUseCase
import com.desarrolloaplicaciones1.patitasperdidas.domain.usecase.user.UpdateUserProfileUseCase

class AppContainer(context: Context) {
    private val database = AppDatabase.getInstance(context)

    private val authDataSource = FirebaseAuthDataSource()
    private val firestoreAlertDataSource = FirestoreAlertDataSource()
    private val firestoreUserDataSource = FirestoreUserDataSource()

    private val userRepository: UserRepositoryContract =
        UserRepositoryImpl(database.userDao(), authDataSource, firestoreUserDataSource)

    private val petRepository: PetRepositoryContract =
        PetRepositoryImpl(database.petDao())

    private val alertRepository: AlertRepositoryContract =
        AlertRepositoryImpl(database.alertDao(), firestoreAlertDataSource)

    private val settingsRepository: SettingsRepositoryContract =
        PreferencesSettingsRepository(context)

    private val geocodingRepository: GeocodingRepositoryContract =
        AndroidGeocodingRepository(context)

    private val photoStorageRepository: PhotoStorageRepositoryContract =
        FirebasePhotoStorageRepository()

    val getCurrentUserIdUseCase = GetCurrentUserIdUseCase(userRepository)
    val isLoggedInUseCase = IsLoggedInUseCase(userRepository)
    val loginUseCase = LoginUseCase(userRepository)
    val logoutUseCase = LogoutUseCase(userRepository)
    val registerUserUseCase = RegisterUserUseCase(userRepository)

    val getCurrentUserUseCase = GetCurrentUserUseCase(userRepository)
    val syncCurrentUserProfileUseCase = SyncCurrentUserProfileUseCase(userRepository)
    val updateUserProfileUseCase = UpdateUserProfileUseCase(userRepository)
    val changePasswordUseCase = ChangePasswordUseCase(userRepository)

    val getActiveAlertsUseCase = GetActiveAlertsUseCase(alertRepository)
    val getAlertByIdUseCase = GetAlertByIdUseCase(alertRepository)
    val getMyAlertsUseCase = GetMyAlertsUseCase(alertRepository, userRepository)
    val createAlertUseCase = CreateAlertUseCase(alertRepository)
    val updateAlertUseCase = UpdateAlertUseCase(alertRepository)
    val resolveAlertUseCase = ResolveAlertUseCase(alertRepository)
    val deleteAlertUseCase = DeleteAlertUseCase(alertRepository)
    val syncAlertsUseCase = SyncAlertsUseCase(alertRepository)

    val getMyPetsUseCase = GetMyPetsUseCase(petRepository, userRepository)
    val getPetByIdUseCase = GetPetByIdUseCase(petRepository)
    val savePetUseCase = SavePetUseCase(petRepository)
    val updatePetUseCase = UpdatePetUseCase(petRepository)
    val deletePetUseCase = DeletePetUseCase(petRepository)

    val getAppSettingsUseCase = GetAppSettingsUseCase(settingsRepository)
    val setDarkModeUseCase = SetDarkModeUseCase(settingsRepository)
    val setAlertRadiusUseCase = SetAlertRadiusUseCase(settingsRepository)
    val setOfflineModeUseCase = SetOfflineModeUseCase(settingsRepository)

    val geocodeAddressUseCase = GeocodeAddressUseCase(geocodingRepository)
    val uploadAlertPhotoUseCase = UploadAlertPhotoUseCase(photoStorageRepository)
}
