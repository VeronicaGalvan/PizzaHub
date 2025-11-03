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
fun NotificationsScreen(onBack: () -> Unit) {
    val cream = Color(0xFFFFF8EE)
    val brownDark = Color(0xFF4E342E)
    val terracota = Color(0xFFD35400)
    val softBeige = Color(0xFFFFEEDD)

    val notifications = remember {
        mutableStateListOf(
                NotificationItem("n1", "Pedido actualizado", "Tu pedido #o1 está en preparación"),
                NotificationItem("n2", "Promoción", "🎉 2x1 en pizzas medianas solo por hoy"),
                NotificationItem("n3", "En camino", "🚗 Tu pedido #o2 ya está en ruta", true)
        )
    }

    Column(modifier = Modifier.fillMaxSize().background(cream)) {
        Box(
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                contentAlignment = Alignment.Center
        ) {
            IconButton(onClick = onBack, modifier = Modifier.align(Alignment.CenterStart)) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Volver", tint = brownDark)
            }
            Text("Notificaciones", color = brownDark, fontWeight = FontWeight.Bold)
        }

        LazyColumn(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(notifications, key = { it.id }) { n ->
                Card(
                        modifier =
                                Modifier.fillMaxWidth().clickable {
                                    val i = notifications.indexOfFirst { it.id == n.id }
                                    if (i >= 0)
                                            notifications[i] = notifications[i].copy(read = true)
                                },
                        shape = RoundedCornerShape(14.dp),
                        colors =
                                CardDefaults.cardColors(
                                        containerColor =
                                                if (n.read) softBeige else Color(0xFFFFE4CC)
                                )
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(n.title, fontWeight = FontWeight.SemiBold, color = brownDark)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(n.body, color = brownDark.copy(alpha = 0.8f), fontSize = 13.sp)
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
