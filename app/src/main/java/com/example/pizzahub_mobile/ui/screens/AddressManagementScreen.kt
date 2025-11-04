package com.example.pizzahub_mobile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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
import com.example.pizzahub_mobile.ui.theme.PizzaHub_MobileTheme

data class AddressItem(
        val id: String,
        val label: String,
        val street: String,
        val city: String,
        val isDefault: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressManagementScreen(
        onBack: () -> Unit,
        onSelect: (AddressItem) -> Unit = {},
        modifier: Modifier = Modifier
) {
    val brownDark = Color(0xFF4E342E)
    val terracota = Color(0xFFD35400)
    val cream = Color(0xFFFFF8EE)
    val softBeige = Color(0xFFFFEEDD)

    val addresses = remember {
        mutableStateListOf(
                AddressItem("a1", "Casa", "Calle Falsa 123", "Ciudad", true),
                AddressItem("a2", "Trabajo", "Av. Siempre Viva 742", "Ciudad", false)
        )
    }

    Box(
            modifier =
                    modifier.fillMaxSize()
                            .background(Brush.verticalGradient(listOf(cream, Color.White)))
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            // Encabezado centrado
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                IconButton(onClick = onBack, modifier = Modifier.align(Alignment.CenterStart)) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Volver", tint = brownDark)
                }
                Text(
                        text = "Mis direcciones",
                        fontWeight = FontWeight.Bold,
                        color = brownDark,
                        fontSize = 20.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Lista de direcciones
            LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(addresses, key = { it.id }) { addr ->
                    Card(
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = softBeige),
                            modifier =
                                    Modifier.fillMaxWidth().shadow(2.dp, RoundedCornerShape(18.dp))
                    ) {
                        Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                            text = addr.label,
                                            fontWeight = FontWeight.SemiBold,
                                            color = brownDark
                                    )
                                    if (addr.isDefault) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Surface(
                                                shape = RoundedCornerShape(8.dp),
                                                color = terracota
                                        ) {
                                            Text(
                                                    text = "Predeterminada",
                                                    color = Color.White,
                                                    fontSize = 12.sp,
                                                    modifier =
                                                            Modifier.padding(
                                                                    horizontal = 6.dp,
                                                                    vertical = 3.dp
                                                            )
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(addr.street, color = brownDark.copy(alpha = 0.9f))
                                Text(
                                        addr.city,
                                        color = brownDark.copy(alpha = 0.7f),
                                        fontSize = 13.sp
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    IconButton(onClick = { /* edit UI-only */}) {
                                        Icon(
                                                Icons.Filled.Edit,
                                                contentDescription = "Editar",
                                                tint = terracota
                                        )
                                    }
                                    IconButton(onClick = { addresses.remove(addr) }) {
                                        Icon(
                                                Icons.Filled.Delete,
                                                contentDescription = "Eliminar",
                                                tint = Color.Gray
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Button(
                                        onClick = {
                                            addresses.replaceAll {
                                                if (it.id == addr.id) it.copy(isDefault = true)
                                                else it.copy(isDefault = false)
                                            }
                                            onSelect(addr)
                                        },
                                        colors =
                                                ButtonDefaults.buttonColors(
                                                        containerColor =
                                                                if (addr.isDefault) terracota
                                                                else Color(0xFFF4F4F4)
                                                ),
                                        shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text(
                                            if (addr.isDefault) "Seleccionada" else "Usar",
                                            color = if (addr.isDefault) Color.White else brownDark,
                                            fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Botón flotante para agregar nueva dirección
        ExtendedFloatingActionButton(
                onClick = { /* abrir formulario */},
                icon = { Icon(Icons.Filled.Add, contentDescription = "Agregar dirección") },
                text = { Text("Agregar dirección") },
                containerColor = terracota,
                modifier = Modifier.align(Alignment.BottomEnd).padding(20.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AddressManagementPreview() {
    PizzaHub_MobileTheme { AddressManagementScreen(onBack = {}) }
}
