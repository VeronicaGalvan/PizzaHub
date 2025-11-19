package com.example.pizzahub_mobile.ui.screens

import android.content.pm.ApplicationInfo
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pizzahub_mobile.ui.theme.PizzaBrown
import com.example.pizzahub_mobile.ui.theme.PizzaHub_MobileTheme
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
        onBack: () -> Unit,
        onNavigateToOrderTracking: (String) -> Unit = {},
        onNavigateToOrderHistory: () -> Unit = {},
        onNavigateToNotifications: () -> Unit = {},
        onNavigateToAddresses: () -> Unit = {},
        onLogout: () -> Unit = {},
        authViewModel: com.example.pizzahub_mobile.ui.viewmodel.AuthViewModel? = null
) {
        // Palette consistent with HomeScreen
        val terracota = Color(0xFFC0392B)
        val cream = Color(0xFFFFF4E8)
        val brownDark = Color(0xFF4E342E)
        val scope = rememberCoroutineScope()

        // ViewModels
        val localAuthViewModel =
                authViewModel ?: viewModel<com.example.pizzahub_mobile.ui.viewmodel.AuthViewModel>()
        val clientePerfil by localAuthViewModel.clientePerfil.collectAsState()
        val currentUser by localAuthViewModel.currentUser.collectAsState()
        val loading by localAuthViewModel.isLoading.collectAsState()

        val orderHistoryViewModel: com.example.pizzahub_mobile.ui.viewmodel.OrderHistoryViewModel =
                viewModel()
        val pedidos by orderHistoryViewModel.pedidos.collectAsState()

        // Estados de edición
        var isEditing by remember { mutableStateOf(false) }
        var nombre by remember { mutableStateOf("") }
        var apellidos by remember { mutableStateOf("") }
        var telefono by remember { mutableStateOf("") }
        var colonia by remember { mutableStateOf("") }
        var calle by remember { mutableStateOf("") }
        var numeroCasa by remember { mutableStateOf("") }
        var observaciones by remember { mutableStateOf("") }

        // Cargar perfil y pedidos al inicio
        LaunchedEffect(Unit) { localAuthViewModel.getClientePerfil() }

        LaunchedEffect(clientePerfil) {
                clientePerfil?.id?.let { clienteId -> orderHistoryViewModel.loadPedidos(clienteId) }
        }

        // Actualizar campos cuando se cargue el perfil
        LaunchedEffect(clientePerfil) {
                clientePerfil?.let { perfil ->
                        nombre = perfil.nombre
                        apellidos = perfil.apellidos
                        telefono = perfil.telefono
                        colonia = perfil.colonia ?: ""
                        calle = perfil.calle ?: ""
                        numeroCasa = perfil.numeroCasa ?: ""
                        observaciones = perfil.observaciones ?: ""
                }
        }

        Column(modifier = Modifier.fillMaxSize().background(cream).padding(20.dp)) {
                // Header
                Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                ) {
                        IconButton(onClick = onBack) {
                                Icon(
                                        imageVector = Icons.Filled.ArrowBack,
                                        contentDescription = "Back",
                                        tint = brownDark
                                )
                        }
                        Text(
                                text = "Mi perfil",
                                style = MaterialTheme.typography.headlineSmall,
                                color = brownDark,
                                fontWeight = FontWeight.Bold
                        )
                        Row {
                                IconButton(onClick = { onNavigateToNotifications() }) {
                                        Icon(
                                                imageVector = Icons.Filled.Notifications,
                                                contentDescription = "Notifications",
                                                tint = brownDark
                                        )
                                }
                                IconButton(onClick = { /* TODO: edit profile */}) {
                                        Icon(
                                                imageVector = Icons.Filled.Edit,
                                                contentDescription = "Edit",
                                                tint = brownDark
                                        )
                                }
                        }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Profile card
                Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                        Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                        ) {
                                Surface(
                                        modifier = Modifier.size(84.dp).clip(CircleShape),
                                        shape = CircleShape,
                                        color = Color(0xFFF7EDEA)
                                ) {
                                        Box(
                                                contentAlignment = Alignment.Center,
                                                modifier = Modifier.fillMaxSize()
                                        ) {
                                                Text(
                                                        text =
                                                                "${nombre.firstOrNull()?.uppercaseChar() ?: "U"}",
                                                        fontSize = 28.sp,
                                                        color = brownDark,
                                                        fontWeight = FontWeight.Bold
                                                )
                                        }
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                                text = "$nombre $apellidos",
                                                style = MaterialTheme.typography.titleMedium,
                                                color = brownDark,
                                                fontWeight = FontWeight.SemiBold
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                                text = currentUser?.email
                                                                ?: clientePerfil?.usuario?.correo
                                                                        ?: "",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = brownDark.copy(alpha = 0.7f)
                                        )
                                        if (telefono.isNotEmpty()) {
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                        text = telefono,
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = brownDark.copy(alpha = 0.6f)
                                                )
                                        }
                                }

                                Button(
                                        onClick = { isEditing = !isEditing },
                                        shape = RoundedCornerShape(12.dp),
                                        colors =
                                                ButtonDefaults.buttonColors(
                                                        containerColor = terracota
                                                )
                                ) {
                                        Text(
                                                text = if (isEditing) "Cancelar" else "Editar",
                                                color = Color.White
                                        )
                                }
                        }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Formulario de edición
                if (isEditing) {
                        Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                        Text(
                                                text = "Editar información personal",
                                                style = MaterialTheme.typography.titleMedium,
                                                color = brownDark,
                                                fontWeight = FontWeight.Bold
                                        )

                                        Spacer(modifier = Modifier.height(12.dp))

                                        OutlinedTextField(
                                                value = nombre,
                                                onValueChange = { nombre = it },
                                                label = { Text("Nombre") },
                                                modifier = Modifier.fillMaxWidth(),
                                                shape = RoundedCornerShape(12.dp)
                                        )

                                        Spacer(modifier = Modifier.height(8.dp))

                                        OutlinedTextField(
                                                value = apellidos,
                                                onValueChange = { apellidos = it },
                                                label = { Text("Apellidos") },
                                                modifier = Modifier.fillMaxWidth(),
                                                shape = RoundedCornerShape(12.dp)
                                        )

                                        Spacer(modifier = Modifier.height(8.dp))

                                        OutlinedTextField(
                                                value = telefono,
                                                onValueChange = { telefono = it },
                                                label = { Text("Teléfono") },
                                                modifier = Modifier.fillMaxWidth(),
                                                shape = RoundedCornerShape(12.dp)
                                        )

                                        Spacer(modifier = Modifier.height(12.dp))

                                        Text(
                                                text = "Dirección de entrega",
                                                style = MaterialTheme.typography.titleSmall,
                                                color = brownDark,
                                                fontWeight = FontWeight.Bold
                                        )

                                        Spacer(modifier = Modifier.height(8.dp))

                                        OutlinedTextField(
                                                value = calle,
                                                onValueChange = { calle = it },
                                                label = { Text("Calle") },
                                                modifier = Modifier.fillMaxWidth(),
                                                shape = RoundedCornerShape(12.dp)
                                        )

                                        Spacer(modifier = Modifier.height(8.dp))

                                        OutlinedTextField(
                                                value = numeroCasa,
                                                onValueChange = { numeroCasa = it },
                                                label = { Text("Número") },
                                                modifier = Modifier.fillMaxWidth(),
                                                shape = RoundedCornerShape(12.dp)
                                        )

                                        Spacer(modifier = Modifier.height(8.dp))

                                        OutlinedTextField(
                                                value = colonia,
                                                onValueChange = { colonia = it },
                                                label = { Text("Colonia") },
                                                modifier = Modifier.fillMaxWidth(),
                                                shape = RoundedCornerShape(12.dp)
                                        )

                                        Spacer(modifier = Modifier.height(8.dp))

                                        OutlinedTextField(
                                                value = observaciones,
                                                onValueChange = { observaciones = it },
                                                label = { Text("Observaciones (opcional)") },
                                                modifier = Modifier.fillMaxWidth(),
                                                shape = RoundedCornerShape(12.dp),
                                                maxLines = 2
                                        )

                                        Spacer(modifier = Modifier.height(16.dp))

                                        Button(
                                                onClick = {
                                                        scope.launch {
                                                                localAuthViewModel
                                                                        .updateClientePerfil(
                                                                                nombre,
                                                                                apellidos,
                                                                                telefono,
                                                                                colonia,
                                                                                calle,
                                                                                numeroCasa,
                                                                                observaciones
                                                                        ) { isEditing = false }
                                                        }
                                                },
                                                modifier = Modifier.fillMaxWidth(),
                                                shape = RoundedCornerShape(12.dp),
                                                colors =
                                                        ButtonDefaults.buttonColors(
                                                                containerColor = terracota
                                                        ),
                                                enabled =
                                                        !loading &&
                                                                nombre.isNotBlank() &&
                                                                apellidos.isNotBlank() &&
                                                                telefono.isNotBlank()
                                        ) {
                                                Text(
                                                        text =
                                                                if (loading) "Guardando..."
                                                                else "Guardar cambios",
                                                        color = Color.White
                                                )
                                        }
                                }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                }

                // Quick stats
                Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                        StatisticCard(
                                modifier = Modifier.weight(1f),
                                number = "12",
                                label = "Pedidos",
                                background = Color(0xFFFFF4F2),
                                brown = brownDark
                        )
                        StatisticCard(
                                modifier = Modifier.weight(1f),
                                number = "4",
                                label = "Favoritos",
                                background = Color(0xFFFFF8E6),
                                brown = brownDark
                        )
                        StatisticCard(
                                modifier = Modifier.weight(1f),
                                number = "240",
                                label = "Puntos",
                                background = Color(0xFFFFF0EC),
                                brown = brownDark
                        )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Pedido activo (si existe)
                val activePedido =
                        pedidos.firstOrNull { pedido ->
                                val estado =
                                        com.example.pizzahub_mobile.data.models.EstadoPedido
                                                .fromString(pedido.estado)
                                estado !=
                                        com.example.pizzahub_mobile.data.models.EstadoPedido
                                                .ENTREGADO &&
                                        estado !=
                                                com.example.pizzahub_mobile.data.models.EstadoPedido
                                                        .CANCELADO
                        }

                if (activePedido != null) {
                        Card(
                                modifier =
                                        Modifier.fillMaxWidth().clickable {
                                                onNavigateToOrderTracking(
                                                        activePedido.id.toString()
                                                )
                                        },
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF4E8))
                        ) {
                                Row(
                                        modifier = Modifier.padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                ) {
                                        Surface(
                                                modifier = Modifier.size(50.dp),
                                                shape = CircleShape,
                                                color = terracota.copy(alpha = 0.2f)
                                        ) {
                                                Box(
                                                        contentAlignment = Alignment.Center,
                                                        modifier = Modifier.fillMaxSize()
                                                ) { Text(text = "🍕", fontSize = 24.sp) }
                                        }

                                        Spacer(modifier = Modifier.width(12.dp))

                                        Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                        text = "Pedido en curso",
                                                        fontWeight = FontWeight.Bold,
                                                        color = brownDark,
                                                        fontSize = 16.sp
                                                )
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                        text =
                                                                "Pedido #${activePedido.id} - ${activePedido.estado}",
                                                        color = brownDark.copy(alpha = 0.7f),
                                                        fontSize = 14.sp
                                                )
                                        }

                                        Icon(
                                                imageVector =
                                                        androidx.compose.material.icons.Icons.Filled
                                                                .ArrowBack,
                                                contentDescription = "Ver pedido",
                                                tint = terracota,
                                                modifier = Modifier.size(24.dp)
                                        )
                                }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                }

                // Settings / actions list
                val items =
                        listOf(
                                Pair("Mis pedidos", "P"),
                                Pair("Direcciones", "D"),
                                Pair("Notificaciones", "N")
                        )

                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                        items(items) { it ->
                                when (it.first) {
                                        "Mis pedidos" -> {
                                                Box(
                                                        modifier =
                                                                Modifier.fillMaxWidth().clickable {
                                                                        onNavigateToOrderHistory()
                                                                }
                                                ) {
                                                        SimpleCardItem(
                                                                initial = it.second,
                                                                title = it.first,
                                                                brown = brownDark
                                                        )
                                                }
                                        }
                                        "Notificaciones" -> {
                                                Box(
                                                        modifier =
                                                                Modifier.fillMaxWidth().clickable {
                                                                        onNavigateToNotifications()
                                                                }
                                                ) {
                                                        SimpleCardItem(
                                                                initial = it.second,
                                                                title = it.first,
                                                                brown = brownDark
                                                        )
                                                }
                                        }
                                        "Direccion", "Direcciones" -> {
                                                Box(
                                                        modifier =
                                                                Modifier.fillMaxWidth().clickable {
                                                                        onNavigateToAddresses()
                                                                }
                                                ) {
                                                        SimpleCardItem(
                                                                initial = it.second,
                                                                title = it.first,
                                                                brown = brownDark
                                                        )
                                                }
                                        }
                                        else -> {
                                                SimpleCardItem(
                                                        initial = it.second,
                                                        title = it.first,
                                                        brown = brownDark
                                                )
                                        }
                                }
                        }

                        item {
                                Spacer(modifier = Modifier.height(8.dp))
                                // Debug-only: show FCM token so developer can copy it for Firebase
                                // Console tests
                                val ctx = LocalContext.current
                                val isDebug =
                                        (ctx.applicationInfo.flags and
                                                ApplicationInfo.FLAG_DEBUGGABLE) != 0
                                if (isDebug) {
                                        Text(
                                                text = "Mostrar token FCM",
                                                color = terracota,
                                                modifier =
                                                        Modifier.fillMaxWidth()
                                                                .clickable {
                                                                        FirebaseMessaging
                                                                                .getInstance()
                                                                                .token
                                                                                .addOnCompleteListener {
                                                                                        task ->
                                                                                        if (task.isSuccessful
                                                                                        ) {
                                                                                                val token =
                                                                                                        task.result
                                                                                                Toast.makeText(
                                                                                                                ctx,
                                                                                                                "FCM token:\n$token",
                                                                                                                Toast.LENGTH_LONG
                                                                                                        )
                                                                                                        .show()
                                                                                        } else {
                                                                                                Toast.makeText(
                                                                                                                ctx,
                                                                                                                "Error obteniendo token FCM",
                                                                                                                Toast.LENGTH_SHORT
                                                                                                        )
                                                                                                        .show()
                                                                                        }
                                                                                }
                                                                }
                                                                .padding(vertical = 8.dp),
                                                fontWeight = FontWeight.SemiBold
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                }
                                Button(
                                        onClick = { onLogout() },
                                        colors =
                                                ButtonDefaults.buttonColors(
                                                        containerColor = terracota
                                                ),
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                                        shape = RoundedCornerShape(12.dp)
                                ) { Text(text = "Cerrar sesión", color = Color.White) }
                        }
                }
        }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
        PizzaHub_MobileTheme { ProfileScreen(onBack = {}) }
}

@Composable
private fun SimpleCardItem(initial: String, title: String, brown: Color) {
        Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                shape = RoundedCornerShape(12.dp)
        ) {
                Row(
                        modifier = Modifier.padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                        modifier =
                                                Modifier.size(40.dp)
                                                        .clip(CircleShape)
                                                        .background(Color(0xFFFFF8F6)),
                                        contentAlignment = Alignment.Center
                                ) { Text(text = initial, color = PizzaBrown) }
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                        text = title,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = brown
                                )
                        }

                        Text(text = ">", color = Color(0xFF8A8A8A))
                }
        }
}

@Composable
private fun StatisticCard(
        modifier: Modifier = Modifier,
        number: String,
        label: String,
        background: Color,
        brown: Color
) {
        Card(
                modifier = modifier.height(88.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = background)
        ) {
                Column(
                        modifier = Modifier.fillMaxSize().padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                ) {
                        Text(
                                text = number,
                                style = MaterialTheme.typography.titleMedium,
                                color = brown,
                                fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                                text = label,
                                style = MaterialTheme.typography.bodySmall,
                                color = brown
                        )
                }
        }
}
