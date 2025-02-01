package com.example

import com.example.model.DummyHabitRepository
import com.example.model.PostgresHabitRepository
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    val repository = PostgresHabitRepository()
    configureSerialization()
    configureDatabase()
    configureRouting(repository)
}
