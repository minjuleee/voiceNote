package com.example.voicenote.home

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Memo(
    val title: String,
    val summary: String,
    val dateTime: String,
    val rawText: String,
    val audioFilePath: String? = null
) : Parcelable
