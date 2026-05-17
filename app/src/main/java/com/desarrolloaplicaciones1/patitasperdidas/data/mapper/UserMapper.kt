package com.desarrolloaplicaciones1.patitasperdidas.data.mapper

import com.desarrolloaplicaciones1.patitasperdidas.data.local.entity.UserEntity
import com.desarrolloaplicaciones1.patitasperdidas.domain.model.User

fun UserEntity.toDomain(): User = User(
    uid = uid,
    name = name,
    email = email,
    phone = phone,
    avatarUrl = avatarUrl,
    createdAt = createdAt
)

fun User.toEntity(): UserEntity = UserEntity(
    uid = uid,
    name = name,
    email = email,
    phone = phone,
    avatarUrl = avatarUrl,
    createdAt = createdAt
)
