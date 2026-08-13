package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.AttendanceDao
import com.example.data.dao.FactoryConfigDao
import com.example.data.dao.PermitDao
import com.example.data.model.AttendanceRecord
import com.example.data.model.FactoryConfig
import com.example.data.model.PermitRequest

@Database(
    entities = [AttendanceRecord::class, PermitRequest::class, FactoryConfig::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun attendanceDao(): AttendanceDao
    abstract fun permitDao(): PermitDao
    abstract fun factoryConfigDao(): FactoryConfigDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "absensi_pabrik_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
