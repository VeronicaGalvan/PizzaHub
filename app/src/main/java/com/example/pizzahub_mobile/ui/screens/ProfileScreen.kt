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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pizzahub_mobile.ui.theme.PizzaBrown
import com.example.pizzahub_mobile.ui.theme.PizzaHub_MobileTheme
import com.google.firebase.messaging.FirebaseMessaging

@Composable
fun ProfileScreen(
        onBack: () -> Unit,
        onNavigateToOrderHistory: () -> Unit = {},
        onNavigateToNotifications: () -> Unit = {},
        onNavigateToAddresses: () -> Unit = {},
        onLogout: () -> Unit = {}
) {
        // Palette consistent with HomeScreen
        val terracota = Color(0xFFC0392B)
        val cream = Color(0xFFFFF4E8)
        val brownDark = Color(0xFF4E342E)

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
                                                        text = "U",
                                                        fontSize = 28.sp,
                                                        color = brownDark,
                                                        fontWeight = FontWeight.Bold
                                                )
                                        }
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                                text = "Ulises González",
                                                style = MaterialTheme.typography.titleMedium,
                                                color = brownDark,
                                                fontWeight = FontWeight.SemiBold
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                                text = "ulises@example.com",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = brownDark.copy(alpha = 0.7f)
                                        )
                                }

                                Button(
                                        onClick = { /* edit */},
                                        shape = RoundedCornerShape(12.dp),
                                        colors =
                                                ButtonDefaults.buttonColors(
                                                        containerColor = terracota
                                                )
                                ) { Text(text = "Editar", color = Color.White) }
                        }
                }

                Spacer(modifier = Modifier.height(16.dp))

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
