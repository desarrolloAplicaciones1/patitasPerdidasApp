package com.uade.huellitas.domain.usecase.media

import com.uade.huellitas.domain.repository.PhotoStorageRepository

class UploadProfilePhotoUseCase(
    private val photoStorageRepository: PhotoStorageRepository
) {
    suspend operator fun invoke(userId: String, localUri: String) =
        photoStorageRepository.uploadProfilePhoto(userId, localUri)
}
