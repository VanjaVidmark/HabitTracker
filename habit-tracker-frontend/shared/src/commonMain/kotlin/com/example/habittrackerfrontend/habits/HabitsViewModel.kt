package com.example.habittrackerfrontend.habits

import co.touchlab.skie.configuration.annotations.SuspendInterop
import com.example.habittrackerfrontend.BaseViewModel
import com.example.habittrackerfrontend.entries.Entry
import com.example.habittrackerfrontend.entries.EntryAddRequest
import com.example.habittrackerfrontend.logMessage
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlin.random.Random

class HabitsViewModel: BaseViewModel() {
    // the list of values are kept at habitsState.value.habits
    private val _habitsState: MutableStateFlow<HabitsState> = MutableStateFlow(HabitsState(loading = true))
    val habitsState: StateFlow<HabitsState> = _habitsState.asStateFlow()

    private val _currentHabitsState = MutableStateFlow<List<HabitsState>>(emptyList())
    val currentHabitsState: StateFlow<List<HabitsState>> = _currentHabitsState.asStateFlow()

    private val service: HabitsService

    init {
        val httpClient = HttpClient{
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                })
            }
        }
        service = HabitsService(httpClient)
        getHabits()
    }

    fun observeHabits(): Flow<HabitsState> {
        return habitsState
    }

    private fun getHabits() {
        scope.launch {
            try {
                val fetched = service.fetchHabits()
                logMessage("Shared", "Fetched habits count: ${fetched.size}")
                _habitsState.value = HabitsState(habits = fetched)
            } catch (e: Exception) {
                logMessage("Shared", "Error fetching habits: ${e.message}")
                _habitsState.value = HabitsState(error = "Failed to fetch habits")
            }
        }
    }

    @SuspendInterop.Enabled
    suspend fun addHabit(name: String, description: String, frequency: String, startDate: String) {
        scope.launch {
            val newHabitRequest = HabitAddRequest(
                name = name,
                description = description.ifEmpty { null },
                frequency = frequency,
                startDate = startDate
            )
            // not sure this will trigger automatic reload of habits, maybe i manually have to set habitsState.value
            try {
                service.addHabit(newHabitRequest)
                getHabits()
            } catch (e: Exception) {
                println("Error adding habit: ${e.message}")
            }
        }
    }

    @SuspendInterop.Enabled
    suspend fun addEntry(habitId: String, timestamp: String, note: String) {
        scope.launch {
            val entryAddRequest = EntryAddRequest(
                timestamp = timestamp,
                note = note.ifEmpty { null }
            )
            try {
                service.addEntry(habitId , entryAddRequest)
                getHabits()
            } catch (e: Exception) {
                println("Error adding entry: ${e.message}")
            }
        }
    }
}