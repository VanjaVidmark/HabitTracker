package com.example.habittrackerfrontend.android.homeScreen

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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.habittrackerfrontend.android.DarkBlue
import com.example.habittrackerfrontend.android.DarkGray
import com.example.habittrackerfrontend.android.DarkPurple
import com.example.habittrackerfrontend.android.LightGray
import com.example.habittrackerfrontend.android.LightPurple
import com.example.habittrackerfrontend.android.MiddlePurple
import com.example.habittrackerfrontend.android.Orange
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
    var selectedTab by remember { mutableStateOf(habitsViewModel.filterValue) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                shape = CircleShape
            ) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = "Add Habit",
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ACTUAL CONTENT
            Spacer(modifier = Modifier.height(16.dp))
            HeaderSection(habitsViewModel)
            Spacer(modifier = Modifier.height(16.dp))
            HabitFilterSelector(selectedTab) { selectedTab = it; habitsViewModel.editFilter(it) }

            if (habitsState.value.loading) Loader()
            if (habitsState.value.error != null) ErrorMessage(habitsState.value.error!!)
            if (habitsState.value.habits.isNotEmpty()) HabitsListView(habitsState.value.habits, navController, habitsViewModel)
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


@Composable
fun HeaderSection(habitsViewModel: HabitsViewModel) {
    val message by habitsViewModel.message.collectAsState()
    val greeting = remember { habitsViewModel.getGreeting() }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 32.dp, bottom = 16.dp)
            .padding(horizontal = 40.dp)
    ) {
        Text(
            text = greeting,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = Orange
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = message,
            fontSize = 18.sp,
            color = DarkBlue
        )
    }
}

@Composable
fun HabitsListView(habits: List<Habit>, navController: NavHostController, habitsViewModel: HabitsViewModel) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 30.dp, vertical = 32.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(40.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(DarkPurple, MiddlePurple)
                    )
                )
                .padding(horizontal = 10.dp, vertical = 30.dp)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(habits.sortedBy {-it.daysSinceDue}) { habit ->
                    HabitItemView(habit = habit, navController, habitsViewModel)
                }
            }
        }
    }
}


@Composable
fun HabitItemView(habit: Habit, navController: NavHostController, viewModel: HabitsViewModel) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp, horizontal = 8.dp)
            .clip(RoundedCornerShape(25.dp))
            .background(LightPurple)
            .clickable { navController.navigate("habitDetails/${habit.id}") }
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween // Ensures the orange box is at the far right
            ) {
                Text(
                    text = habit.name,
                    style = TextStyle(color = DarkBlue, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                )
                Box(
                    modifier = Modifier
                        .background(Orange, shape = RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = viewModel.getDueMessage(habit),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.White
                    )
                }
            }
            Text(
                text = "Daily, since ${habit.startDate}",
                style = TextStyle(color = Color.Black, fontSize = 17.sp)
            )
        }
    }
}


@Composable
fun HabitFilterSelector(selectedTab: String, onTabSelected: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
            .wrapContentWidth(Alignment.CenterHorizontally)
            .clip(RoundedCornerShape(40))
            .background(LightGray),
        horizontalArrangement = Arrangement.Center
    ) {
        listOf("Today", "Upcoming").forEach { tab ->
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(40))
                    .clickable { onTabSelected(tab) }
                    .background(
                        if (selectedTab == tab) Orange else Color.Transparent
                    )
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = tab,
                    color = if (selectedTab == tab) Color.White else DarkGray,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Light
                )
            }
        }
    }
}


