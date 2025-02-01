package com.example.habittrackerfrontend.android.detailsScreen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.habittrackerfrontend.android.utilities.ErrorMessage
import com.example.habittrackerfrontend.entries.Entry
import com.example.habittrackerfrontend.habits.Habit
import com.example.habittrackerfrontend.habits.HabitsViewModel

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    habitId: String,
    habitsViewModel: HabitsViewModel,
    onBack: () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    // find habit of given id
    val habitsState = habitsViewModel.habitsState.collectAsState()
    val habit = habitsState.value.habits.find { it.id == habitId }

    if (habit == null) {
        ErrorMessage("Habit not found")
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(habit.name) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(onClick = { showDialog = true }) {
                    Icon(Icons.Filled.Add, contentDescription = "Add Habit")
                }
            }
        ) { padding ->
            Column(modifier = Modifier.padding(padding).fillMaxSize()) {
                EntriesList(habit.entries)
            }
        }
        // Show the Add Habit Dialog
        if (showDialog) {
            AddEntryDialog(
                habitId = habit.id,
                habitsViewModel = habitsViewModel,
                onDismiss = { showDialog = false }
            )
        }
    }
}

@Composable
fun EntriesList(entries: List<Entry>) {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        items(entries) { entry ->
            EntryItem(entry)
        }
    }
}

@Composable
fun EntryItem(entry: Entry) {
    Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        Text(entry.timestamp.toString(), style = MaterialTheme.typography.bodySmall)
        Text(entry.note ?: "No notes", style = MaterialTheme.typography.bodyMedium)
        Divider(modifier = Modifier.padding(vertical = 4.dp))
    }
}
