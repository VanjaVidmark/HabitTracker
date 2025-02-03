package com.example.habittrackerfrontend.habits

import com.example.habittrackerfrontend.Platform
import com.example.habittrackerfrontend.entries.Entry
import com.example.habittrackerfrontend.entries.EntryAddRequest
import com.example.habittrackerfrontend.entries.EntryRaw
import com.example.habittrackerfrontend.logMessage
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class HabitsService(
    private val httpClient: HttpClient
) {
    val BASE_URL = Platform.getBaseUrl()

    suspend fun fetchHabits(): List<HabitRaw> {
        try {
            val response = httpClient.get("$BASE_URL/habits")
            logMessage("Service", "received response: ${response.status}")
            return response.body()
        } catch (e: Exception) {
            logMessage("Service", "Error in fetchHabits: ${e.message}")
            return emptyList()
        }
    }

    suspend fun addHabit(habitRequest: HabitAddRequest) {
        try {
            httpClient.post("$BASE_URL/habits") {
                contentType(ContentType.Application.Json)
                setBody(habitRequest)
            }
        } catch (e: Exception) {
            println("Failed to add habit: ${e.message}")
            throw e
        }
    }

    suspend fun deleteHabit(habitId: String) {
        try {
            httpClient.delete("$BASE_URL/habits/$habitId")
        } catch (e: Exception) {
            println("Failed to add habit: ${e.message}")
            throw e
        }
    }

    suspend fun addEntry(habitId: String, entryRequest: EntryAddRequest) {
        try {
            httpClient.post("$BASE_URL/habits/$habitId/tracking") {
                contentType(ContentType.Application.Json)
                setBody(entryRequest)
            }
        } catch (e: Exception) {
            println("Failed to add entry: ${e.message}")
            throw e
        }
    }

    suspend fun deleteEntry(entryId: String) {
        try {
            httpClient.delete("$BASE_URL/habits/$entryId/tracking")
        } catch (e: Exception) {
            println("Failed to add habit: ${e.message}")
            throw e
        }
    }
}