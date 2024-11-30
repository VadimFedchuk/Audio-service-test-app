package com.example.audiobooktestapplication.di

import android.content.Context
import com.example.audiobooktestapplication.util.AudioPlayerInteractor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AudioPlayerModule {

    @Provides
    @Singleton
    fun provideAudioPlayerInteractor(
        @ApplicationContext context: Context
    ): AudioPlayerInteractor {
        return AudioPlayerInteractor(context)
    }
}