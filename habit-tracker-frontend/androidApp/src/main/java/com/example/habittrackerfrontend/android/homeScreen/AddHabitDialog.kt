package com.example.habittrackerfrontend.android.homeScreen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.habittrackerfrontend.habits.HabitsViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import com.example.habittrackerfrontend.android.utilities.DatePickerDialog

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddHabitDialog(
    habitsViewModel: HabitsViewModel,
    onDismiss: () -> Unit
) {
    var newHabitName by remember { mutableStateOf("") }
    var newHabitDescription by remember { mutableStateOf("") }
    var newHabitFrequency by remember { mutableStateOf("Daily") }
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    var newHabitStartDate by remember { mutableStateOf(LocalDate.now().format(formatter)) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showFrequencyDropdown by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Habit") },
        text = {
            Column {
                OutlinedTextField(
                    value = newHabitName,
                    onValueChange = { newHabitName = it },
                    label = { Text("Habit Name") }
                )
                OutlinedTextField(
                    value = newHabitDescription,
                    onValueChange = { newHabitDescription = it },
                    label = { Text("Description") }
                )

                // Dropdown for frequency selection
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = newHabitFrequency,
                        onValueChange = {},
                        label = { Text("Frequency") },
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showFrequencyDropdown = true }
                    )
                    DropdownMenu(
                        expanded = showFrequencyDropdown,
                        onDismissRequest = { showFrequencyDropdown = false }
                    ) {
                        listOf("Daily", "Weekly", "Monthly").forEach { frequency ->
                            DropdownMenuItem(
                                text = { Text(frequency) },
                                onClick = {
                                    newHabitFrequency = frequency
                                    showFrequencyDropdown = false
                                }
                            )
                        }
                    }
                }

                // Date Picker
                OutlinedTextField(
                    value = newHabitStartDate,
                    onValueChange = {},
                    label = { Text("Start Date") },
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePicker = true }
                )

                if (showDatePicker) {
                    DatePickerDialog(
                        onDismissRequest = { showDatePicker = false },
                        onDateSelected = { date ->
                            newHabitStartDate = date.format(formatter)
                            showDatePicker = false
                        }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    coroutineScope.launch {
                        habitsViewModel.addHabit(
                            newHabitName,
                            newHabitDescription,
                            newHabitFrequency,
                            newHabitStartDate
                        )
                        onDismiss()
                    }
                }
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
