package com.example.habittrackerfrontend.android.detailsScreen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import com.example.habittrackerfrontend.android.utilities.DatePickerDialog
import com.example.habittrackerfrontend.habits.HabitsViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddEntryDialog(
    habitId: String,
    habitsViewModel: HabitsViewModel,
    onDismiss: () -> Unit
) {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    var newEntryTimestamp by remember { mutableStateOf(LocalDate.now().format(formatter)) }
    var newEntryNote by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Entry") },
        text = {
            Column {
                // DATE PICKER
                OutlinedTextField(
                    value = newEntryTimestamp,
                    onValueChange = {},
                    label = { Text("When?") },
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePicker = true }
                )

                if (showDatePicker) {
                    DatePickerDialog(
                        onDismissRequest = { showDatePicker = false },
                        onDateSelected = { date ->
                            newEntryTimestamp = date.format(formatter)
                            showDatePicker = false
                        }
                    )
                }

                OutlinedTextField(
                    value = newEntryNote,
                    onValueChange = { newEntryNote = it },
                    label = { Text("Optional Note") }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    coroutineScope.launch {
                        habitsViewModel.addEntry(
                            habitId,
                            newEntryTimestamp,
                            newEntryNote
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
