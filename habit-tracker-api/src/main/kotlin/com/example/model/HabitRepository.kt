package com.example.model

import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

object HabitRepository {
    private val habits = mutableListOf(
        Habit(name = "cleaning", description = "Clean the house", frequency = Frequency.Daily, startDate = Instant.now().atZone(
            ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT)),
        Habit(name = "gardening", frequency = Frequency.Weekly, startDate = Instant.now().atZone(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT))
    )

    fun getAllHabits(): List<Habit> {
        return habits
    }

    fun habitById(id: String): Habit? {
        // TODO: Lazily fetch all entries given their ID
        return habits.find { it.id == id }
    }

    fun addHabit(habit: Habit) {
        habits.add(habit)
    }

    fun removeHabit(id: String): Boolean {
        // TODO: if a habit is removed, so is all respective entries

        return habits.removeIf { it.id == id }
    }

    fun editHabit(habit: Habit, updateRequest: HabitUpdateRequest) {
        if (updateRequest.name != null) habit.name = updateRequest.name
        if (updateRequest.description != null) habit.description = updateRequest.description
        if (updateRequest.frequency != null) {
            require(Frequency.entries.toTypedArray().contains(updateRequest.frequency)) { "Invalid frequency" }
            habit.frequency = updateRequest.frequency
        }
        if (updateRequest.startDate != null) {
            try {
                Instant.parse(updateRequest.startDate)
                habit.startDate = updateRequest.startDate
            } catch (e: DateTimeParseException) {
                throw IllegalArgumentException("Invalid date format: ${updateRequest.startDate}")
            }
        }
    }
}


