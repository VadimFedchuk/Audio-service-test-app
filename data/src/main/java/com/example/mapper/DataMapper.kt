package com.example.mapper

import com.example.model.BookFile
import com.example.model.BookFileDTO

fun BookFileDTO.toBookFile(): BookFile {
    return BookFile(
        fileName = this.fileName,
        chaptersTime = this.chaptersTime,
        posterName = this.posterName,
        duration = this.duration,
    )
}