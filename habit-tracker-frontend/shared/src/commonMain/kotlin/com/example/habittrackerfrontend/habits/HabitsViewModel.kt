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
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.json.Json

class HabitsViewModel: BaseViewModel() {
    private val _habitsState: MutableStateFlow<HabitsState> = MutableStateFlow(HabitsState(loading = true))
    val habitsState: StateFlow<HabitsState> = _habitsState.asStateFlow()

    private val _message = MutableStateFlow("")
    val message: StateFlow<String> = _message.asStateFlow()

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
                val todays = fetched.filter { habit ->
                    habit.daysSinceDue >= 0
                }

                logMessage("Shared", "Fetched habits count: ${fetched.size}")
                todaysHabits = todays
                upcomingHabits = fetched - todays.toSet()

                // sets habits to display based on filterValue
                if (filterValue == "Today") _habitsState.value = HabitsState(habits = todaysHabits)
                else _habitsState.value = HabitsState(habits = upcomingHabits)

                updateMessage()

            } catch (e: Exception) {
                logMessage("Shared", "Error fetching habits: ${e.message}")
                _habitsState.value = HabitsState(error = "Failed to fetch habits")
            }
        }
    }

    private fun updateMessage() {
        val habitsAmount = _habitsState.value.habits.size
        _message.value = when {
            filterValue == "Today" && habitsAmount == 0 -> "Well done! You're all done for today 🎉"
            filterValue == "Today" && habitsAmount == 1 -> "Just one habit left, keep going!"
            filterValue == "Today" && habitsAmount > 1 -> "You have $habitsAmount habits left today, keep up the good work!"
            filterValue == "Upcoming" && habitsAmount == 0 -> "You have no upcoming habits, add some today!"
            filterValue == "Upcoming" && habitsAmount == 1 -> "Just one upcoming habit, keep going!"
            else -> "You have $habitsAmount upcoming habits, you are doing a great job!"
        }
    }

    fun getGreeting(): String {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        return when (now.hour) {
            in 5..11 -> "Good morning!"
            in 12..17 -> "Good afternoon!"
            in 18..22 -> "Good evening!"
            else -> "Good night!"
        }
    }

    fun getDueMessage(habit: Habit): String {
        return when  {
            habit.daysSinceDue == 0 -> "Today"
            habit.daysSinceDue == 1 -> "Due yesterday"
            habit.daysSinceDue > 2 -> "Due ${habit.daysSinceDue} days ago"
            habit.daysSinceDue == -1 -> "Due tomorrow"
            else -> "In ${habit.daysSinceDue * -1} days"
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