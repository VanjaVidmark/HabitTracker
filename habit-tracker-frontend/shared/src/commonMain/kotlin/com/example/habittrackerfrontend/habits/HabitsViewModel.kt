package com.example.habittrackerfrontend.habits

import co.touchlab.skie.configuration.annotations.SuspendInterop
import com.example.habittrackerfrontend.BaseViewModel
import com.example.habittrackerfrontend.entries.Entry
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
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

    // like the method in the chatroom, i want to (whenever the habitsState variable is updated)
    // forward the new state object to the swift code
    // should i use just _habitsstate.value instead of emit? differnce?
    fun observeHabits(): Flow<HabitsState> {
        return habitsState
    }

    private fun getHabits() {
        scope.launch {
            val fetched = service.fetchHabits()
            // I am sending this new instance of the state class to the stateflow (w updated state)
            _habitsState.value = HabitsState(habits = fetched)
        }
    }

    @SuspendInterop.Enabled
    suspend fun addHabit(name: String, description: String, frequency: String, startDate: String) {
        scope.launch {
            val newHabit = Habit(
                id = (habitsState.value.habits.size + 1).toString(),
                name = name,
                description = description,
                frequency = frequency,
                startDate = startDate
            )
            _habitsState.value = habitsState.value.copy(
                habits = habitsState.value.habits + newHabit
            )
        }
    }

    @SuspendInterop.Enabled
    suspend fun addEntry(habitId: String, timestamp: String, note: String) {
        // SHOULD actually just call backend
        scope.launch {
            val newEntry = Entry(
                id = List(16) { Random.nextInt(0, 16).toString(16) }.joinToString(""),
                timestamp = timestamp,
                note = note
            )

            // Find the habit and update its entries
            val updatedHabit = habitsState.value.habits.map { habit ->
                if (habit.id == habitId) {
                    habit.copy(entries = habit.entries + newEntry)
                } else {
                    habit
                }
            }
            // Update state with new list containing the modified habit
            _habitsState.value = habitsState.value.copy(habits = updatedHabit)
        }
    }
}