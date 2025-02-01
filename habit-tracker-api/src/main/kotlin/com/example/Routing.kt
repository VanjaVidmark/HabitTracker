package com.example

import com.example.model.*
import com.example.model.HabitRepository
import io.ktor.http.*
import io.ktor.serialization.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.http.content.staticResources
import io.ktor.server.request.*
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter


fun Application.configureRouting() {
    routing {
        staticResources("static", "static")

        route("/habits") {
            get {
                call.respond(HabitRepository.getAllHabits())
                return@get
            }

            post {
                try {
                    val habit = call.receive<Habit>()
                    HabitRepository.addHabit(habit)
                    call.respond(HttpStatusCode.NoContent)
                } catch (ex: IllegalStateException) {
                    call.respond(HttpStatusCode.BadRequest)
                } catch (ex: JsonConvertException) {
                    call.respond(HttpStatusCode.BadRequest)
                }
            }

            route("/{habitId}") {
                get {
                    // get habit with a given id
                    val id = call.parameters["habitId"]
                    if (id == null) {
                        call.respond(HttpStatusCode.BadRequest, "Missing habitId")
                        return@get
                    }
                    val habit = HabitRepository.habitById(id)
                    if (habit == null) {
                        call.respond(HttpStatusCode.NotFound, "Habit not found")
                        return@get
                    }
                    call.respond(habit)
                }
                delete {
                    val id = call.parameters["habitId"]
                    if (id == null) {
                        call.respond(HttpStatusCode.BadRequest, "Missing habitId")
                        return@delete
                    }

                    if (HabitRepository.removeHabit(id)) {
                        call.respond(HttpStatusCode.NoContent)
                    } else {
                        call.respond(HttpStatusCode.NotFound)
                    }
                }
                patch {
                    val id = call.parameters["habitId"]
                    if (id == null) {
                        call.respond(HttpStatusCode.BadRequest, "Missing habitId")
                        return@patch
                    }
                    val habit = HabitRepository.habitById(id)
                    if (habit == null) {
                        call.respond(HttpStatusCode.NotFound, "Habit not found")
                        return@patch
                    }
                    try {
                        val updateRequest = call.receive<HabitUpdateRequest>()
                        HabitRepository.editHabit(habit, updateRequest)
                        call.respond(HttpStatusCode.NoContent)
                    } catch (ex: IllegalStateException) {
                        call.respond(HttpStatusCode.BadRequest)
                    } catch (ex: JsonConvertException) {
                        call.respond(HttpStatusCode.BadRequest)
                    }
                }

                route("/tracking") {
                    post {
                        val id = call.parameters["habitId"]
                        if (id == null) {
                            call.respond(HttpStatusCode.BadRequest, "Missing habitId")
                            return@post
                        }
                        val habit = HabitRepository.habitById(id)
                        if (habit == null) {
                            call.respond(HttpStatusCode.NotFound, "Habit not found")
                            return@post
                        }
                        try {
                            val entry = call.receive<Entry>()
                            EntryRepository.addEntry(habit, entry)
                            call.respond(HttpStatusCode.NoContent)
                        } catch (ex: IllegalStateException) {
                            call.respond(HttpStatusCode.BadRequest)
                        } catch (ex: JsonConvertException) {
                            call.respond(HttpStatusCode.BadRequest)
                        }
                    }
                }

            }
        }
    }
}
