package com.example.habittrackerfrontend.entries

import kotlinx.serialization.Serializable

@Serializable
data class Entry (
    val id: String,
    val timestamp: String,
    val note: String
)