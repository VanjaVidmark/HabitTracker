package com.example.habittrackerfrontend.habits

data class HabitsState (
    val habits: List<Habit> = listOf(),
    val loading: Boolean = false,
    val error: String? = null
)