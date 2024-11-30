package com.example.di

import com.example.dataSource.ExampleDataSource
import com.example.repository.BookRepository
import com.example.repository.BookRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideBookRepository(dataSource: ExampleDataSource): BookRepository {
        return BookRepositoryImpl(dataSource)
    }
}