package com.example.db

import com.example.model.Entry
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toKotlinLocalDate
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.javatime.date

object EntryTable : IdTable<String>("entries") {
    override val id = varchar("id", 255).entityId()
    val note = text("note").nullable()
    val timestamp = date("timestamp")

    val habitId = reference("habit_id", HabitTable.id)

    override val primaryKey = PrimaryKey(id)
}

// DAO = Data Access Object
class EntryDAO(id: EntityID<String>) : Entity<String>(id) {
    companion object : EntityClass<String, EntryDAO>(EntryTable)
    var note by EntryTable.note
    var timestamp by EntryTable.timestamp
    var habitId by EntryTable.habitId
}

fun entryDAOToModel(dao: EntryDAO) = Entry(
    id = dao.id.value,
    note = dao.note,
    timestamp = dao.timestamp.toKotlinLocalDate(),
)
