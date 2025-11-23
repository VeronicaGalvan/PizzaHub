package com.example.pizzahub_mobile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
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
import com.example.pizzahub_mobile.ui.theme.PizzaHub_MobileTheme
import com.example.pizzahub_mobile.ui.viewmodel.NotificationsViewModel

@Composable
fun NotificationsScreen(onBack: () -> Unit, onNavigateToOrder: (String) -> Unit = {}) {
    val viewModel: NotificationsViewModel = viewModel()
    val notificaciones by viewModel.notificaciones.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    val cream = Color(0xFFFFF8EE)
    val brownDark = Color(0xFF4E342E)
    val terracota = Color(0xFFD35400)
    val softBeige = Color(0xFFFFEEDD)

    LaunchedEffect(error) {
        if (error != null) {
            // Podrías mostrar un Snackbar aquí
            viewModel.clearError()
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(cream)) {
        // Top bar con botón de marcar todas como leídas
        Box(
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                contentAlignment = Alignment.Center
        ) {
            IconButton(onClick = onBack, modifier = Modifier.align(Alignment.CenterStart)) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Volver", tint = brownDark)
            }
            Text("Notificaciones", color = brownDark, fontWeight = FontWeight.Bold)

            // Botón para marcar todas como leídas
            if (notificaciones.any { !it.leida }) {
                IconButton(
                        onClick = { viewModel.marcarTodasLeidas() },
                        modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    Icon(
                            Icons.Filled.CheckCircle,
                            contentDescription = "Marcar todas leídas",
                            tint = terracota
                    )
                }
            }
        }

        if (isLoading && notificaciones.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = terracota)
            }
        } else if (notificaciones.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                        "No hay notificaciones",
                        color = brownDark.copy(alpha = 0.6f),
                        fontSize = 14.sp
                )
            }
        } else {
            LazyColumn(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(notificaciones, key = { it.id }) { notif ->
                    Card(
                            modifier =
                                    Modifier.fillMaxWidth().clickable {
                                        // Marcar como leída
                                        if (!notif.leida) {
                                            viewModel.marcarLeida(notif.id)
                                        }

                                        // Si es notificación de pedido, navegar
                                        notif.pedidoId?.let { pedidoId ->
                                            onNavigateToOrder(pedidoId.toString())
                                        }
                                    },
                            shape = RoundedCornerShape(14.dp),
                            colors =
                                    CardDefaults.cardColors(
                                            containerColor =
                                                    if (notif.leida) softBeige
                                                    else Color(0xFFFFE4CC)
                                    )
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                        notif.titulo,
                                        fontWeight = FontWeight.SemiBold,
                                        color = brownDark,
                                        modifier = Modifier.weight(1f)
                                )
                                if (!notif.leida) {
                                    Box(
                                            modifier =
                                                    Modifier.size(8.dp)
                                                            .background(
                                                                    terracota,
                                                                    shape =
                                                                            androidx.compose
                                                                                    .foundation
                                                                                    .shape
                                                                                    .CircleShape
                                                            )
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                    notif.mensaje,
                                    color = brownDark.copy(alpha = 0.8f),
                                    fontSize = 13.sp
                            )

                            // Mostrar tipo y fecha si están disponibles
                            notif.tipo?.let { tipo ->
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                        tipo.uppercase(),
                                        fontSize = 11.sp,
                                        color = terracota,
                                        fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NotificationsPreview() {
    PizzaHub_MobileTheme { NotificationsScreen(onBack = {}) }
}
