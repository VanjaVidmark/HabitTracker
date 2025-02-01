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
fun DeleteHabitDialog(
    habitId: String,
    habitsViewModel: HabitsViewModel,
    onDismiss: () -> Unit,
    onBack: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete Habit") },
        text = {
            Column {
                Text("Are you sure you want to delete this habit?")
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    coroutineScope.launch {
                        habitsViewModel.deleteHabit(habitId)
                        onDismiss()
                        onBack()
                    }
                }
            ) {
                Text("Yes")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
