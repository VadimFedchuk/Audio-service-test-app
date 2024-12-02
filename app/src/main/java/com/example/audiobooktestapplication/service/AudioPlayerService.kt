package com.example.audiobooktestapplication.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
import android.media.MediaPlayer
import android.media.PlaybackParams
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.audiobooktestapplication.MainActivity
import com.example.audiobooktestapplication.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AudioPlayerService : Service() {

    private var mediaPlayer: MediaPlayer? = null
    private var playbackSpeed: Float = 1f

    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition: StateFlow<Long> = _currentPosition

    private val binder = LocalBinder()

    inner class LocalBinder : Binder() {
        fun getService(): AudioPlayerService = this@AudioPlayerService
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        when (action) {
            ACTION_INIT -> {
                val audioResId = intent.getIntExtra(EXTRA_AUDIO_RES_ID, -1)
                if (audioResId != -1) initMediaPlayer(audioResId)
            }
            ACTION_PLAY -> {
                playAudio()
            }
            ACTION_PAUSE -> pauseAudio()
            ACTION_SEEK_TO -> {
                val newTime = intent.getIntExtra(EXTRA_SEEK_POSITION, -1)
                if (newTime != -1) seekTo(newTime)
            }
            ACTION_CHANGE_SPEED -> changeSpeed(intent.getFloatExtra(EXTRA_SPEED, 1f))
            ACTION_STOP -> stopAudio()
        }

        return START_STICKY
    }

    private fun initMediaPlayer(audioResId: Int) {
        mediaPlayer = MediaPlayer.create(this, audioResId)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(NOTIFICATION_ID, createNotification(), FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK)
        } else {
            startForeground(NOTIFICATION_ID, createNotification())
        }
    }

    private fun playAudio() {
        mediaPlayer?.start()
        mediaPlayer?.setPlaybackSpeed(playbackSpeed)
        startTrackingCurrentPosition()
    }

    private fun seekTo(newTime: Int) {
        mediaPlayer?.let {
            if (newTime in 0..it.duration) {
                it.seekTo(newTime)
            }
        }
    }

    private fun startTrackingCurrentPosition() {
        CoroutineScope(Dispatchers.IO).launch {
            while (mediaPlayer?.isPlaying == true) {
                _currentPosition.value = mediaPlayer?.currentPosition?.toLong() ?: 0L
                delay(100)
            }
        }
    }

    private fun stopTrackingCurrentPosition() {
        _currentPosition.value = 0L
    }

    private fun changeSpeed(speed: Float) {
        playbackSpeed = speed
        mediaPlayer?.setPlaybackSpeed(speed)
    }

    private fun MediaPlayer.setPlaybackSpeed(speed: Float) {
        val playbackParams = PlaybackParams().apply {
            this.speed = speed
        }
        this.playbackParams = playbackParams
    }

    private fun pauseAudio() {
        mediaPlayer?.pause()
    }

    private fun stopAudio() {
        stopTrackingCurrentPosition()
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun createNotification(): Notification {
        val notificationChannelId = "audio_player_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                notificationChannelId,
                "Audio Player",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(notificationChannel)
        }

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0, // requestCode
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(this, notificationChannelId)
            .setContentTitle("Audio Player")
            .setContentText("Playing your audio")
            .setSmallIcon(R.drawable.ic_music_note)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setContentIntent(pendingIntent)

        return notificationBuilder.build()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    companion object {
        const val ACTION_INIT = "ACTION_INIT"
        const val ACTION_PLAY = "ACTION_PLAY"
        const val ACTION_PAUSE = "ACTION_PAUSE"
        const val ACTION_STOP = "ACTION_STOP"
        const val ACTION_SEEK_TO = "ACTION_SEEK_TO"
        const val ACTION_CHANGE_SPEED = "ACTION_CHANGE_SPEED"
        const val EXTRA_AUDIO_RES_ID = "EXTRA_AUDIO_RES_ID"
        const val EXTRA_SEEK_POSITION = "EXTRA_AUDIO_POSITION"
        const val EXTRA_SPEED = "EXTRA_SPEED"
        const val NOTIFICATION_ID = 1
    }
}
