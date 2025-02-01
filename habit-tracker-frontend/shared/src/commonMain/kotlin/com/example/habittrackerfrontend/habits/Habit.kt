package com.example.habittrackerfrontend.habits

import com.example.habittrackerfrontend.entries.Entry
import com.example.habittrackerfrontend.entries.EntryRaw
import kotlinx.serialization.Serializable


@Serializable
data class Habit (
    val id: String,
    var name: String,
    var description: String,
    var frequency: String,
    var startDate: String,
    var entries: List<Entry> = listOf()
)

@Serializable
data class HabitAddRequest(
    val name: String,
    val description: String? = null,
    val frequency: String,
    val startDate: String
)

@Serializable
data class HabitRaw (
    val id: String,
    var name: String,
    var description: String?,
    var frequency: String,
    var startDate: String,
    var entries: List<EntryRaw> = listOf()
)