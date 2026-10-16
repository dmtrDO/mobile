package com.example.npp_radiation_monitor.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Головний клас бази даних Room (AppDatabase).
 * Реалізує шаблон Singleton для створення єдиного екземпляра бази даних.
 */
@Database(entities = [RadiationEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun radiationDao(): RadiationDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Отримує або створює екземпляр бази даних Room.
         */
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "npp_radiation_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
