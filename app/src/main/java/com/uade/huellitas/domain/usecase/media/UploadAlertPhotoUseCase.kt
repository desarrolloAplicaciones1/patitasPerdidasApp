package com.uade.huellitas.domain.usecase.media

import com.uade.huellitas.domain.repository.PhotoStorageRepository

class UploadAlertPhotoUseCase(
    private val photoStorageRepository: PhotoStorageRepository
) {
    suspend operator fun invoke(localUri: String) =
        photoStorageRepository.uploadAlertPhoto(localUri)
}
