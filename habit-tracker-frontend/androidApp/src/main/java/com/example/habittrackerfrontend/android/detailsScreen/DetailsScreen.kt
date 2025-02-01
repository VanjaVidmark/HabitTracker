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
import com.example.habittrackerfrontend.habits.HabitsViewModel
import com.example.habittrackerfrontend.logMessage

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    habitId: String,
    habitsViewModel: HabitsViewModel,
    onBack: () -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var showDeleteHabitDialog by remember { mutableStateOf(false) }

    // find habit of given id
    val habitsState = habitsViewModel.habitsState.collectAsState()
    val habit = habitsState.value.habits.find { it.id == habitId }
    logMessage("andriod", "habit: $habit")

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
                FloatingActionButton(onClick = { showAddDialog = true }) {
                    Icon(Icons.Filled.Add, contentDescription = "Add Habit")
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()) {

                LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    items(habit.entries) { entry ->
                        EntryItem(entry)
                    }
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    item {
                        Button(
                            onClick = { showDeleteHabitDialog = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                        ) {
                            Text("Delete Habit")
                        }
                    }
                }
            }
        }

        // Show the Add Habit Dialog
        if (showAddDialog) {
            AddEntryDialog(
                habitId = habit.id,
                habitsViewModel = habitsViewModel,
                onDismiss = { showAddDialog = false }
            )
        }
        if (showDeleteHabitDialog) {
            DeleteHabitDialog(
                habitId = habit.id,
                habitsViewModel = habitsViewModel,
                onDismiss = { showDeleteHabitDialog = false },
                onBack = { onBack() }
            )
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
