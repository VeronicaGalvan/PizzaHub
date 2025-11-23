package com.example.pizzahub_mobile.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pizzahub_mobile.data.models.EstadoPedido
import com.example.pizzahub_mobile.ui.theme.PizzaHub_MobileTheme
import com.example.pizzahub_mobile.ui.viewmodel.OrderTrackingViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun OrderTrackingScreen(
        onBack: () -> Unit,
        orderId: String,
        onOpenMap: (String, String) -> Unit = { _, _ -> },
        onViewDetails: (String) -> Unit = {},
        viewModel: OrderTrackingViewModel = viewModel()
) {
        val terracota = Color(0xFFD35400)
        val cream = Color(0xFFFFF8EE)
        val brownDark = Color(0xFF4E342E)
        val softBeige = Color(0xFFFFEEDD)

        val pedido by viewModel.pedido.collectAsState()
        val isLoading by viewModel.isLoading.collectAsState()
        val error by viewModel.error.collectAsState()

        LaunchedEffect(orderId) { orderId.toIntOrNull()?.let { id -> viewModel.loadPedido(id) } }

        Column(modifier = Modifier.fillMaxSize().background(cream)) {
                if (isLoading) {
                        Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                        ) { CircularProgressIndicator(color = terracota) }
                } else if (error != null) {
                        Box(
                                modifier = Modifier.fillMaxSize().padding(16.dp),
                                contentAlignment = Alignment.Center
                        ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                                "Error al cargar el pedido",
                                                color = Color.Red,
                                                fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(error ?: "", color = brownDark)
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Button(
                                                onClick = {
                                                        orderId.toIntOrNull()?.let {
                                                                viewModel.loadPedido(it)
                                                        }
                                                },
                                                colors =
                                                        ButtonDefaults.buttonColors(
                                                                containerColor = terracota
                                                        )
                                        ) { Text("Reintentar") }
                                }
                        }
                } else if (pedido != null) {
                        OrderTrackingContent(
                                pedido = pedido!!,
                                onBack = onBack,
                                onOpenMap = onOpenMap,
                                onViewDetails = onViewDetails,
                                terracota = terracota,
                                cream = cream,
                                brownDark = brownDark,
                                softBeige = softBeige
                        )
                } else {
                        Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                        ) { Text("No se encontró el pedido", color = brownDark) }
                }
        }
}

