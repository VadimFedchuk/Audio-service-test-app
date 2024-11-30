package com.example.model

data class BookFileDTO(
    val fileName: String,
    val chaptersTime: List<Long>,
    val posterName: String,
    val duration: Long,
)
