package com.example.habittrackerfrontend.habits

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class HabitsService(
    private val httpClient: HttpClient
) {
    val BASE_URL = "http://10.0.2.2:8080"

    suspend fun fetchHabits(): List<Habit> {
        val response = httpClient.get("$BASE_URL/habits")
        return mapHabits(response.body())
    }

    private fun mapHabits(habitsRaw: List<HabitRaw>): List<Habit> {
        return habitsRaw.map { raw ->
            Habit(
                raw.id,
                raw.name,
                raw.description ?: "",
                raw.frequency,
                raw.startDate,
                raw.entries
            )
        }
    }
}