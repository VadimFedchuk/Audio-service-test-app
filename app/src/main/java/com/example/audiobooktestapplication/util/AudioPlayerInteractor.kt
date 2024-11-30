package com.example.audiobooktestapplication.util

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.audiobooktestapplication.service.AudioPlayerService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class AudioPlayerInteractor @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private var audioPlayerService: AudioPlayerService? = null
    private var isBound = false

    private val _currentPosition = MutableStateFlow<Long>(0L)
    val currentPosition: StateFlow<Long> = _currentPosition

    fun setCurrentPosition(position: Long) {
        _currentPosition.value = position
    }

    fun playAudio(audioResId: Int) {
        val intent = Intent(context, AudioPlayerService::class.java).apply {
            action = AudioPlayerService.ACTION_PLAY
            putExtra(AudioPlayerService.EXTRA_AUDIO_RES_ID, audioResId)
        }
        ContextCompat.startForegroundService(context, intent)
        Log.i("TestDebug", "play 1")
    }

    fun pauseAudio() {
        val intent = Intent(context, AudioPlayerService::class.java).apply {
            action = AudioPlayerService.ACTION_PAUSE
        }
        context.startService(intent)
        Log.i("TestDebug", "play 2")
    }

    fun seekTo(time: Long) {
        val intent = Intent(context, AudioPlayerService::class.java).apply {
            action = AudioPlayerService.ACTION_SEEK_TO
            putExtra(AudioPlayerService.EXTRA_SEEK_POSITION, time.toInt())
        }
        context.startService(intent)
    }

    fun changePlaybackSpeed(speed: Float) {
        val intent = Intent(context, AudioPlayerService::class.java).apply {
            action = AudioPlayerService.ACTION_CHANGE_SPEED
            putExtra(AudioPlayerService.EXTRA_SPEED, speed)
        }
        context.startService(intent)
    }
}