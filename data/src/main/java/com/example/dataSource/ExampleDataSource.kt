package com.example.dataSource

import com.example.model.BookFileDTO
import kotlinx.coroutines.delay
import javax.inject.Inject

// it can be local or remote data source for receiving data

class ExampleDataSource @Inject constructor() {

    suspend fun getFileName(): BookFileDTO {
        // for example it is long operation
        delay(2000L)
        return BookFileDTO(
            fileName = "audiobook.mp3",
            chaptersTime = listOf(50_000, 120_000, 800_000, 1_250_000,), //just for this example
            posterName = "poster",
            duration = 2_650_000,
        )
    }
}