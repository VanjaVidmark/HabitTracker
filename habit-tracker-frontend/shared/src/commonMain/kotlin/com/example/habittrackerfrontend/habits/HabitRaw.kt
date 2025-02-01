package com.example.habittrackerfrontend.habits

import com.example.habittrackerfrontend.entries.Entry
import kotlinx.serialization.Serializable

// dont know if i need this classs, the only difference is that description is nullable...
@Serializable
data class HabitRaw (
    val id: String,
    var name: String,
    var description: String?,
    var frequency: String,
    var startDate: String,
    var entries: List<Entry> = listOf()
)