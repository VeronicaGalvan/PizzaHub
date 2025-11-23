package com.example.pizzahub_mobile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pizzahub_mobile.data.sample.SampleData
import com.example.pizzahub_mobile.ui.theme.PizzaHub_MobileTheme
import com.example.pizzahub_mobile.ui.viewmodel.CartItem
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
        itemsWithQty: List<CartItem> = SampleData.pizzas.map { CartItem(it, 1) },
        onBack: () -> Unit,
        onShowMap: (destLat: Double, destLon: Double) -> Unit = { _, _ -> },
        onConfirmOrder: (orderId: String, destLat: Double?, destLon: Double?) -> Unit = { _, _, _ ->
        },
        onSelectAddress: () -> Unit = {},
        authViewModel: com.example.pizzahub_mobile.ui.viewmodel.AuthViewModel? = null
) {
        val cream = Color(0xFFFFF8EE)
        val softBeige = Color(0xFFFFEEDD)
        val brownDark = Color(0xFF4E342E)
        val terracota = Color(0xFFD35400)

        val ctx = LocalContext.current
        val scope = rememberCoroutineScope()

        var tipoPedido by remember { mutableStateOf("Domicilio") }

        // ViewModel de autenticación
        val localAuthViewModel =
                authViewModel ?: viewModel<com.example.pizzahub_mobile.ui.viewmodel.AuthViewModel>()
        val currentUser by localAuthViewModel.currentUser.collectAsState()
        val clientePerfil by localAuthViewModel.clientePerfil.collectAsState()
        val loading by localAuthViewModel.isLoading.collectAsState()
        val error by localAuthViewModel.error.collectAsState()

        // ViewModel de checkout
        val checkoutViewModel: com.example.pizzahub_mobile.ui.viewmodel.CheckoutViewModel =
                viewModel()
        val pedidoCreated by checkoutViewModel.pedidoCreated.collectAsState()
        val checkoutLoading by checkoutViewModel.isLoading.collectAsState()
        val checkoutError by checkoutViewModel.error.collectAsState()

        // Cargar perfil del cliente al inicio
        LaunchedEffect(Unit) { localAuthViewModel.getClientePerfil() }

        // Formulario de dirección - prellenar con datos del usuario
        var nombre by remember { mutableStateOf("") }
        var apellidos by remember { mutableStateOf("") }
        var telefono by remember { mutableStateOf("") }
        var colonia by remember { mutableStateOf("") }
        var calle by remember { mutableStateOf("") }
        var numeroCasa by remember { mutableStateOf("") }
        var observaciones by remember { mutableStateOf("") }

        // Coordenadas geocodificadas del usuario
        var userCoordinates by remember { mutableStateOf<Pair<Double, Double>?>(null) }
        var isGeocoding by remember { mutableStateOf(false) }
        var geocodingError by remember { mutableStateOf<String?>(null) }

        // Método de pago
        var metodoPago by remember { mutableStateOf(1) } // 1=Efectivo por defecto

        // Coordenadas de la pizzería (León, Guanajuato)
        // https://maps.app.goo.gl/iZqkmDrn7AM8DKZ27
        // Coordenadas de la pizzería: Blvd. Antonio Madrazo #6401-Local 3, Valle de Señora, 37205
        // León de los Aldama, Gto.
        val pizzeriaLat = 21.15969
        val pizzeriaLon = -101.65070

        // Prellenar nombre y teléfono del usuario autenticado y datos del cliente
        LaunchedEffect(currentUser, clientePerfil) {
                // Si ya existe un perfil de cliente, usar esos datos
                clientePerfil?.let { perfil ->
                        if (nombre.isEmpty()) nombre = perfil.nombre
                        if (apellidos.isEmpty()) apellidos = perfil.apellidos
                        if (telefono.isEmpty()) telefono = perfil.telefono
                        if (colonia.isEmpty()) colonia = perfil.colonia ?: ""
                        if (calle.isEmpty()) calle = perfil.calle ?: ""
                        if (numeroCasa.isEmpty()) numeroCasa = perfil.numeroCasa ?: ""
                        if (observaciones.isEmpty()) observaciones = perfil.observaciones ?: ""
                }
                        ?: currentUser?.let { user ->
                                // Si no hay perfil de cliente, usar datos del usuario
                                if (nombre.isEmpty()) {
                                        // Separar nombreCompleto en nombre y apellidos
                                        val parts =
                                                user.nombreCompleto?.split(" ", limit = 2)
                                                        ?: listOf()
                                        nombre = parts.getOrNull(0) ?: ""
                                        apellidos = parts.getOrNull(1) ?: ""
                                }
                                if (telefono.isEmpty()) {
                                        telefono = user.telefonoContacto ?: ""
                                }
                        }
        }

        val subtotal = itemsWithQty.sumOf { it.product.price * it.quantity }
        val tax = subtotal * 0.12
        val total = subtotal + tax

        Column(
                modifier =
                        Modifier.fillMaxSize()
                                .background(Brush.verticalGradient(listOf(cream, Color.White)))
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                                .verticalScroll(rememberScrollState())
        ) {
                // 🔙 Encabezado con título centrado
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        IconButton(
                                onClick = onBack,
                                modifier = Modifier.align(Alignment.CenterStart)
                        ) {
                                Icon(
                                        Icons.Filled.ArrowBack,
                                        contentDescription = "Volver",
                                        tint = brownDark
                                )
                        }

                        Text(
                                text = "Confirmar pedido",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = brownDark
                        )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 🧾 Tipo de pedido
                Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = softBeige),
                        modifier = Modifier.fillMaxWidth().shadow(4.dp, RoundedCornerShape(20.dp))
                ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                                Text(
                                        "Tipo de pedido",
                                        fontWeight = FontWeight.Bold,
                                        color = brownDark
                                )
                                Spacer(modifier = Modifier.height(10.dp))

                                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                        FilterChip(
                                                selected = tipoPedido == "Domicilio",
                                                onClick = { tipoPedido = "Domicilio" },
                                                label = { Text("Domicilio") }
                                        )
                                        FilterChip(
                                                selected = tipoPedido == "Recoger",
                                                onClick = { tipoPedido = "Recoger" },
                                                label = { Text("Recoger") }
                                        )
                                }

                                if (tipoPedido == "Domicilio") {
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Text(
                                                "Dirección de entrega",
                                                fontWeight = FontWeight.Bold,
                                                color = brownDark,
                                                fontSize = 16.sp
                                        )
                                        Spacer(modifier = Modifier.height(12.dp))

                                        // Formulario completo de dirección
                                        Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                                OutlinedTextField(
                                                        value = nombre,
                                                        onValueChange = { newValue ->
                                                                if (newValue.all {
                                                                                it.isLetter() ||
                                                                                        it.isWhitespace()
                                                                        }
                                                                ) {
                                                                        nombre = newValue
                                                                }
                                                        },
                                                        label = { Text("Nombre") },
                                                        modifier = Modifier.weight(1f),
                                                        shape = RoundedCornerShape(12.dp),
                                                        singleLine = true,
                                                        keyboardOptions =
                                                                KeyboardOptions(
                                                                        keyboardType =
                                                                                KeyboardType.Text
                                                                )
                                                )
                                                OutlinedTextField(
                                                        value = apellidos,
                                                        onValueChange = { newValue ->
                                                                if (newValue.all {
                                                                                it.isLetter() ||
                                                                                        it.isWhitespace()
                                                                        }
                                                                ) {
                                                                        apellidos = newValue
                                                                }
                                                        },
                                                        label = { Text("Apellidos") },
                                                        modifier = Modifier.weight(1f),
                                                        shape = RoundedCornerShape(12.dp),
                                                        singleLine = true,
                                                        keyboardOptions =
                                                                KeyboardOptions(
                                                                        keyboardType =
                                                                                KeyboardType.Text
                                                                )
                                                )
                                        }

                                        Spacer(modifier = Modifier.height(10.dp))

                                        OutlinedTextField(
                                                value = telefono,
                                                onValueChange = { newValue ->
                                                        if (newValue.all { it.isDigit() } &&
                                                                        newValue.length <= 10
                                                        ) {
                                                                telefono = newValue
                                                        }
                                                },
                                                label = { Text("Teléfono (10 dígitos)") },
                                                modifier = Modifier.fillMaxWidth(),
                                                shape = RoundedCornerShape(12.dp),
                                                singleLine = true,
                                                keyboardOptions =
                                                        KeyboardOptions(
                                                                keyboardType = KeyboardType.Phone
                                                        )
                                        )

                                        Spacer(modifier = Modifier.height(10.dp))

                                        OutlinedTextField(
                                                value = colonia,
                                                onValueChange = { colonia = it },
                                                label = { Text("Colonia") },
                                                modifier = Modifier.fillMaxWidth(),
                                                shape = RoundedCornerShape(12.dp),
                                                singleLine = true
                                        )

                                        Spacer(modifier = Modifier.height(10.dp))

                                        Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                                OutlinedTextField(
                                                        value = calle,
                                                        onValueChange = { calle = it },
                                                        label = { Text("Calle") },
                                                        modifier = Modifier.weight(2f),
                                                        shape = RoundedCornerShape(12.dp),
                                                        singleLine = true
                                                )
                                                OutlinedTextField(
                                                        value = numeroCasa,
                                                        onValueChange = { numeroCasa = it },
                                                        label = { Text("Número") },
                                                        modifier = Modifier.weight(1f),
                                                        shape = RoundedCornerShape(12.dp),
                                                        singleLine = true
                                                )
                                        }

                                        Spacer(modifier = Modifier.height(10.dp))

                                        OutlinedTextField(
                                                value = observaciones,
                                                onValueChange = { observaciones = it },
                                                label = { Text("Observaciones (opcional)") },
                                                modifier = Modifier.fillMaxWidth(),
                                                shape = RoundedCornerShape(12.dp),
                                                maxLines = 2
                                        )

                                        Spacer(modifier = Modifier.height(12.dp))

                                        // Mostrar error de geocodificación si existe
                                        geocodingError?.let { errMsg ->
                                                Text(
                                                        text = errMsg,
                                                        color = MaterialTheme.colorScheme.error,
                                                        fontSize = 12.sp,
                                                        modifier = Modifier.padding(bottom = 8.dp)
                                                )
                                        }

                                        Button(
                                                onClick = {
                                                        // Validar que la dirección esté completa
                                                        if (nombre.isBlank() ||
                                                                        apellidos.isBlank() ||
                                                                        telefono.isBlank() ||
                                                                        calle.isBlank() ||
                                                                        numeroCasa.isBlank() ||
                                                                        colonia.isBlank()
                                                        ) {
                                                                geocodingError =
                                                                        "Por favor completa todos los campos obligatorios"
                                                                return@Button
                                                        }

                                                        geocodingError = null
                                                        isGeocoding = true

                                                        scope.launch {
                                                                try {
                                                                        // Guardar/actualizar datos
                                                                        // del cliente
                                                                        if (clientePerfil == null) {
                                                                                // Crear nuevo
                                                                                // cliente
                                                                                currentUser?.id
                                                                                        ?.let {
                                                                                                usuarioId
                                                                                                ->
                                                                                                localAuthViewModel
                                                                                                        .createCliente(
                                                                                                                nombre,
                                                                                                                apellidos,
                                                                                                                telefono,
                                                                                                                colonia,
                                                                                                                calle,
                                                                                                                numeroCasa,
                                                                                                                observaciones,
                                                                                                                usuarioId
                                                                                                        )
                                                                                                // Recargar perfil
                                                                                                localAuthViewModel
                                                                                                        .getClientePerfil()
                                                                                        }
                                                                        } else {
                                                                                // Actualizar
                                                                                // cliente existente
                                                                                localAuthViewModel
                                                                                        .updateClientePerfil(
                                                                                                nombre,
                                                                                                apellidos,
                                                                                                telefono,
                                                                                                colonia,
                                                                                                calle,
                                                                                                numeroCasa,
                                                                                                observaciones
                                                                                        )
                                                                        }

                                                                        val hereRepo =
                                                                                com.example
                                                                                        .pizzahub_mobile
                                                                                        .data
                                                                                        .network
                                                                                        .HereRepository(
                                                                                                ctx
                                                                                        )
                                                                        val result =
                                                                                hereRepo.geocodeAddress(
                                                                                        calle =
                                                                                                calle,
                                                                                        numero =
                                                                                                numeroCasa,
                                                                                        colonia =
                                                                                                colonia
                                                                                )

                                                                        result.fold(
                                                                                onSuccess = { coords
                                                                                        ->
                                                                                        userCoordinates =
                                                                                                coords
                                                                                        // Navegar
                                                                                        // con las
                                                                                        // coordenadas
                                                                                        onShowMap(
                                                                                                coords.first,
                                                                                                coords.second
                                                                                        )
                                                                                },
                                                                                onFailure = { e ->
                                                                                        geocodingError =
                                                                                                "No se pudo encontrar la dirección: ${e.message}"
                                                                                }
                                                                        )
                                                                } catch (e: Exception) {
                                                                        geocodingError =
                                                                                "Error al buscar dirección: ${e.message}"
                                                                } finally {
                                                                        isGeocoding = false
                                                                }
                                                        }
                                                },
                                                modifier = Modifier.fillMaxWidth(),
                                                colors =
                                                        ButtonDefaults.buttonColors(
                                                                containerColor = terracota
                                                        ),
                                                enabled = !isGeocoding
                                        ) {
                                                if (isGeocoding) {
                                                        CircularProgressIndicator(
                                                                modifier = Modifier.size(20.dp),
                                                                color = Color.White
                                                        )
                                                } else {
                                                        Text(
                                                                "Ver ruta estimada",
                                                                color = Color.White
                                                        )
                                                }
                                        }
                                }
                        }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 💳 Resumen y confirmación
                Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = softBeige),
                        modifier = Modifier.fillMaxWidth().shadow(4.dp, RoundedCornerShape(20.dp))
                ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                                SummaryRow("Subtotal", subtotal, brownDark)
                                SummaryRow("Impuestos", tax, brownDark)
                                Divider(modifier = Modifier.padding(vertical = 8.dp))
                                SummaryRow("Total", total, brownDark, bold = true)
                                Spacer(modifier = Modifier.height(16.dp))

                                // Navegar al tracking cuando se crea el pedido
                                LaunchedEffect(pedidoCreated) {
                                        pedidoCreated?.let { pedido ->
                                                // Navegar al tracking
                                                onConfirmOrder(
                                                        pedido.id.toString(),
                                                        userCoordinates?.first,
                                                        userCoordinates?.second
                                                )
                                                checkoutViewModel.clearPedidoCreated()
                                        }
                                }

                                Button(
                                        onClick = {
                                                scope.launch {
                                                        var coords: Pair<Double, Double>? = null

                                                        // Si es domicilio, primero guardar la
                                                        // dirección y geocodificar
                                                        if (tipoPedido == "Domicilio") {
                                                                if (nombre.isBlank() ||
                                                                                apellidos
                                                                                        .isBlank() ||
                                                                                telefono.isBlank() ||
                                                                                colonia.isBlank() ||
                                                                                calle.isBlank() ||
                                                                                numeroCasa.isBlank()
                                                                ) {
                                                                        // Mostrar error - falta
                                                                        // información
                                                                        return@launch
                                                                }

                                                                // Guardar/actualizar cliente si es
                                                                // domicilio
                                                                if (clientePerfil == null) {
                                                                        // Crear nuevo cliente
                                                                        currentUser?.id?.let {
                                                                                usuarioId ->
                                                                                localAuthViewModel
                                                                                        .createCliente(
                                                                                                nombre,
                                                                                                apellidos,
                                                                                                telefono,
                                                                                                colonia,
                                                                                                calle,
                                                                                                numeroCasa,
                                                                                                observaciones
                                                                                                        .ifBlank {
                                                                                                                "Ninguna"
                                                                                                        },
                                                                                                usuarioId
                                                                                        )
                                                                        }
                                                                } else {
                                                                        // Actualizar cliente
                                                                        // existente
                                                                        localAuthViewModel
                                                                                .updateClientePerfil(
                                                                                        nombre,
                                                                                        apellidos,
                                                                                        telefono,
                                                                                        colonia,
                                                                                        calle,
                                                                                        numeroCasa,
                                                                                        observaciones
                                                                                                .ifBlank {
                                                                                                        "Ninguna"
                                                                                                }
                                                                                )
                                                                }

                                                                // Geocodificar si aún no se ha
                                                                // hecho
                                                                if (userCoordinates == null) {
                                                                        try {
                                                                                val hereRepo =
                                                                                        com.example
                                                                                                .pizzahub_mobile
                                                                                                .data
                                                                                                .network
                                                                                                .HereRepository(
                                                                                                        ctx
                                                                                                )
                                                                                val result =
                                                                                        hereRepo.geocodeAddress(
                                                                                                calle =
                                                                                                        calle,
                                                                                                numero =
                                                                                                        numeroCasa,
                                                                                                colonia =
                                                                                                        colonia
                                                                                        )
                                                                                result.fold(
                                                                                        onSuccess = {
                                                                                                c ->
                                                                                                coords =
                                                                                                        c
                                                                                        },
                                                                                        onFailure = { /* Continuar sin coordenadas */
                                                                                        }
                                                                                )
                                                                        } catch (e: Exception) {
                                                                                // Continuar sin
                                                                                // coordenadas
                                                                        }
                                                                } else {
                                                                        coords = userCoordinates
                                                                }
                                                        }

                                                        // Crear pedido real con el backend
                                                        val clienteId = clientePerfil?.id ?: 0
                                                        val tipo =
                                                                if (tipoPedido == "Domicilio") 4
                                                                else 2 // 4=Domicilio, 2=ParaLlevar
                                                        val direccion =
                                                                if (tipoPedido == "Domicilio") {
                                                                        "$calle $numeroCasa, $colonia"
                                                                } else null

                                                        checkoutViewModel.createPedido(
                                                                clienteId = clienteId,
                                                                tipo = tipo,
                                                                metodoPago = metodoPago,
                                                                direccionEntrega = direccion,
                                                                observaciones =
                                                                        observaciones.ifBlank {
                                                                                null
                                                                        },
                                                                cartItems = itemsWithQty
                                                        )
                                                }
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(16.dp),
                                        colors =
                                                ButtonDefaults.buttonColors(
                                                        containerColor = terracota
                                                ),
                                        enabled = !loading && !checkoutLoading
                                ) {
                                        if (loading || checkoutLoading) {
                                                CircularProgressIndicator(
                                                        modifier = Modifier.size(20.dp),
                                                        color = Color.White
                                                )
                                        } else {
                                                Text(
                                                        "Confirmar pedido",
                                                        color = Color.White,
                                                        fontWeight = FontWeight.Bold
                                                )
                                        }
                                }

                                // Mostrar errores
                                checkoutError?.let {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                                text = it,
                                                color = MaterialTheme.colorScheme.error,
                                                fontSize = 12.sp
                                        )
                                }

                                error?.let {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                                text = it,
                                                color = MaterialTheme.colorScheme.error,
                                                fontSize = 12.sp
                                        )
                                }
                        }
                }
        }
}

@Preview(showBackground = true)
@Composable
fun CheckoutScreenPreview() {
        PizzaHub_MobileTheme {
                CheckoutScreen(
                        onBack = {},
                        onShowMap = { _, _ -> },
                        onConfirmOrder = { _, _, _ -> }
                )
        }
}
