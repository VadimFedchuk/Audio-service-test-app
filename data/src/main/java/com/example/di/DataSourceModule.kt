package com.example.di

import com.example.dataSource.ExampleDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataSourceModule {

    @Provides
    @Singleton
    fun provideExampleDataSource(): ExampleDataSource {
        return ExampleDataSource()
    }
}