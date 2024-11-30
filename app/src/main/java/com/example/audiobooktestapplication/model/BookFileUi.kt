package com.example.audiobooktestapplication.model

data class BookFileUi(
    val fileName: String,
    val chaptersTime: List<Long>,
    val posterName: String,
    val duration: Long,
    val currentTime: Long,
    val isPlaying: Boolean,
    val playbackSpeed: Float
)