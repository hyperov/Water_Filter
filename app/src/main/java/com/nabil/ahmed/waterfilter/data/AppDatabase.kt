package com.nabil.ahmed.waterfilter.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.nabil.ahmed.waterfilter.data.dao.FilterDao
import com.nabil.ahmed.waterfilter.data.entity.Filter
import com.nabil.ahmed.waterfilter.data.entity.MaintenanceLog
import com.nabil.ahmed.waterfilter.data.entity.Stage
import com.nabil.ahmed.waterfilter.data.util.Converters

@Database(
    entities = [Filter::class, Stage::class, MaintenanceLog::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun filterDao(): FilterDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "water_filter_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
