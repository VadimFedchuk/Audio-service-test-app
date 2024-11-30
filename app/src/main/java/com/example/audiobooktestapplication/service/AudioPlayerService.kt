package com.example.audiobooktestapplication.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
import android.media.MediaPlayer
import android.media.PlaybackParams
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.audiobooktestapplication.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AudioPlayerService : Service() {

    private var mediaPlayer: MediaPlayer? = null
    private var playbackSpeed: Float = 1f

    private val binder = LocalBinder()

    inner class LocalBinder : Binder() {
        fun getService(): AudioPlayerService = this@AudioPlayerService
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    fun getCurrentPosition(): Long {
        return mediaPlayer?.currentPosition?.toLong() ?: 0L
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        Log.i("TestDebug", "onStartCommand $action")
        when (action) {
            ACTION_PLAY -> {
                val audioResId = intent.getIntExtra(EXTRA_AUDIO_RES_ID, -1)
                if (audioResId != -1) playAudio(audioResId)
            }
            ACTION_PAUSE -> pauseAudio()
            ACTION_SEEK_TO -> {
                val newTime = intent.getIntExtra(EXTRA_SEEK_POSITION, -1)
                Log.i("TestDebug", "onStartCommand $newTime")
                if (newTime != -1) seekTo(newTime)
            }
            ACTION_CHANGE_SPEED -> changeSpeed(intent.getFloatExtra(EXTRA_SPEED, 1f))
        }

        return START_STICKY
    }

    private fun playAudio(audioResId: Int) {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(this, audioResId).apply {
                start()
                setPlaybackSpeed(playbackSpeed)
            }
        } else {
            mediaPlayer?.start()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(NOTIFICATION_ID, createNotification(), FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK)
        } else {
            startForeground(NOTIFICATION_ID, createNotification())
        }
    }

    private fun seekTo(newTime: Int) {
        mediaPlayer?.let {
            if (newTime in 0..it.duration) {
                it.seekTo(newTime)
            }
        }
    }

    private fun rewindAudio() {
        mediaPlayer?.let {
            val newTime = (it.currentPosition - REWIND_TIME).coerceAtLeast(0)
            it.seekTo(newTime)
        }
    }

    private fun forwardAudio() {
        mediaPlayer?.let {
            val newTime = (it.currentPosition + FORWARD_TIME).coerceAtMost(it.duration)
            it.seekTo(newTime)
        }
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

        val notificationBuilder = NotificationCompat.Builder(this, notificationChannelId)
            .setContentTitle("Audio Player")
            .setContentText("Playing your audio")
            .setSmallIcon(R.drawable.ic_music_note)
            .setPriority(NotificationCompat.PRIORITY_LOW)

        return notificationBuilder.build()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    companion object {
        const val ACTION_PLAY = "ACTION_PLAY"
        const val ACTION_PAUSE = "ACTION_PAUSE"
        const val ACTION_SEEK_TO = "ACTION_SEEK_TO"
        const val ACTION_CHANGE_SPEED = "ACTION_CHANGE_SPEED"
        const val EXTRA_AUDIO_RES_ID = "EXTRA_AUDIO_RES_ID"
        const val EXTRA_SEEK_POSITION = "EXTRA_AUDIO_POSITION"
        const val EXTRA_SPEED = "EXTRA_SPEED"

        const val REWIND_TIME = 10_000 // 10 seconds
        const val FORWARD_TIME = 10_000 // 10 seconds
        const val NOTIFICATION_ID = 1
    }
}