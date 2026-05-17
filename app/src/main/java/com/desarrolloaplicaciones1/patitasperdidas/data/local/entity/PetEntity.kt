package com.desarrolloaplicaciones1.patitasperdidas.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "pets",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["uid"],
            childColumns = ["ownerId"]
        )
    ]
)
data class PetEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(index = true) val ownerId: String,
    val name: String,
    val petType: String,
    val breed: String?,
    val color: String?,
    val description: String?,
    val photoUrlsJson: String,
    val microchipId: String?,
    val createdAt: Long
)
