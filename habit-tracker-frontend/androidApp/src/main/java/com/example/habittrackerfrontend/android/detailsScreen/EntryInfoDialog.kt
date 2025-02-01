package com.example.habittrackerfrontend.android.detailsScreen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import kotlinx.coroutines.launch
import com.example.habittrackerfrontend.habits.HabitsViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DeleteEntryDialog(
    habitId: String,
    habitsViewModel: HabitsViewModel,
    onDismiss: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete Entry") },
        text = {
            Column {
                Text("Are you sure you want to delete this entry?")
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    coroutineScope.launch {
                        habitsViewModel.deleteEntry(habitId)
                        onDismiss()
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
