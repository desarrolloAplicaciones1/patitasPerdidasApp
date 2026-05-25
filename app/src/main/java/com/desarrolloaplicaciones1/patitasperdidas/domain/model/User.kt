package com.desarrolloaplicaciones1.patitasperdidas.domain.model

data class User(
    val uid: String,
    val name: String,
    val email: String,
    val phone: String? = null,
    val avatarUrl: String? = null,
    val location: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
