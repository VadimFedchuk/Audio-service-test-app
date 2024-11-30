package com.example.audiobooktestapplication.util

import com.example.audiobooktestapplication.model.BookFileUi
import com.example.model.BookFile
import com.example.utils.Resource

fun Resource<BookFile>.toPlayerScreenState(): PlayerScreenState {
    return when (this) {
        is Resource.Success -> {
            PlayerScreenState.Success(
                currentBook = this.data.toUiModel()
            )
        }
        is Resource.Error -> {
            PlayerScreenState.Error(
                errorMessage = this.message
            )
        }
        is Resource.Loading -> {
            PlayerScreenState.Loading(
                isLoading = true
            )
        }
    }
}

fun formatTime(seconds: Long): String {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return String.format("%02d:%02d", minutes, remainingSeconds)
}

private fun BookFile.toUiModel(): BookFileUi {
    return BookFileUi(
        fileName = fileName,
        posterName = posterName,
        duration = duration,
        chaptersTime = chaptersTime,
        currentTime = 0,
        isPlaying = false,
        playbackSpeed = 1f,
    )
}