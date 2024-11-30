package com.example.audiobooktestapplication.util

sealed class PlayerEvent {
    data class Seek(val timeInMillis: Long) : PlayerEvent()
    data object PlayPause : PlayerEvent()
    data object Previous : PlayerEvent()
    data object Next : PlayerEvent()
    data object Rewind : PlayerEvent()
    data object Forward : PlayerEvent()
    data class ChangePlaybackSpeed(val speed: Float) : PlayerEvent()
}