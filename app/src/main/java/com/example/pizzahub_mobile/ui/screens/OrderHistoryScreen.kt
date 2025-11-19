package com.example.pizzahub_mobile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pizzahub_mobile.data.models.EstadoPedido
import com.example.pizzahub_mobile.data.models.PedidoResponse
import com.example.pizzahub_mobile.ui.theme.PizzaHub_MobileTheme
import com.example.pizzahub_mobile.ui.viewmodel.OrderHistoryViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale
import kotlinx.coroutines.launch

@Composable
fun OrderHistoryScreen(
        onBack: () -> Unit,
        onOpenDetail: (String) -> Unit = {},
        onRepeatOrder: (String) -> Unit = {},
        viewModel: OrderHistoryViewModel = viewModel(),
        authViewModel: com.example.pizzahub_mobile.ui.viewmodel.AuthViewModel = viewModel()
) {
        val cream = Color(0xFFFFF8EE)
        val softBeige = Color(0xFFFFF2D5)
        val brownDark = Color(0xFF4E342E)
        val terracota = Color(0xFFD35400)

        val pedidos by viewModel.pedidos.collectAsState()
        val isLoading by viewModel.isLoading.collectAsState()
        val error by viewModel.error.collectAsState()
        val clientePerfil by authViewModel.clientePerfil.collectAsState()

        val scope = rememberCoroutineScope()

        LaunchedEffect(Unit) { authViewModel.getClientePerfil() }

        LaunchedEffect(clientePerfil) {
                clientePerfil?.id?.let { clienteId -> viewModel.loadPedidos(clienteId) }
        }

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
                                        imageVector = Icons.Filled.ArrowBack,
                                        contentDescription = "Volver",
                                        tint = brownDark
                                )
                        }
                        Text(
                                "Historial de pedidos",
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
                                                "Error al cargar pedidos",
                                                color = Color.Red,
                                                fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(error ?: "", color = brownDark)
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Button(
                                                onClick = {
                                                        scope.launch {
                                                                authViewModel.getClientePerfil()
                                                                clientePerfil?.id?.let {
                                                                        viewModel.loadPedidos(it)
                                                                }
                                                        }
                                                },
                                                colors =
                                                        ButtonDefaults.buttonColors(
                                                                containerColor = terracota
                                                        )
                                        ) { Text("Reintentar") }
                                }
                        }
                } else if (pedidos.isEmpty()) {
                        Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                        ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                                "No tienes pedidos aún",
                                                color = brownDark,
                                                fontSize = 18.sp
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                                "¡Haz tu primer pedido!",
                                                color = brownDark.copy(alpha = 0.7f)
                                        )
                                }
                        }
                } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                items(pedidos) { pedido ->
                                        OrderHistoryCard(
                                                pedido = pedido,
                                                onOpenDetail = onOpenDetail,
                                                onRepeatOrder = onRepeatOrder,
                                                brownDark = brownDark,
                                                softBeige = softBeige,
                                                terracota = terracota
                                        )
                                }
                        }
                }
        }
}

@Composable
private fun OrderHistoryCard(
        pedido: PedidoResponse,
        onOpenDetail: (String) -> Unit,
        onRepeatOrder: (String) -> Unit,
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
                        EstadoPedido.ENTREGADO -> Color(0xFFDFF6E9)
                        EstadoPedido.EN_PREPARACION -> Color(0xFFFFF4E8)
                        EstadoPedido.EN_CAMINO -> Color(0xFFE8F4FF)
                        else -> Color(0xFFFFF8EE)
                }

        Card(
                modifier =
                        Modifier.fillMaxWidth().shadow(3.dp, RoundedCornerShape(14.dp)).clickable {
                                onOpenDetail(pedido.id.toString())
                        },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = softBeige)
        ) {
                Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                ) {
                        Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("Pedido ", color = brownDark.copy(alpha = 0.8f))
                                        Text(
                                                "#${pedido.id}",
                                                fontWeight = FontWeight.SemiBold,
                                                color = brownDark
                                        )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                        formattedDate ?: "Sin fecha",
                                        color = brownDark.copy(alpha = 0.7f),
                                        fontSize = 13.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(6.dp))

                                // Mostrar productos
                                pedido.detalles?.take(2)?.forEach { detalle ->
                                        Text(
                                                "${detalle.productoNombre} x${detalle.cantidad}",
                                                color = brownDark.copy(alpha = 0.6f),
                                                fontSize = 12.sp,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                        )
                                }
                                if ((pedido.detalles?.size ?: 0) > 2) {
                                        Text(
                                                "+${pedido.detalles!!.size - 2} más",
                                                color = brownDark.copy(alpha = 0.5f),
                                                fontSize = 11.sp,
                                                fontStyle =
                                                        androidx.compose.ui.text.font.FontStyle
                                                                .Italic
                                        )
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                Surface(shape = RoundedCornerShape(12.dp), color = estadoColor) {
                                        Text(
                                                pedido.estado,
                                                modifier =
                                                        Modifier.padding(
                                                                horizontal = 10.dp,
                                                                vertical = 6.dp
                                                        ),
                                                color = brownDark,
                                                fontSize = 12.sp
                                        )
                                }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                                Text(
                                        numberFormat.format(pedido.total),
                                        fontWeight = FontWeight.Bold,
                                        color = brownDark
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                TextButton(onClick = { onRepeatOrder(pedido.id.toString()) }) {
                                        Icon(
                                                Icons.Filled.Refresh,
                                                contentDescription = "Repetir",
                                                tint = terracota
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Repetir", color = terracota)
                                }
                        }
                }
        }
}

@Preview(showBackground = true)
@Composable
fun OrderHistoryPreview() {
        PizzaHub_MobileTheme { OrderHistoryScreen(onBack = {}) }
}
