package com.example.habittrackerfrontend.habits

import com.example.habittrackerfrontend.entries.Entry
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