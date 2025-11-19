package com.example.pizzahub_mobile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pizzahub_mobile.data.models.EstadoPedido
import com.example.pizzahub_mobile.ui.theme.PizzaHub_MobileTheme
import com.example.pizzahub_mobile.ui.viewmodel.OrderDetailViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun OrderDetailScreen(
        orderId: String,
        onBack: () -> Unit,
        onRepeat: (String) -> Unit = {},
        onRate: (String) -> Unit = {},
        viewModel: OrderDetailViewModel = viewModel()
) {
        val cream = Color(0xFFFFF8EE)
        val softBeige = Color(0xFFFFF2D5)
        val brownDark = Color(0xFF4E342E)
        val terracota = Color(0xFFD35400)

        val pedido by viewModel.pedido.collectAsState()
        val isLoading by viewModel.isLoading.collectAsState()
        val error by viewModel.error.collectAsState()

        LaunchedEffect(orderId) { orderId.toIntOrNull()?.let { id -> viewModel.loadPedido(id) } }

        Column(
                modifier =
                        Modifier.fillMaxSize()
                                .background(Brush.verticalGradient(listOf(cream, Color.White)))
                                .padding(16.dp)
        ) {
                // Título centrado
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
                                "Detalle del pedido",
                                color = brownDark,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                        )
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (isLoading) {
                        Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                        ) { CircularProgressIndicator(color = terracota) }
                } else if (error != null) {
                        Box(
                                modifier = Modifier.fillMaxSize(),
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
                        OrderDetailContent(
                                pedido = pedido!!,
                                onRepeat = onRepeat,
                                onRate = onRate,
                                brownDark = brownDark,
                                softBeige = softBeige,
                                terracota = terracota
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
private fun OrderDetailContent(
        pedido: com.example.pizzahub_mobile.data.models.PedidoResponse,
        onRepeat: (String) -> Unit,
        onRate: (String) -> Unit,
        brownDark: Color,
        softBeige: Color,
        terracota: Color
) {
        val numberFormat = NumberFormat.getCurrencyInstance(Locale("es", "MX"))
        val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("es", "MX"))

        val formattedDate =
                try {
                        val parsedDate =
                                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
                                        .parse(pedido.fechaPedido ?: "")
                        parsedDate?.let { dateFormat.format(it) } ?: pedido.fechaPedido
                } catch (e: Exception) {
                        pedido.fechaPedido
                }

        val estadoPedido = EstadoPedido.fromString(pedido.estado)
        val estadoColor =
                when (estadoPedido) {
                        EstadoPedido.ENTREGADO -> Color(0xFF4CAF50)
                        EstadoPedido.EN_PREPARACION -> Color(0xFFFF9800)
                        EstadoPedido.EN_CAMINO -> Color(0xFF2196F3)
                        else -> Color(0xFFFFC107)
                }

        Column(modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
                Card(
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth().shadow(3.dp, RoundedCornerShape(16.dp)),
                        colors = CardDefaults.cardColors(containerColor = softBeige)
                ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.fillMaxWidth()
                                ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                        "Pedido #${pedido.id}",
                                                        fontWeight = FontWeight.SemiBold,
                                                        color = brownDark,
                                                        fontSize = 18.sp
                                                )
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                        formattedDate ?: "Sin fecha",
                                                        color = brownDark.copy(alpha = 0.6f),
                                                        fontSize = 12.sp
                                                )
                                        }
                                        Surface(
                                                shape = RoundedCornerShape(12.dp),
                                                color = estadoColor.copy(alpha = 0.2f)
                                        ) {
                                                Text(
                                                        pedido.estado,
                                                        modifier =
                                                                Modifier.padding(
                                                                        horizontal = 12.dp,
                                                                        vertical = 6.dp
                                                                ),
                                                        color = estadoColor,
                                                        fontSize = 13.sp,
                                                        fontWeight = FontWeight.SemiBold
                                                )
                                        }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // Productos
                                Text(
                                        "Productos",
                                        fontWeight = FontWeight.Bold,
                                        color = brownDark,
                                        fontSize = 16.sp
                                )
                                Spacer(modifier = Modifier.height(12.dp))

                                pedido.detalles?.forEach { detalle ->
                                        Row(
                                                modifier =
                                                        Modifier.fillMaxWidth()
                                                                .padding(vertical = 8.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                        ) {
                                                Box(
                                                        modifier =
                                                                Modifier.size(56.dp)
                                                                        .background(
                                                                                Color(0xFFFFE0B2),
                                                                                shape =
                                                                                        RoundedCornerShape(
                                                                                                10.dp
                                                                                        )
                                                                        ),
                                                        contentAlignment = Alignment.Center
                                                ) { Text("🍕", fontSize = 24.sp) }
                                                Spacer(modifier = Modifier.width(12.dp))
                                                Column(modifier = Modifier.weight(1f)) {
                                                        Text(
                                                                detalle.productoNombre
                                                                        ?: "Producto",
                                                                color = brownDark,
                                                                fontWeight = FontWeight.SemiBold
                                                        )
                                                        Text(
                                                                "Cantidad: ${detalle.cantidad}",
                                                                color =
                                                                        brownDark.copy(
                                                                                alpha = 0.7f
                                                                        ),
                                                                fontSize = 12.sp
                                                        )
                                                }
                                                Text(
                                                        numberFormat.format(detalle.subtotal),
                                                        color = brownDark,
                                                        fontWeight = FontWeight.SemiBold
                                                )
                                        }
                                }

                                Divider(modifier = Modifier.padding(vertical = 12.dp))

                                // Información adicional
                                if (!pedido.direccionEntrega.isNullOrEmpty()) {
                                        Row(
                                                modifier =
                                                        Modifier.fillMaxWidth()
                                                                .padding(vertical = 4.dp)
                                        ) {
                                                Text(
                                                        "Dirección:",
                                                        color = brownDark.copy(alpha = 0.7f),
                                                        fontSize = 13.sp,
                                                        modifier = Modifier.width(100.dp)
                                                )
                                                Text(
                                                        pedido.direccionEntrega!!,
                                                        color = brownDark,
                                                        fontSize = 13.sp,
                                                        modifier = Modifier.weight(1f)
                                                )
                                        }
                                }

                                if (!pedido.observaciones.isNullOrEmpty()) {
                                        Row(
                                                modifier =
                                                        Modifier.fillMaxWidth()
                                                                .padding(vertical = 4.dp)
                                        ) {
                                                Text(
                                                        "Observaciones:",
                                                        color = brownDark.copy(alpha = 0.7f),
                                                        fontSize = 13.sp,
                                                        modifier = Modifier.width(100.dp)
                                                )
                                                Text(
                                                        pedido.observaciones!!,
                                                        color = brownDark,
                                                        fontSize = 13.sp,
                                                        modifier = Modifier.weight(1f)
                                                )
                                        }
                                }

                                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                                        Text(
                                                "Método de pago:",
                                                color = brownDark.copy(alpha = 0.7f),
                                                fontSize = 13.sp,
                                                modifier = Modifier.width(100.dp)
                                        )
                                        Text(pedido.metodoPago, color = brownDark, fontSize = 13.sp)
                                }

                                Divider(modifier = Modifier.padding(vertical = 12.dp))

                                Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                        Text(
                                                "Total",
                                                color = brownDark,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 18.sp
                                        )
                                        Text(
                                                numberFormat.format(pedido.total),
                                                fontWeight = FontWeight.Bold,
                                                color = terracota,
                                                fontSize = 20.sp
                                        )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                        Button(
                                                onClick = { onRepeat(pedido.id.toString()) },
                                                modifier = Modifier.weight(1f),
                                                colors =
                                                        ButtonDefaults.buttonColors(
                                                                containerColor = Color(0xFFFFE0B2)
                                                        )
                                        ) {
                                                Icon(
                                                        Icons.Filled.Refresh,
                                                        contentDescription = "Repetir",
                                                        tint = terracota
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text("Repetir", color = terracota)
                                        }
                                        Button(
                                                onClick = { onRate(pedido.id.toString()) },
                                                modifier = Modifier.weight(1f),
                                                colors =
                                                        ButtonDefaults.buttonColors(
                                                                containerColor = terracota
                                                        )
                                        ) {
                                                Icon(
                                                        Icons.Filled.Star,
                                                        contentDescription = "Calificar",
                                                        tint = Color.White
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text("Calificar", color = Color.White)
                                        }
                                }
                        }
                }
        }
}

@Preview(showBackground = true)
@Composable
fun OrderDetailPreview() {
        PizzaHub_MobileTheme { OrderDetailScreen(orderId = "1", onBack = {}) }
}
