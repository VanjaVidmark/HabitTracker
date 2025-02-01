package com.example.model

import com.example.db.*
import kotlinx.datetime.toJavaLocalDate
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq


class PostgresHabitRepository : HabitRepository {

    override suspend fun getAllHabits(): List<Habit> = suspendTransaction {
        HabitDAO.all().map(::habitDAOToModel)
    }

    override suspend fun habitById(id: String): Habit? = suspendTransaction {
        HabitDAO
            .find { HabitTable.id eq id } // Ensure ID is treated as UUID
            .limit(1)
            .map(::habitDAOToModel)
            .firstOrNull()
    }

    override suspend fun addHabit(habit: Habit): Unit = suspendTransaction {
        HabitDAO.new(habit.id) {
            name = habit.name
            description = habit.description
            frequency = habit.frequency.name
            startDate = habit.startDate.toJavaLocalDate()
        }
    }

    override suspend fun removeHabit(id: String): Boolean = suspendTransaction {
        val rowsDeleted = HabitTable.deleteWhere { HabitTable.id eq id }
        rowsDeleted == 1
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


    override suspend fun addEntry(habit: Habit, entry: Entry): Unit = suspendTransaction {
        EntryDAO.new(entry.id) {
            timestamp = entry.timestamp.toJavaLocalDate()
            note = entry.note
            habitId = EntityID(habit.id, HabitTable)
        }
    }
}


