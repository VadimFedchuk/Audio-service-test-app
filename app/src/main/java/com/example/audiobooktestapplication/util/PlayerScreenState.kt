package com.example.audiobooktestapplication.util

import com.example.audiobooktestapplication.model.BookFileUi

sealed class PlayerScreenState {
    data class Loading(val isLoading: Boolean = false): PlayerScreenState()
    data class Success(val currentBook: BookFileUi): PlayerScreenState()
    data class Error(val errorMessage: String): PlayerScreenState()
}