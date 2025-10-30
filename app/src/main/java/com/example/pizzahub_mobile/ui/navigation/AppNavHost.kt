package com.example.pizzahub_mobile.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pizzahub_mobile.ui.screens.*

@Composable
fun AppNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    // Simple in-memory auth state for gating protected routes. In a real app this should be
    // provided by a proper Auth/ViewModel and persistent store.
    val isLoggedIn = remember { mutableStateOf(false) }
    val pendingRedirect = remember { mutableStateOf<String?>(null) }

    // Navigation helper that enforces auth for protected routes.
    val protectedRoutes = setOf("order", "profile", "orderHistory", "checkout")
    val navigateTo: (String) -> Unit = { route ->
        val base = route.substringBefore('?') // ignore query params when checking
        if (base in protectedRoutes && !isLoggedIn.value) {
            // remember where the user wanted to go, then send to login
            pendingRedirect.value = route
            navController.navigate("login")
        } else {
            navController.navigate(route)
        }
    }

    NavHost(navController = navController, startDestination = "home", modifier = modifier) {
        composable("home") { HomeScreen(onNavigate = navigateTo) }

        // general catalog (all)
        composable("catalog") { CatalogScreen({ navController.popBackStack() }, navigateTo, "all") }

        // filtered catalog: catalog/{category} where category = pizzas|bebidas|complementos
        composable("catalog/{category}") { backStackEntry ->
            val cat = backStackEntry.arguments?.getString("category") ?: "all"
            CatalogScreen({ navController.popBackStack() }, navigateTo, cat)
        }

        // product detail
        composable("product/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: ""
            ProductDetailScreen(
                    productId = id,
                    onAddToCart = {
                        // require auth for adding to cart / ordering
                        if (!isLoggedIn.value) {
                            pendingRedirect.value = "order"
                            navController.navigate("login")
                        } else {
                            navController.navigate("order")
                        }
                    }
            )
        }

        composable("login") {
            LoginScreen(
                    onBack = { navController.popBackStack() },
                    onLogin = { _phone ->
                        // mark logged in and redirect if we had a pending route
                        isLoggedIn.value = true
                        val dest = pendingRedirect.value
                        pendingRedirect.value = null
                        if (!dest.isNullOrBlank()) navController.navigate(dest)
                        else navController.popBackStack()
                    }
            )
        }

        composable("register") {
            RegisterScreen(
                    onRegister = { _name, _phone ->
                        isLoggedIn.value = true
                        val dest = pendingRedirect.value
                        pendingRedirect.value = null
                        if (!dest.isNullOrBlank()) navController.navigate(dest)
                        else navController.popBackStack()
                    }
            )
        }

        composable("order") { OrderTrackingScreen(onBack = { navController.popBackStack() }) }
        composable("profile") { ProfileScreen(onBack = { navController.popBackStack() }) }
    }
}
