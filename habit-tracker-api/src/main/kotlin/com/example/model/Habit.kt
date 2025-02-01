package com.example.model

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import java.util.UUID

enum class Frequency {
    Daily, Weekly, Monthly
}

@Serializable
data class Habit(
    val id: String = UUID.randomUUID().toString(),
    var name: String,
    var description: String? = null,  // optional
    var frequency: Frequency,
    var startDate: LocalDate,
    val entries: MutableList<Entry> = mutableListOf()
)

@Serializable
data class HabitUpdateRequest(
    val name: String? = null,
    val description: String? = null,
    val frequency: Frequency? = null,
    val startDate: LocalDate? = null
)
