package com.example.habittrackerfrontend.habits

import com.example.habittrackerfrontend.entries.Entry
import com.example.habittrackerfrontend.entries.EntryAddRequest
import com.example.habittrackerfrontend.entries.EntryRaw
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.toLocalDateTime

class HabitsUseCase (
    private val service: HabitsService
) {
    suspend fun getHabits(): List<Habit> = mapHabits(service.fetchHabits())
    suspend fun addHabit(habitRequest: HabitAddRequest) = service.addHabit(habitRequest)
    suspend fun deleteHabit(habitId: String) = service.deleteHabit(habitId)
    suspend fun addEntry(habitId: String, entryRequest: EntryAddRequest) = service.addEntry(habitId, entryRequest)
    suspend fun deleteEntry(entryId: String) = service.deleteEntry(entryId)

    private fun mapHabits(habitsRaw: List<HabitRaw>): List<Habit> {
        return habitsRaw.map { raw ->
            Habit(
                raw.id,
                raw.name,
                raw.description ?: "",
                raw.frequency,
                raw.startDate,
                mapEntries(raw.entries),
                calcDaysSinceDue(raw)
            )
        }
    }

    private fun mapEntries(entriesRaw: List<EntryRaw>): List<Entry> {
        return entriesRaw.map { raw ->
            Entry(
                raw.id,
                raw.timestamp,
                raw.note ?: ""
            )
        }
    }
    private fun calcDaysSinceDue(habitRaw: HabitRaw): Int {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val lastEntryDate = habitRaw.entries.maxByOrNull { it.timestamp }?.timestamp

        if (lastEntryDate == null) {
            // inga entries, den var alltså due för lika många dagar sedan som habiten skapades
            return habitRaw.startDate.daysUntil(today)
        }

        val daysSinceEntry = lastEntryDate.daysUntil(today)
        return when (habitRaw.frequency) {
            "Daily" -> daysSinceEntry - 1
            "Weekly" -> daysSinceEntry - 7
            "Monthly" -> daysSinceEntry - 30
            else -> 0
        }
    }

}