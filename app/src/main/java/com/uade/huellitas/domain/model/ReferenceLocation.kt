package com.uade.huellitas.domain.model

data class ReferenceLocation(
    val label: String,
    val location: Location,
    val source: ReferenceLocationSource
)

enum class ReferenceLocationSource {
    CURRENT_DEVICE,
    USER_PROFILE,
    DEFAULT
}
