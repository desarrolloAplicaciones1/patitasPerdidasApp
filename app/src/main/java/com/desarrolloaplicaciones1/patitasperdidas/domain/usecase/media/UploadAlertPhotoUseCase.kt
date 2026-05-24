package com.desarrolloaplicaciones1.patitasperdidas.domain.usecase.media

import com.desarrolloaplicaciones1.patitasperdidas.domain.repository.PhotoStorageRepository

class UploadAlertPhotoUseCase(
    private val photoStorageRepository: PhotoStorageRepository
) {
    suspend operator fun invoke(localUri: String) =
        photoStorageRepository.uploadAlertPhoto(localUri)
}
