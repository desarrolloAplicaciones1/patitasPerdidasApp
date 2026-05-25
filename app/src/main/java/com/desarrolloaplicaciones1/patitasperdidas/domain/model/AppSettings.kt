package com.desarrolloaplicaciones1.patitasperdidas.domain.model

data class AppSettings(
    val darkModeEnabled: Boolean = false,
    val alertRadiusKm: Int = 3,
    val offlineModeEnabled: Boolean = true
)
