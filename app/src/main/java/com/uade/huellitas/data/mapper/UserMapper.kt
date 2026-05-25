package com.uade.huellitas.data.mapper

import com.uade.huellitas.data.local.entity.UserEntity
import com.uade.huellitas.domain.model.User

fun UserEntity.toDomain(): User = User(
    uid = uid,
    name = name,
    email = email,
    phone = phone,
    avatarUrl = avatarUrl,
    location = location,
    createdAt = createdAt
)

fun User.toEntity(): UserEntity = UserEntity(
    uid = uid,
    name = name,
    email = email,
    phone = phone,
    avatarUrl = avatarUrl,
    location = location,
    createdAt = createdAt
)
