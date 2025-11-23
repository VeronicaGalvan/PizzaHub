package com.example.pizzahub_mobile.ui.screens

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.widget.FrameLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.pizzahub_mobile.R
import com.here.sdk.core.GeoCoordinates
import com.here.sdk.mapview.*
import kotlinx.coroutines.delay

@Composable
fun DeliveryTrackingMapScreen(
        orderId: String,
        onBack: () -> Unit,
        // Coordenadas de la pizzería: Blvd. Antonio Madrazo #6401-Local 3, Valle de Señora, 37205
        // León de los Aldama, Gto.
        originLat: Double = 21.15969,
        originLon: Double = -101.65070,
        destLat: Double? = null, // Coordenadas del usuario desde geocoding
        destLon: Double? = null,
        estado: String = "EN_CAMINO" // Estado del pedido
) {
    val cream = Color(0xFFFFF8EE)
    val brownDark = Color(0xFF4E342E)
    val terracota = Color(0xFFD35400)
    val softBeige = Color(0xFFFFEEDD)

    // Usar coordenadas del usuario o valores por defecto
    val finalDestLat = destLat ?: 21.12
    val finalDestLon = destLon ?: -101.68

    var pos by remember { mutableStateOf(0f) }

    // Evitar que el MapView se recree en cada recomposición
    val mapViewRef = remember { mutableStateOf<MapView?>(null) }

    Column(modifier = Modifier.fillMaxSize().background(cream)) {

        // Header
        Box(
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                contentAlignment = Alignment.Center
        ) {
            IconButton(onClick = onBack, modifier = Modifier.align(Alignment.CenterStart)) {
                Icon(Icons.Filled.ArrowBack, "Volver", tint = brownDark)
            }
            Text("Seguimiento", color = brownDark, fontSize = 18.sp)
        }

        // MAPA
        Box(
                modifier =
                        Modifier.padding(16.dp)
                                .fillMaxWidth()
                                .height(360.dp)
                                .clip(RoundedCornerShape(18.dp))
                                .background(softBeige)
        ) {
            val context = LocalContext.current

            AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { ctx ->
                        val frame = FrameLayout(ctx)
                        val mapView = MapView(ctx)
                        mapViewRef.value = mapView
                        frame.addView(mapView)

                        mapView.onCreate(null)

                        mapView.mapScene.loadScene(MapScheme.NORMAL_DAY) { error ->
                            if (error == null) {

                                mapView.camera.lookAt(
                                        GeoCoordinates(originLat, originLon),
                                        MapMeasure(MapMeasure.Kind.ZOOM_LEVEL, 14.0)
                                )

                                addCourierMarker(mapView, originLat, originLon)
                            }
                        }

                        frame
                    },
                    update = { /* Evitar actualizaciones innecesarias */}
            )
        }

        // INFO Pedido
        Card(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = softBeige)
        ) {
            Column(Modifier.padding(16.dp)) {
                Text("Pedido #$orderId", fontWeight = FontWeight.SemiBold, color = brownDark)
                Spacer(Modifier.height(6.dp))
                val estadoFormatted = estado.replace("_", " ").uppercase()
                Text("Estado: $estadoFormatted", color = terracota, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(6.dp))

                val eta = ((1 - pos) * 25).toInt().coerceAtLeast(1)
                Text("Tiempo estimado: ~${eta} min", color = brownDark.copy(alpha = 0.8f))
            }
        }

        // Simulación
        LaunchedEffect(orderId) {
            while (pos < 0.98f) {
                delay(1000)
                pos = (pos + 0.12f).coerceAtMost(0.98f)
            }
        }
    }

    // Manejo del ciclo de vida del MapView
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            mapViewRef.value?.let { mapView ->
                when (event) {
                    Lifecycle.Event.ON_RESUME -> mapView.onResume()
                    Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                    Lifecycle.Event.ON_DESTROY -> {
                        mapView.onDestroy()
                        mapViewRef.value = null
                    }
                    else -> {}
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            mapViewRef.value?.onDestroy()
            mapViewRef.value = null
        }
    }
}

// MARCADOR
fun addCourierMarker(mapView: MapView, lat: Double, lon: Double) {
    val coords = GeoCoordinates(lat, lon)

    // Convertir el vector drawable a bitmap para que funcione con HERE SDK
    val bitmap = getBitmapFromVectorDrawable(mapView.context, R.drawable.ic_courier_marker)

    if (bitmap != null) {
        val image = MapImageFactory.fromBitmap(bitmap)
        val marker = MapMarker(coords, image)
        mapView.mapScene.addMapMarker(marker)
    } else {
        // Fallback: usar imagen por defecto del SDK si falla la conversión
        android.util.Log.w("DeliveryTracking", "No se pudo cargar el marcador personalizado")
    }
}

// Helper para convertir VectorDrawable a Bitmap
private fun getBitmapFromVectorDrawable(context: Context, drawableId: Int): Bitmap? {
    return try {
        val drawable = ContextCompat.getDrawable(context, drawableId) ?: return null

        // Tamaño del marcador (ajustar según necesidad)
        val width = if (drawable.intrinsicWidth > 0) drawable.intrinsicWidth else 96
        val height = if (drawable.intrinsicHeight > 0) drawable.intrinsicHeight else 96

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        bitmap
    } catch (e: Exception) {
        android.util.Log.e("DeliveryTracking", "Error al convertir drawable a bitmap", e)
        null
    }
}
