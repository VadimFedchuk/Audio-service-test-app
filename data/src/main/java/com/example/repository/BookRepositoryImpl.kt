package com.example.repository

import com.example.dataSource.ExampleDataSource
import com.example.mapper.toBookFile
import com.example.model.BookFile
import javax.inject.Inject

class BookRepositoryImpl @Inject constructor(
    private val dataSource: ExampleDataSource,
): BookRepository {
    override suspend fun getFileName(): BookFile {
        val bookFileDto = dataSource.getFileName()
        val bookFile = bookFileDto.toBookFile()
        return bookFile
    }

    override suspend fun getFileNameWithError(): BookFile {
        throw Exception("Some message")
    }
}