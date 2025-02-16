package com.example.habittrackerfrontend.android.detailsScreen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.habittrackerfrontend.android.DarkBlue
import com.example.habittrackerfrontend.android.DarkPurple
import com.example.habittrackerfrontend.android.LightPurple
import com.example.habittrackerfrontend.android.MiddlePurple
import com.example.habittrackerfrontend.android.Orange
import com.example.habittrackerfrontend.android.utilities.ErrorMessage
import com.example.habittrackerfrontend.entries.Entry
import com.example.habittrackerfrontend.habits.Habit
import com.example.habittrackerfrontend.habits.HabitsViewModel
import com.example.habittrackerfrontend.logMessage
import java.util.Locale

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

    val habitsState = habitsViewModel.habitsState.collectAsState()
    val habit = habitsState.value.habits.find { it.id == habitId }

    if (habit == null) {
        ErrorMessage("Habit not found")
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("") },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = DarkPurple
                    ),
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = Orange,
                    shape = CircleShape
                ) {
                    Icon(
                        Icons.Filled.Add,
                        contentDescription = "Add Habit",
                        tint = Color.White
                    )
                }
            }
        ) { padding ->
            // ACTUAL CONTENT
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // PURPLE PART
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(DarkPurple, MiddlePurple)
                            )
                        )
                ) {
                    Column {
                        HeaderSection(habit, habitsViewModel)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(30.dp)
                                .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
                                .background(Color.White)
                        )
                    }
                }

                // White section
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
                        .background(Color.White)
                        .padding(8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 30.dp)
                    ) {
                        Text(
                            text = "Wow, you have done this habit ${habit.entries.size} times!",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkBlue
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            items(habit.entries.sortedByDescending { it.timestamp }) { entry ->
                                EntryItem(entry)
                            }
                            item {
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(
                                    onClick = { showDeleteHabitDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                                    modifier = Modifier.fillMaxWidth(0.6f)
                                ) {
                                    Text("Delete Habit", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }

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
fun HeaderSection(habit: Habit, habitViewModel: HabitsViewModel) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, bottom = 16.dp)
            .padding(horizontal = 40.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = habit.name,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Box(
                modifier = Modifier
                    .background(Orange, shape = RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = habitViewModel.getDueMessage(habit),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.White
                )
            }
        }
        Spacer(modifier = Modifier.height(30.dp))
        Text(
            text = habit.description,
            fontSize = 18.sp,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(60.dp))
    }
}

@Composable
fun EntryItem(entry: Entry) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp, horizontal = 8.dp)
            .clip(RoundedCornerShape(25.dp))
            .background(LightPurple)
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = entry.timestamp.toString(),
                style = TextStyle(color = DarkBlue, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            )
            Text(
                text = entry.note,
                style = TextStyle(color = Color.Black, fontSize = 17.sp)
            )
        }
    }
}
