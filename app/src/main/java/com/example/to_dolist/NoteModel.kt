package com.example.to_dolist


data class NoteModel(
    var title: String,
    var content: String,
    var isBookmarked: Boolean = false,
    var timestamp: Long = System.currentTimeMillis(),
)