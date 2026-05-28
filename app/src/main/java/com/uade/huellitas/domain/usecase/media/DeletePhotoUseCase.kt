package com.uade.huellitas.domain.usecase.media

import com.uade.huellitas.domain.repository.PhotoStorageRepository

class DeletePhotoUseCase(
    private val photoStorageRepository: PhotoStorageRepository
) {
    suspend operator fun invoke(remoteUrl: String) =
        photoStorageRepository.deletePhoto(remoteUrl)
}
