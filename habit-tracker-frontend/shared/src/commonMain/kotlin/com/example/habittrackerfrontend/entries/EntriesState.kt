package com.example.habittrackerfrontend.entries

data class EntriesState (
    val entries: List<Entry> = listOf(),
    val loading: Boolean = false,
    val error: String? = null
)