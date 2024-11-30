package com.example.repository

import com.example.model.BookFile

interface BookRepository {

    suspend fun getFileName(): BookFile

    suspend fun getFileNameWithError(): BookFile
}