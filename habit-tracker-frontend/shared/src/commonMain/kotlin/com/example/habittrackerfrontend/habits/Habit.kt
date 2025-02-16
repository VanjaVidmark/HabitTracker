package com.example.habittrackerfrontend.habits

import com.example.habittrackerfrontend.entries.Entry
import com.example.habittrackerfrontend.entries.EntryRaw
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class Habit (
    val id: String,
    var name: String,
    var description: String,
    var frequency: String,
    var startDate: LocalDate,
    var entries: List<Entry> = listOf(),
    val daysSinceDue: Int // negative if due in the future
)

@Serializable
data class HabitAddRequest(
    val name: String,
    val description: String? = null,
    val frequency: String,
    val startDate: LocalDate
)

@Serializable
data class HabitRaw (
    val id: String,
    var name: String,
    var description: String?,
    var frequency: String,
    var startDate: LocalDate,
    var entries: List<EntryRaw> = listOf()
)