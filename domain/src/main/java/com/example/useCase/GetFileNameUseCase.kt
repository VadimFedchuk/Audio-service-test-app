package com.example.useCase

import com.example.model.BookFile
import com.example.repository.BookRepository
import com.example.utils.Resource
import javax.inject.Inject

class GetFileNameUseCase @Inject constructor(
    private val repository: BookRepository
) {
    suspend operator fun invoke(): Resource<BookFile> {
        return try {
            val file = repository.getFileName()
            // call repository.getFileNameWithError to check Error state
            Resource.Success(file)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Unknown error")
        }
    }
}