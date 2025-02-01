package com.example

import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database

fun Application.configureDatabase() {
    Database.connect(
        url = "jdbc:postgresql://localhost:5432/habit-tracker-db",
        user = "vanjavidmark",
        password = ""
    )
}
