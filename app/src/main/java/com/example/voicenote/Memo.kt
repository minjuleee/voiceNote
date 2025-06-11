package com.example.voicenote.home

data class Memo(
    val documentId: String,
    val title: String,
    val summary: String,
    val dateTime: String,
    val rawText: String
)
