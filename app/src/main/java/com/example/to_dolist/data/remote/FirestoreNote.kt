package com.example.to_dolist.data.remote

data class FirestoreNote(
    val id: String = "",
    val title: String = "",
    val content: String = "",
    val timestamp: Long = System.currentTimeMillis(),
)