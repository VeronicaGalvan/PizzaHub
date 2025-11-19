package com.example.pizzahub_mobile.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.pizzahub_mobile.data.sample.SampleData
import com.example.pizzahub_mobile.ui.screens.*
import com.example.pizzahub_mobile.ui.viewmodel.AuthViewModel
import com.example.pizzahub_mobile.ui.viewmodel.CartViewModel
import com.example.pizzahub_mobile.ui.viewmodel.ProductsViewModel

@Composable
fun AppNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    // Auth state from ViewModel
    val authViewModel: AuthViewModel = viewModel()
    val isLoggedIn by authViewModel.isAuthenticated.collectAsState()
    val pendingRedirect = remember { mutableStateOf<String?>(null) }
    val previousAuthState = remember { mutableStateOf(isLoggedIn) }

    LaunchedEffect(Unit) { authViewModel.checkExistingToken() }

    // Detectar cuando la sesión expira (el TokenAuthenticator limpió los tokens)
    LaunchedEffect(isLoggedIn) {
        // Si el usuario estaba autenticado y ahora no lo está, redirigir a login
        if (previousAuthState.value && !isLoggedIn) {
            navController.navigate("login") {
                popUpTo(0) { inclusive = true } // Limpiar todo el backstack
                launchSingleTop = true
            }
        }
        previousAuthState.value = isLoggedIn
    }

    // Navigation helper that enforces auth for protected routes.
    val protectedRoutes = setOf("home", "catalog", "cart", "checkout", "profile", "order_tracking")
    val cartViewModel: CartViewModel = viewModel()
    val navigateTo: (String) -> Unit = { route ->
        val base = route.substringBefore('?') // ignore query params when checking
        // normalize param routes like order_tracking/{id} or product_detail/{id}
        val baseKey = base.substringBefore('/')
        if (baseKey in protectedRoutes && !isLoggedIn) {
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

        // filtered catalog: catalog/{category}
        composable("catalog/{category}") { backStackEntry ->
            val cat = backStackEntry.arguments?.getString("category") ?: "all"
            CatalogScreen({ navController.popBackStack() }, navigateTo, cat)
        }

        // product detail
        composable("product_detail/{productId}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("productId") ?: ""
            // get products viewmodel to find the product by id
            val productsViewModel: ProductsViewModel = viewModel()
            val products by productsViewModel.products.collectAsState()
            LaunchedEffect(Unit) { productsViewModel.loadProducts() }

            ProductDetailScreen(
                    productId = id,
                    onAddToCart = {
                        val product = products.firstOrNull { it.id == id }
                        if (product != null) cartViewModel.addProduct(product)

                        if (!isLoggedIn) {
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
                    onLogin = {
                        val dest = pendingRedirect.value
                        pendingRedirect.value = null
                        if (!dest.isNullOrBlank()) {
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
                    onRegister = { _accessToken ->
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
                    onNavigateToLogin = { navController.navigate("login") },
                    authViewModel = authViewModel
            )
        }

        // order tracking
        composable(
                route = "order_tracking/{id}?destLat={destLat}&destLon={destLon}",
                arguments =
                        listOf(
                                navArgument("destLat") {
                                    type = NavType.StringType
                                    nullable = true
                                    defaultValue = null
                                },
                                navArgument("destLon") {
                                    type = NavType.StringType
                                    nullable = true
                                    defaultValue = null
                                }
                        )
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("id") ?: ""
            val destLat = backStackEntry.arguments?.getString("destLat")?.toDoubleOrNull()
            val destLon = backStackEntry.arguments?.getString("destLon")?.toDoubleOrNull()
            OrderTrackingScreen(
                    onBack = { navController.popBackStack() },
                    orderId = orderId,
                    onOpenMap = { id ->
                        val route =
                                if (destLat != null && destLon != null) {
                                    "delivery_tracking/$id?destLat=$destLat&destLon=$destLon"
                                } else {
                                    "delivery_tracking/$id"
                                }
                        navigateTo(route)
                    },
                    onViewDetails = { id -> navController.navigate("order_detail/$id") }
            )
        }

        composable("profile") {
            ProfileScreen(
                    onBack = { navController.popBackStack() },
                    onNavigateToOrderTracking = { id ->
                        navController.navigate("order_tracking/$id")
                    },
                    onNavigateToOrderHistory = { navigateTo("order_history") },
                    onNavigateToNotifications = { navigateTo("notifications") },
                    onNavigateToAddresses = { navigateTo("addresses") },
                    onLogout = {
                        // perform logout in ViewModel and navigate to login screen
                        authViewModel.logout()
                        navController.navigate("login") {
                            popUpTo("profile") { inclusive = true }
                            launchSingleTop = true
                        }
                    }
            )
        }

        composable("order_history") {
            OrderHistoryScreen(
                    onBack = { navController.popBackStack() },
                    onOpenDetail = { id -> navController.navigate("order_detail/$id") },
                    onRepeatOrder = { pedidoId ->
                        // TODO: Implementar carga de productos del pedido desde API
                        // Por ahora agregamos productos de ejemplo
                        (SampleData.pizzas + SampleData.beverages).take(2).forEach {
                            cartViewModel.addProduct(it)
                        }
                        navigateTo("cart")
                    }
            )
        }

        composable("order_detail/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: ""
            OrderDetailScreen(
                    orderId = id,
                    onBack = { navController.popBackStack() },
                    onRepeat = { orderId ->
                        // TODO: Implementar carga de productos del pedido desde API
                        // Por ahora agregamos productos de ejemplo
                        (SampleData.pizzas + SampleData.beverages).take(2).forEach {
                            cartViewModel.addProduct(it)
                        }
                        navigateTo("cart")
                    },
                    onRate = { orderId -> navController.navigate("rating/$orderId") }
            )
        }

        composable("rating/{orderId}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("orderId") ?: ""
            RatingScreen(
                    orderId = id,
                    onBack = { navController.popBackStack() },
                    onSubmit = { stars, comment -> navController.popBackStack() }
            )
        }

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
                    onShowMap = { destLat, destLon ->
                        navController.navigate("map_preview/$destLat/$destLon")
                    },
                    onConfirmOrder = { orderId, destLat, destLon ->
                        cartViewModel.clearCart()
                        val route =
                                if (destLat != null && destLon != null) {
                                    "order_tracking/$orderId?destLat=$destLat&destLon=$destLon"
                                } else {
                                    "order_tracking/$orderId"
                                }
                        navController.navigate(route) {
                            popUpTo("cart") { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    onSelectAddress = { navController.navigate("addresses") }
            )
        }

        composable("map_preview/{destLat}/{destLon}") { backStackEntry ->
            val destLat = backStackEntry.arguments?.getString("destLat")?.toDoubleOrNull() ?: 21.12
            val destLon =
                    backStackEntry.arguments?.getString("destLon")?.toDoubleOrNull() ?: -101.68
            MapPreviewScreen(
                    destLat = destLat,
                    destLon = destLon,
                    onBack = { navController.popBackStack() },
                    onConfirm = { navController.popBackStack() }
            )
        }

        composable("addresses") {
            AddressManagementScreen(
                    onBack = { navController.popBackStack() },
                    onSelect = { _ -> navController.popBackStack() }
            )
        }

        composable("chat") { ChatScreen(onBack = { navController.popBackStack() }) }

        composable(
                route = "delivery_tracking/{orderId}?destLat={destLat}&destLon={destLon}",
                arguments =
                        listOf(
                                navArgument("destLat") {
                                    type = NavType.StringType
                                    nullable = true
                                    defaultValue = null
                                },
                                navArgument("destLon") {
                                    type = NavType.StringType
                                    nullable = true
                                    defaultValue = null
                                }
                        )
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("orderId") ?: ""
            val destLat = backStackEntry.arguments?.getString("destLat")?.toDoubleOrNull()
            val destLon = backStackEntry.arguments?.getString("destLon")?.toDoubleOrNull()
            DeliveryTrackingMapScreen(
                    orderId = id,
                    destLat = destLat,
                    destLon = destLon,
                    onBack = { navController.popBackStack() }
            )
        }

        composable("notifications") {
            NotificationsScreen(onBack = { navController.popBackStack() })
        }
    }
}
