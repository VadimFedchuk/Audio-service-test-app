package com.example.audiobooktestapplication.util

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.audiobooktestapplication.service.AudioPlayerService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class AudioPlayerInteractor @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val _serviceReady = MutableStateFlow(false)
    private val serviceReady: StateFlow<Boolean> = _serviceReady.asStateFlow()

    val positionFlow: Flow<Long> = serviceReady.flatMapLatest { ready ->
        if (ready) {
            audioPlayerService?.currentPosition ?: flowOf(0L)
        } else {
            flowOf(0L)
        }
    }

    private var audioPlayerService: AudioPlayerService? = null

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.i("TestDebug", "Service connected")
            val binder = service as? AudioPlayerService.LocalBinder
            audioPlayerService = binder?.getService()
            _serviceReady.value = true
            Log.i("TestDebug", "Service connected, audioPlayerService: $audioPlayerService")
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            audioPlayerService = null
            _serviceReady.value = false
        }
    }

    private fun bindService() {
        val intent = Intent(context, AudioPlayerService::class.java)
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    fun unbindService() {
        if (serviceReady.value) {
            context.unbindService(serviceConnection)
            _serviceReady.value = false
        }
    }

    fun playAudio() {
        val intent = Intent(context, AudioPlayerService::class.java).apply {
            action = AudioPlayerService.ACTION_PLAY
        }
        ContextCompat.startForegroundService(context, intent)
        bindService()
    }

    fun pauseAudio() {
        val intent = Intent(context, AudioPlayerService::class.java).apply {
            action = AudioPlayerService.ACTION_PAUSE
        }
        context.startService(intent)
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

    fun stopAudio() {
        val intent = Intent(context, AudioPlayerService::class.java).apply {
            action = AudioPlayerService.ACTION_STOP
        }
        context.startService(intent)
    }

    fun initMediaPlayer(audioResId: Int) {
        val intent = Intent(context, AudioPlayerService::class.java).apply {
            action = AudioPlayerService.ACTION_INIT
            putExtra(AudioPlayerService.EXTRA_AUDIO_RES_ID, audioResId)
        }
        context.startService(intent)
    }
}