package com.nabil.ahmed.waterfilter.di

import android.content.Context
import com.nabil.ahmed.waterfilter.data.AppDatabase
import com.nabil.ahmed.waterfilter.data.dao.FilterDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    @Provides
    fun provideFilterDao(database: AppDatabase): FilterDao {
        return database.filterDao()
    }
}
