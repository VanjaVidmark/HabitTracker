package com.example.model

object EntryRepository {
    fun addEntry(habit: Habit, entry: Entry) {
        habit.entries.add(entry)
    }
}


