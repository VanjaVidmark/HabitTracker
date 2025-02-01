package com.example.habittrackerfrontend.entries

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class Entry (
    val id: String,
    val timestamp: LocalDate,
    val note: String
)

@Serializable
data class EntryRaw (
    val id: String,
    val timestamp: LocalDate,
    val note: String?
)

@Serializable
data class EntryAddRequest (
    val timestamp: LocalDate,
    val note: String? = null
)