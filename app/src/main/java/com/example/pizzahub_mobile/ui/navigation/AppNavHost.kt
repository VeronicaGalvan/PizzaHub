package com.example.pizzahub_mobile.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pizzahub_mobile.ui.screens.*

@Composable
fun AppNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    // helper navigation lambda with explicit type to avoid overload/ambiguity when calling
    // navController.navigate
    val navigateTo: (String) -> Unit = { r -> navController.navigate(r) }

    NavHost(navController = navController, startDestination = "home", modifier = modifier) {
        composable("home") { HomeScreen(onNavigate = navigateTo) }
        composable("catalog") { CatalogScreen({ navController.popBackStack() }, navigateTo) }
        composable("order") { OrderTrackingScreen(onBack = { navController.popBackStack() }) }
        composable("profile") { ProfileScreen(onBack = { navController.popBackStack() }) }
    }
}
