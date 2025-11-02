package com.example.pizzahub_mobile.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pizzahub_mobile.data.sample.SampleData
import com.example.pizzahub_mobile.ui.screens.*
import com.example.pizzahub_mobile.ui.viewmodel.CartViewModel

@Composable
fun AppNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    // Simple in-memory auth state for gating protected routes. In a real app this should be
    // provided by a proper Auth/ViewModel and persistent store.
    val isLoggedIn = remember { mutableStateOf(false) }
    val pendingRedirect = remember { mutableStateOf<String?>(null) }

    // Navigation helper that enforces auth for protected routes.
    val protectedRoutes = setOf("home", "catalog", "cart", "checkout", "profile", "order_tracking")
    val cartViewModel: CartViewModel = viewModel()
    val navigateTo: (String) -> Unit = { route ->
        val base = route.substringBefore('?') // ignore query params when checking
        // normalize param routes like order_tracking/{id} or product_detail/{id}
        val baseKey = base.substringBefore('/')
        if (baseKey in protectedRoutes && !isLoggedIn.value) {
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
        composable("product_detail/{productId}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("productId") ?: ""
            ProductDetailScreen(
                    productId = id,
                    onAddToCart = {
                        // find product from sample data (in a real app use repository)
                        val product =
                                (SampleData.pizzas + SampleData.beverages + SampleData.complements)
                                        .firstOrNull { it.id == id }
                        if (product != null) {
                            cartViewModel.addProduct(product)
                        }

                        // require auth for viewing cart/checkout
                        if (!isLoggedIn.value) {
                            pendingRedirect.value = "cart"
                            navController.navigate("login")
                        } else {
                            navController.navigate("cart")
                        }
                    },
                    onBack = { navController.popBackStack() },
                    onNavigateToCart = { navigateTo("cart") }
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
                        if (!dest.isNullOrBlank()) {
                            // navigate to the pending destination and clear login from backstack
                            navController.navigate(dest) {
                                popUpTo("login") { inclusive = true }
                                launchSingleTop = true
                            }
                        } else {
                            navController.navigate("home") {
                                popUpTo("login") { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    },
                    onNavigateToRegister = { navController.navigate("register") }
            )
        }

        composable("register") {
            RegisterScreen(
                    onBack = { navController.popBackStack() },
                    onRegister = { _name, _phone ->
                        isLoggedIn.value = true
                        val dest = pendingRedirect.value
                        pendingRedirect.value = null
                        if (!dest.isNullOrBlank()) {
                            navController.navigate(dest) {
                                popUpTo("register") { inclusive = true }
                                launchSingleTop = true
                            }
                        } else {
                            navController.navigate("home") {
                                popUpTo("register") { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    },
                    onNavigateToLogin = { navController.navigate("login") }
            )
        }

        // order tracking with id
        composable("order_tracking/{id}") { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("id") ?: ""
            OrderTrackingScreen(onBack = { navController.popBackStack() }, orderId = orderId)
        }
        composable("profile") { ProfileScreen(onBack = { navController.popBackStack() }) }
        composable("cart") {
            val cartState by cartViewModel.cartState.collectAsState()
            CartScreen(
                    itemsWithQty = cartState.items,
                    onBack = { navController.popBackStack() },
                    onUpdateQuantity = { id, qty -> cartViewModel.updateQuantity(id, qty) },
                    onRemoveItem = { id -> cartViewModel.removeItem(id) },
                    onClearCart = { cartViewModel.clearCart() },
                    onProceedToCheckout = { navigateTo("checkout") }
            )
        }

        composable("checkout") {
            val cartState by cartViewModel.cartState.collectAsState()
            CheckoutScreen(
                    itemsWithQty = cartState.items,
                    onBack = { navController.popBackStack() },
                    onShowMap = { navController.navigate("map_preview") },
                    onConfirmOrder = { orderId ->
                        // clear cart after creating order
                        cartViewModel.clearCart()
                        navController.navigate("order_tracking/$orderId") {
                            popUpTo("cart") { inclusive = true }
                            launchSingleTop = true
                        }
                    }
            )
        }

        composable("map_preview") {
            MapPreviewScreen(
                    onBack = { navController.popBackStack() },
                    onConfirm = { navController.popBackStack() }
            )
        }
    }
}
