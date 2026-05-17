package com.desarrolloaplicaciones1.patitasperdidas.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.desarrolloaplicaciones1.patitasperdidas.data.local.converter.Converters
import com.desarrolloaplicaciones1.patitasperdidas.data.local.dao.AlertDao
import com.desarrolloaplicaciones1.patitasperdidas.data.local.dao.PetDao
import com.desarrolloaplicaciones1.patitasperdidas.data.local.dao.UserDao
import com.desarrolloaplicaciones1.patitasperdidas.data.local.entity.AlertEntity
import com.desarrolloaplicaciones1.patitasperdidas.data.local.entity.PetEntity
import com.desarrolloaplicaciones1.patitasperdidas.data.local.entity.UserEntity

@Database(
    entities = [UserEntity::class, PetEntity::class, AlertEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun petDao(): PetDao
    abstract fun alertDao(): AlertDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "patitas_db"
                ).build().also { INSTANCE = it }
            }
    }
}
