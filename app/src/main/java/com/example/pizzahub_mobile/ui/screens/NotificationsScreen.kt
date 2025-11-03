package com.example.pizzahub_mobile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pizzahub_mobile.ui.theme.PizzaHub_MobileTheme

data class NotificationItem(
        val id: String,
        val title: String,
        val body: String,
        var read: Boolean = false
)

@Composable
fun NotificationsScreen(onBack: () -> Unit, modifier: Modifier = Modifier) {
    val cream = Color(0xFFFFF8EE)
    val brownDark = Color(0xFF4E342E)
    val terracota = Color(0xFFD35400)

    val notifications = remember {
        mutableStateListOf(
                NotificationItem(
                        "n1",
                        "Pedido actualizado",
                        "Tu pedido #o1 está en preparación",
                        false
                ),
                NotificationItem("n2", "Promoción", "2x1 en pizzas medianas hoy", false),
                NotificationItem(
                        "n3",
                        "En camino",
                        "El repartidor está en ruta para tu pedido #o2",
                        true
                )
        )
    }

    Column(modifier = modifier.fillMaxSize().background(cream)) {
        // Local top bar to keep this file self-contained
        Row(
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
            }
            Text(
                    text = "Notificaciones",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 8.dp)
            )
        }

        LazyColumn(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(notifications, key = { it.id }) { n ->
                Card(
                        modifier =
                                Modifier.fillMaxWidth().clickable {
                                    // toggle read on click (UI only)
                                    val idx = notifications.indexOfFirst { it.id == n.id }
                                    if (idx >= 0)
                                            notifications[idx] =
                                                    notifications[idx].copy(read = true)
                                },
                        shape = RoundedCornerShape(12.dp),
                        colors =
                                CardDefaults.cardColors(
                                        containerColor =
                                                if (n.read) Color.White else Color(0xFFFFFBF6)
                                )
                ) {
                    Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                    text = n.title,
                                    fontWeight = FontWeight.SemiBold,
                                    color = brownDark
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                    text = n.body,
                                    color = brownDark.copy(alpha = 0.8f),
                                    fontSize = 13.sp
                            )
                        }
                        if (!n.read) {
                            Surface(shape = RoundedCornerShape(8.dp), color = terracota) {
                                Text(
                                        text = "Nuevo",
                                        modifier =
                                                Modifier.padding(
                                                        horizontal = 8.dp,
                                                        vertical = 6.dp
                                                ),
                                        color = Color.White
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
