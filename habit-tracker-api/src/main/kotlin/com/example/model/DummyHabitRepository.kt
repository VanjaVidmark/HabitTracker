package com.example.model

class DummyHabitRepository : HabitRepository {
    private val habits = mutableListOf<Habit>()

    override suspend fun getAllHabits(): List<Habit> {
        return habits
    }

    override suspend fun habitById(id: String): Habit? {
        // TODO: Lazily fetch all entries given their ID
        return habits.find { it.id == id }
    }

    override suspend fun addHabit(habit: Habit) {
        habits.add(habit)
    }

    override suspend fun removeHabit(id: String): Boolean {
        // TODO: if a habit is removed, so is all respective entries

        return habits.removeIf { it.id == id }
    }
    /*
    override suspend fun editHabit(habit: Habit, updateRequest: HabitUpdateRequest) {
        if (updateRequest.name != null) habit.name = updateRequest.name
        if (updateRequest.description != null) habit.description = updateRequest.description
        if (updateRequest.frequency != null) {
            require(Frequency.entries.toTypedArray().contains(updateRequest.frequency)) { "Invalid frequency" }
            habit.frequency = updateRequest.frequency
        }
        if (updateRequest.startDate != null) {habit.startDate = updateRequest.startDate
        }
    }*/

    override suspend fun addEntry(habit: Habit, entry: Entry) {
        habit.entries.add(entry)
    }
}


