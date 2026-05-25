package com.desarrolloaplicaciones1.patitasperdidas.data.local

import android.content.Context
import androidx.room.migration.Migration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.desarrolloaplicaciones1.patitasperdidas.data.local.converter.Converters
import com.desarrolloaplicaciones1.patitasperdidas.data.local.dao.AlertDao
import com.desarrolloaplicaciones1.patitasperdidas.data.local.dao.PetDao
import com.desarrolloaplicaciones1.patitasperdidas.data.local.dao.UserDao
import com.desarrolloaplicaciones1.patitasperdidas.data.local.entity.AlertEntity
import com.desarrolloaplicaciones1.patitasperdidas.data.local.entity.PetEntity
import com.desarrolloaplicaciones1.patitasperdidas.data.local.entity.UserEntity

@Database(
    entities = [UserEntity::class, PetEntity::class, AlertEntity::class],
    version = 2,
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
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                    .also { INSTANCE = it }
            }

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE users ADD COLUMN location TEXT")
                db.execSQL("ALTER TABLE alerts ADD COLUMN size TEXT")
                db.execSQL("ALTER TABLE alerts ADD COLUMN hasCollar INTEGER")
                db.execSQL("ALTER TABLE alerts ADD COLUMN isCastrated INTEGER")
            }
        }
    }
}
