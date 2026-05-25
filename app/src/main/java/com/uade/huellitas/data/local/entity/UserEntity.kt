package com.uade.huellitas.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val uid: String,
    val name: String,
    val email: String,
    val phone: String?,
    val avatarUrl: String?,
    val location: String?,
    val createdAt: Long
)
