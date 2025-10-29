package com.example.pizzahub_mobile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pizzahub_mobile.ui.theme.PizzaBrown
import com.example.pizzahub_mobile.ui.theme.PizzaHub_MobileTheme

@Composable
fun ProfileScreen(onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back")
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = "Ulises", style = MaterialTheme.typography.titleLarge, color = PizzaBrown)
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(
                    modifier = Modifier.size(72.dp).clip(CircleShape).background(Color(0xFFF7EDEA)),
                    contentAlignment = Alignment.Center
            ) { Text(text = "U", style = MaterialTheme.typography.titleMedium, color = PizzaBrown) }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                        text = "Ulises",
                        style = MaterialTheme.typography.titleMedium,
                        color = PizzaBrown
                )
                Text(
                        text = "Ver perfil",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color(0xFF8A8A8A)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        SimpleCardItem(initial = "P", title = "Mis pedidos")
        SimpleCardItem(initial = "M", title = "Metodo de pago")
        SimpleCardItem(initial = "D", title = "Direccion")
        SimpleCardItem(initial = "N", title = "Notificaciones")
        SimpleCardItem(initial = "S", title = "Soporte")
        SimpleCardItem(initial = "C", title = "Cerrar sesión")
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    PizzaHub_MobileTheme { ProfileScreen(onBack = {}) }
}

@Composable
private fun SimpleCardItem(initial: String, title: String) {
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
                Text(text = title, style = MaterialTheme.typography.bodyLarge, color = PizzaBrown)
            }

            Text(text = ">", color = Color(0xFF8A8A8A))
        }
    }
}
