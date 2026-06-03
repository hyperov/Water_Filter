package com.nabil.ahmed.waterfilter.di

import com.nabil.ahmed.waterfilter.data.repository.WaterFilterRepository
import com.nabil.ahmed.waterfilter.data.repository.WaterFilterRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindWaterFilterRepository(
        repositoryImpl: WaterFilterRepositoryImpl
    ): WaterFilterRepository
}
