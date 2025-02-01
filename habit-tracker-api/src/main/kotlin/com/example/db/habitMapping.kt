package com.example.db

import com.example.model.Frequency
import com.example.model.Habit
import kotlinx.coroutines.Dispatchers
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toKotlinLocalDate
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

// this file defines a kotlin exposed ORM

object HabitTable : IdTable<String>("habits") {
    override val id = varchar("id", 255).entityId()
    val name = varchar("name", 255)
    val description = text("description").nullable()
    val frequency = varchar("frequency", 50)
    val startDate = date("start_date")

    override val primaryKey = PrimaryKey(id)
}

// DAO = Data Access Object
class HabitDAO(id: EntityID<String>) : Entity<String>(id) {
    companion object : EntityClass<String, HabitDAO>(HabitTable)

    var name by HabitTable.name
    var description by HabitTable.description
    var frequency by HabitTable.frequency
    var startDate by HabitTable.startDate
    val entries by EntryDAO referrersOn EntryTable.habitId
}

suspend fun <T> suspendTransaction(block: Transaction.() -> T): T =
    newSuspendedTransaction(Dispatchers.IO, statement = block)

fun habitDAOToModel(dao: HabitDAO) = Habit(
    id = dao.id.value,
    name = dao.name,
    description = dao.description,
    frequency = Frequency.valueOf(dao.frequency),
    startDate = dao.startDate.toKotlinLocalDate(),
    entries = dao.entries.map { entryDAOToModel(it) }.toMutableList()  // auto fetch entries
)
