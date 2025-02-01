package com.example.habittrackerfrontend.android.homeScreen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.habittrackerfrontend.android.utilities.Loader
import com.example.habittrackerfrontend.habits.Habit
import com.example.habittrackerfrontend.habits.HabitsViewModel
import com.example.habittrackerfrontend.android.utilities.ErrorMessage

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
    habitsViewModel: HabitsViewModel,
    navController: NavHostController
    ) {
    val habitsState = habitsViewModel.habitsState.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { AppBar() },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Filled.Add, contentDescription = "Add Habit")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            if (habitsState.value.loading) Loader()
            if (habitsState.value.error != null) ErrorMessage(habitsState.value.error!!)
            if (habitsState.value.habits.isNotEmpty()) HabitsListView(habitsState.value.habits, navController)
        }
    }

    // Show the Add Habit Dialog
    if (showDialog) {
        AddHabitDialog(
            habitsViewModel = habitsViewModel,
            onDismiss = { showDialog = false }
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppBar() {
    TopAppBar(
        title = { Text(text = "Your Habits") }
    )
}

@Composable
fun HabitsListView(habits: List<Habit>, navController: NavHostController) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(habits) { habit ->
            HabitItemView(habit = habit, navController) // passes onclick function to item
        }
    }
}

@Composable
fun HabitItemView(habit: Habit, navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                navController.navigate("habitDetails/${habit.id}")
             }
            .padding(16.dp)
    ) {
        Text(
            text = habit.name + " " + habit.entries.size,
            style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 22.sp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = habit.description)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = habit.frequency,
            style = TextStyle(color = Color.Gray),
            modifier = Modifier.align(Alignment.End)
        )
        Spacer(modifier = Modifier.height(4.dp))
    }
}

