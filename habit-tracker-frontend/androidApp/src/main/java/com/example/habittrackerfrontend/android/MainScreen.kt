package com.example.habittrackerfrontend.android

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.*
import androidx.navigation.compose.*
import com.example.habittrackerfrontend.android.homeScreen.HomeScreen
import com.example.habittrackerfrontend.habits.Habit
import com.example.habittrackerfrontend.habits.HabitsViewModel
import kotlinx.serialization.json.Json
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.habittrackerfrontend.android.detailsScreen.DetailsScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val habitsViewModel = remember { HabitsViewModel() }

    NavHost(navController, startDestination = "habitsList") {
        composable("habitsList") {
            HomeScreen(habitsViewModel, navController)
        }
        composable(
            "habitDetails/{habitId}",
            arguments = listOf(navArgument("habitId") { type = NavType.StringType })
        ) { backStackEntry ->
            val habitId = backStackEntry.arguments?.getString("habitId") ?: return@composable
            DetailsScreen(habitId = habitId, habitsViewModel = habitsViewModel, onBack = { navController.popBackStack() })
        }
    }
}
