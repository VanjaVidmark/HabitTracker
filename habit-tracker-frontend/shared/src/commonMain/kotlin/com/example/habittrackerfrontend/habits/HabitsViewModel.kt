package com.example.habittrackerfrontend.habits

import co.touchlab.skie.configuration.annotations.SuspendInterop
import com.example.habittrackerfrontend.BaseViewModel
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
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.json.Json

class HabitsViewModel: BaseViewModel() {
    private val _habitsState: MutableStateFlow<HabitsState> = MutableStateFlow(HabitsState(loading = true))
    val habitsState: StateFlow<HabitsState> = _habitsState.asStateFlow()

    private var todaysHabits: List<Habit> = listOf()
    private var upcomingHabits: List<Habit> = listOf()

    private val service: HabitsService
    private val useCase: HabitsUseCase
    var filterValue: String = "Today"

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
        useCase = HabitsUseCase(service)
        getHabits()
    }

    fun observeHabits(): Flow<HabitsState> {
        return habitsState
    }

    fun editFilter(filter: String) {
        if (filter !== filterValue) {
            filterValue = filter
            getHabits()
        }
    }

    private fun getHabits() {
        scope.launch {
            try {
                val fetched = useCase.getHabits()
                val todays = filterTodaysHabits(fetched)

                logMessage("Shared", "Fetched habits count: ${fetched.size}")
                todaysHabits = todays
                upcomingHabits = fetched - todays.toSet()

                // sets habits to display based on filterValue
                if (filterValue == "Today") _habitsState.value = HabitsState(habits = todaysHabits)
                else _habitsState.value = HabitsState(habits = upcomingHabits)

            } catch (e: Exception) {
                logMessage("Shared", "Error fetching habits: ${e.message}")
                _habitsState.value = HabitsState(error = "Failed to fetch habits")
            }
        }
    }

    private fun filterTodaysHabits(habits: List<Habit>): List<Habit> {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

        return habits.filter { habit ->
            val lastEntryDate = habit.entries.maxByOrNull { it.timestamp }?.timestamp ?: habit.startDate
            val daysAgo = lastEntryDate.daysUntil(today)
            logMessage("viewmodel", "lastEntryDate: $lastEntryDate")
            when (habit.frequency) {
                "Daily" -> daysAgo >= 1
                "Weekly" -> daysAgo >= 7
                "Monthly" -> daysAgo >= 30
                else -> false
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
                startDate = LocalDate.parse(startDate)
            )
            // not sure this will trigger automatic reload of habits, maybe i manually have to set habitsState.value
            try {
                useCase.addHabit(newHabitRequest)
                getHabits()
            } catch (e: Exception) {
                println("Error adding habit: ${e.message}")
            }
        }
    }

    @SuspendInterop.Enabled
    suspend fun deleteHabit(habitId: String) {
        scope.launch {
            try {
                useCase.deleteHabit(habitId)
                getHabits()
            } catch (e: Exception) {
                println("Error deleting habit: ${e.message}")
            }
        }
    }

    @SuspendInterop.Enabled
    suspend fun addEntry(habitId: String, timestamp: String, note: String) {
        scope.launch {
            val entryAddRequest = EntryAddRequest(
                timestamp = LocalDate.parse(timestamp),
                note = note.ifEmpty { null }
            )
            try {
                useCase.addEntry(habitId , entryAddRequest)
                getHabits()
            } catch (e: Exception) {
                println("Error adding entry: ${e.message}")
            }
        }
    }

    @SuspendInterop.Enabled
    suspend fun deleteEntry(entryId: String) {
        scope.launch {
            try {
                useCase.deleteEntry(entryId)
                getHabits()
            } catch (e: Exception) {
                println("Error deleting entry: ${e.message}")
            }
        }
    }
}