@Composable
fun StepIndicator(label: String, step: Int, current: Int, brown: Color, activeColor: Color) {
        val isActive = step <= current
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(contentAlignment = Alignment.Center) {
                        Canvas(modifier = Modifier.size(48.dp)) {
                                drawCircle(
                                        color = if (isActive) activeColor else Color(0xFFF4E6E0),
                                        radius = size.minDimension / 2
                                )
                        }
                        if (isActive && step < current)
                                Text("✓", color = Color.White, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(label, color = brown, fontSize = 12.sp)
        }
}

@Composable
private fun OrderTrackingContent(
        pedido: com.example.pizzahub_mobile.data.models.PedidoResponse,
        onBack: () -> Unit,
        onOpenMap: (String, String) -> Unit,
        onViewDetails: (String) -> Unit,
        terracota: Color,
        cream: Color,
        brownDark: Color,
        softBeige: Color
) {
        val currentStep =
                when (EstadoPedido.fromString(pedido.estado)) {
                        EstadoPedido.PENDIENTE -> 1
                        EstadoPedido.EN_PREPARACION -> 1
                        EstadoPedido.EN_CAMINO -> 2
                        EstadoPedido.ENTREGADO -> 3
                        else -> 1
                }

        val numberFormat = NumberFormat.getCurrencyInstance(Locale("es", "MX"))

        Column(
                modifier =
                        Modifier.fillMaxSize()
                                .background(cream)
                                .padding(16.dp)
                                .verticalScroll(rememberScrollState())
        ) {
                // Header centrado
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
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                        "Tu pedido",
                                        color = brownDark,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 20.sp
                                )
                                Text(
                                        "#${pedido.id}",
                                        color = brownDark.copy(alpha = 0.7f),
                                        fontSize = 13.sp
                                )
                        }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Card resumen
                Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = softBeige),
                        modifier = Modifier.fillMaxWidth()
                ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                                // Mostrar cada producto del pedido
                                pedido.detalles?.forEach { detalle ->
                                        Row(
                                                modifier =
                                                        Modifier.fillMaxWidth()
                                                                .padding(vertical = 4.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                        ) {
                                                Box(
                                                        modifier =
                                                                Modifier.size(48.dp)
                                                                        .background(
                                                                                Color(0xFFF6E1C3),
                                                                                RoundedCornerShape(
                                                                                        10.dp
                                                                                )
                                                                        ),
                                                        contentAlignment = Alignment.Center
                                                ) { Text("🍕", fontSize = 20.sp) }

                                                Spacer(modifier = Modifier.width(12.dp))

                                                Column(modifier = Modifier.weight(1f)) {
                                                        Text(
                                                                "${detalle.productoNombre} x${detalle.cantidad}",
                                                                color = brownDark,
                                                                fontWeight = FontWeight.SemiBold,
                                                                fontSize = 14.sp
                                                        )
                                                        Text(
                                                                numberFormat.format(
                                                                        detalle.subtotal
                                                                ),
                                                                color =
                                                                        brownDark.copy(
                                                                                alpha = 0.7f
                                                                        ),
                                                                fontSize = 13.sp
                                                        )
                                                }
                                        }
                                }

                                if (pedido.detalles?.isNotEmpty() == true) {
                                        Divider(
                                                modifier = Modifier.padding(vertical = 8.dp),
                                                color = brownDark.copy(alpha = 0.2f)
                                        )
                                }

                                Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                        Column {
                                                Text(
                                                        "Total",
                                                        color = brownDark,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 16.sp
                                                )
                                                Text(
                                                        "Método: ${pedido.metodoPago}",
                                                        color = brownDark.copy(alpha = 0.7f),
                                                        fontSize = 12.sp
                                                )
                                        }
                                        Text(
                                                numberFormat.format(pedido.total),
                                                color = terracota,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 18.sp
                                        )
                                }
                        }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Progreso del pedido
                Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                ) {
                        StepIndicator("Preparación", 1, currentStep, brownDark, terracota)
                        StepIndicator("En camino", 2, currentStep, brownDark, terracota)
                        StepIndicator("Entregado", 3, currentStep, brownDark, terracota)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                        text =
                                when (currentStep) {
                                        1 -> "Tu pedido está en preparación 🧑‍🍳"
                                        2 -> "Tu pedido está en camino 🚗"
                                        else -> "Pedido entregado ✅"
                                },
                        color = brownDark,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Dirección de entrega
                if (!pedido.direccionEntrega.isNullOrEmpty()) {
                        Card(
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(containerColor = softBeige),
                                modifier = Modifier.fillMaxWidth()
                        ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                        Icons.Filled.Place,
                                                        contentDescription = "Dirección",
                                                        tint = terracota,
                                                        modifier = Modifier.size(20.dp)
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                        "Dirección de entrega",
                                                        color = brownDark,
                                                        fontWeight = FontWeight.SemiBold
                                                )
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                                pedido.direccionEntrega!!,
                                                color = brownDark.copy(alpha = 0.8f),
                                                fontSize = 14.sp
                                        )
                                        if (!pedido.observaciones.isNullOrEmpty()) {
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                        "Observaciones: ${pedido.observaciones}",
                                                        color = brownDark.copy(alpha = 0.7f),
                                                        fontSize = 13.sp,
                                                        fontStyle =
                                                                androidx.compose.ui.text.font
                                                                        .FontStyle.Italic
                                                )
                                        }
                                }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                }

                // Info del repartidor (solo si hay repartidor asignado y pedido en camino)
                if (pedido.repartidorNombre != null && currentStep >= 2) {
                        Card(
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(containerColor = softBeige),
                                modifier = Modifier.fillMaxWidth()
                        ) {
                                Row(
                                        modifier = Modifier.padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                ) {
                                        Box(
                                                modifier =
                                                        Modifier.size(56.dp)
                                                                .background(
                                                                        Color(0xFFF6E1C3),
                                                                        CircleShape
                                                                ),
                                                contentAlignment = Alignment.Center
                                        ) { Text("🚴", fontSize = 24.sp) }

                                        Spacer(modifier = Modifier.width(12.dp))

                                        Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                        "Repartidor: ${pedido.repartidorNombre}",
                                                        color = brownDark,
                                                        fontWeight = FontWeight.SemiBold
                                                )
                                                Text(
                                                        "En camino a tu ubicación",
                                                        color = brownDark.copy(alpha = 0.7f),
                                                        fontSize = 13.sp
                                                )
                                        }

                                        Row {
                                                IconButton(onClick = { /* call */}) {
                                                        Icon(
                                                                Icons.Filled.Phone,
                                                                contentDescription = "Llamar",
                                                                tint = terracota
                                                        )
                                                }
                                                IconButton(
                                                        onClick = {
                                                                onOpenMap(
                                                                        pedido.id.toString(),
                                                                        pedido.estado
                                                                )
                                                        }
                                                ) {
                                                        Icon(
                                                                Icons.Filled.Place,
                                                                contentDescription = "Ver mapa",
                                                                tint = terracota
                                                        )
                                                }
                                        }
                                }
                        }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Botones inferiores
                Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                        OutlinedButton(
                                onClick = { onViewDetails(pedido.id.toString()) },
                                modifier = Modifier.weight(1f),
                                colors =
                                        ButtonDefaults.outlinedButtonColors(
                                                contentColor = terracota
                                        )
                        ) { Text("Ver detalles") }

                        Button(
                                onClick = { onOpenMap(pedido.id.toString(), pedido.estado) },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = terracota)
                        ) {
                                Icon(
                                        Icons.Filled.Place,
                                        contentDescription = "Mapa",
                                        modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Ver mapa", color = Color.White)
                        }
                }
        }
}

@Preview(showBackground = true)
@Composable
fun OrderTrackingScreenPreview() {
        PizzaHub_MobileTheme { OrderTrackingScreen(onBack = {}, orderId = "1") }
}
