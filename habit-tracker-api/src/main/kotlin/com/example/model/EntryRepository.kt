package com.example.model

object EntryRepository {
    private val entries = mutableListOf<Entry>()

    fun addEntry(entry: Entry) {
        entries.add(entry)
    }

    fun getHabitEntries(habitId: String): List<Entry> {
        return entries.filter { it.habitId == habitId }
    }
}


