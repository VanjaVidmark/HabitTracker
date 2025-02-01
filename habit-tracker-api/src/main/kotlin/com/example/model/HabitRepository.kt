package com.example.model

interface HabitRepository {
    suspend fun getAllHabits(): List<Habit>

    suspend fun habitById(id: String): Habit?

    suspend fun addHabit(habit: Habit)

    suspend fun removeHabit(id: String): Boolean

    // suspend fun editHabit(habit: Habit, updateRequest: HabitUpdateRequest)

    suspend fun addEntry(habit: Habit, entry: Entry)
}


