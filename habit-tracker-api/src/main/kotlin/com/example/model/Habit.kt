package com.example.model

import kotlinx.serialization.Serializable
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
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
    var startDate: String,
    val entries: MutableList<Entry> = mutableListOf()
)

@Serializable
data class HabitUpdateRequest(
    val name: String? = null,
    val description: String? = null,
    val frequency: Frequency? = null,
    val startDate: String? = null
)
