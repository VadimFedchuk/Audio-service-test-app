package com.example.audiobooktestapplication.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.audiobooktestapplication.R
import com.example.audiobooktestapplication.util.AudioPlayerInteractor
import com.example.audiobooktestapplication.util.PlayerEvent
import com.example.audiobooktestapplication.util.PlayerScreenState
import com.example.audiobooktestapplication.util.toPlayerScreenState
import com.example.useCase.GetFileNameUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val getFileNameUseCase: GetFileNameUseCase,
    private val audioPlayerInteractor: AudioPlayerInteractor,
): ViewModel() {

    private val _state = MutableStateFlow<PlayerScreenState>(PlayerScreenState.Loading())
    val state: StateFlow<PlayerScreenState> = _state

    init {
        loadFileName()
    }

    private fun loadFileName() {
        viewModelScope.launch {
            val data = getFileNameUseCase()
            _state.value = data.toPlayerScreenState()
            audioPlayerInteractor.initMediaPlayer(R.raw.audiobook)
        }
    }

    fun onEvent(event: PlayerEvent) {
        when (event) {
            is PlayerEvent.Seek -> {
                seekTo(event.timeInMillis)
            }

            PlayerEvent.PlayPause -> {
                togglePlayPause()
            }

            PlayerEvent.Previous -> {
                moveToPreviousChapter()
            }

            PlayerEvent.Next -> {
                moveToNextChapter()
            }

            PlayerEvent.Rewind -> {
                rewindBy(10000)
            }

            PlayerEvent.Forward -> {
                forwardBy(10000)
            }
            is PlayerEvent.ChangePlaybackSpeed -> {
                changePlaybackSpeed(event.speed)
            }
        }
    }

    private fun seekTo(timeInMillis: Long) {
        val currentState = state.value
        if (currentState is PlayerScreenState.Success) {
            _state.value = currentState.copy(
                currentBook = currentState.currentBook.copy(
                    currentTime = timeInMillis
                )
            )
            audioPlayerInteractor.seekTo(timeInMillis)
        }
    }

    private fun togglePlayPause() {
        val currentState = state.value
        if (currentState is PlayerScreenState.Success) {
            val isPlaying = currentState.currentBook.isPlaying
            _state.value = currentState.copy(
                currentBook = currentState.currentBook.copy(
                    isPlaying = !isPlaying
                )
            )
            if (isPlaying) {
                audioPlayerInteractor.pauseAudio()
            } else {
                audioPlayerInteractor.playAudio()
                observeCurrentPosition()
            }
        }
    }

    private fun moveToPreviousChapter() {
        val currentState = state.value
        if (currentState is PlayerScreenState.Success) {
            val chapters = currentState.currentBook.chaptersTime
            val currentTime = currentState.currentBook.currentTime

            val previousChapterIndex = chapters.indexOfLast { it < currentTime }
            if (previousChapterIndex >= 0) {
                val newTime = chapters[previousChapterIndex]
                _state.value = currentState.copy(
                    currentBook = currentState.currentBook.copy(
                        currentTime = newTime
                    )
                )
                audioPlayerInteractor.seekTo(newTime)
            }
        }
    }

    private fun moveToNextChapter() {
        val currentState = state.value
        if (currentState is PlayerScreenState.Success) {
            val chapters = currentState.currentBook.chaptersTime
            val currentTime = currentState.currentBook.currentTime

            val nextChapterIndex = chapters.indexOfFirst { it > currentTime }
            if (nextChapterIndex >= 0) {
                val newTime = chapters[nextChapterIndex]
                _state.value = currentState.copy(
                    currentBook = currentState.currentBook.copy(
                        currentTime = newTime
                    )
                )
                audioPlayerInteractor.seekTo(newTime)
            }
        }
    }

    private fun rewindBy(milliseconds: Long) {
        val currentState = state.value
        if (currentState is PlayerScreenState.Success) {
            val newTime = (currentState.currentBook.currentTime - milliseconds).coerceAtLeast(0L)
            _state.value = currentState.copy(
                currentBook = currentState.currentBook.copy(
                    currentTime = newTime
                )
            )
            audioPlayerInteractor.seekTo(newTime)
        }
    }

    private fun forwardBy(milliseconds: Long) {
        val currentState = state.value
        if (currentState is PlayerScreenState.Success) {
            val newTime = (currentState.currentBook.currentTime + milliseconds)
                .coerceAtMost(currentState.currentBook.duration)
            _state.value = currentState.copy(
                currentBook = currentState.currentBook.copy(
                    currentTime = newTime
                )
            )
            audioPlayerInteractor.seekTo(newTime)
        }
    }

    private fun changePlaybackSpeed(speed: Float) {
        val currentState = state.value
        if (currentState is PlayerScreenState.Success) {
            _state.value = currentState.copy(
                currentBook = currentState.currentBook.copy(
                    playbackSpeed = speed
                )
            )
            audioPlayerInteractor.changePlaybackSpeed(speed)
        }
    }

    private fun observeCurrentPosition() {
        viewModelScope.launch {
            audioPlayerInteractor.positionFlow.collect { position ->
                val currentState = state.value
                if (currentState is PlayerScreenState.Success) {
                    _state.value = currentState.copy(
                        currentBook = currentState.currentBook.copy(
                            currentTime = position
                        )
                    )
                }
            }
        }
    }

    fun closeService() {
        audioPlayerInteractor.stopAudio()
        audioPlayerInteractor.unbindService()
    }

    override fun onCleared() {
        closeService()
        super.onCleared()
    }
}