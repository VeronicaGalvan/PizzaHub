package com.example.pizzahub_mobile.ui.screens

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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.pizzahub_mobile.data.network.HereRepository
import com.example.pizzahub_mobile.ui.theme.PizzaHub_MobileTheme
import com.here.sdk.core.GeoCoordinates
import com.here.sdk.mapview.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapPreviewScreen(
        destLat: Double,
        destLon: Double,
        onBack: () -> Unit,
        onConfirm: () -> Unit = {}
) {
    val cream = Color(0xFFFFF8EE)
    val brownDark = Color(0xFF4E342E)
    val terracota = Color(0xFFD35400)
    val softBeige = Color(0xFFFFF2D5)

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // Coordenadas de la pizzería
    // Coordenadas de la pizzería: Blvd. Antonio Madrazo #6401-Local 3, Valle de Señora, 37205 León
    // de los Aldama, Gto.
    val originLat = 21.15969
    val originLon = -101.65070

    var estimatedTime by remember { mutableStateOf<String?>(null) }
    var isLoadingRoute by remember { mutableStateOf(false) }

    Column(
            modifier =
                    Modifier.fillMaxSize()
                            .background(Brush.verticalGradient(listOf(cream, Color.White)))
                            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // Header with centered title
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            IconButton(onClick = onBack, modifier = Modifier.align(Alignment.CenterStart)) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Volver", tint = brownDark)
            }

            Text(
                    text = "Ruta estimada",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = brownDark
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Mapa con HERE SDK
        Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = softBeige),
                modifier =
                        Modifier.fillMaxWidth().weight(1f).shadow(4.dp, RoundedCornerShape(20.dp))
        ) {
            AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { ctx ->
                        val frame = FrameLayout(ctx)
                        val mapView = MapView(ctx)
                        frame.addView(mapView)

                        mapView.onCreate(null)

                        mapView.mapScene.loadScene(MapScheme.NORMAL_DAY) { error ->
                            if (error == null) {
                                // Centrar el mapa entre la pizzería y el destino
                                val centerLat = (originLat + destLat) / 2.0
                                val centerLon = (originLon + destLon) / 2.0

                                mapView.camera.lookAt(
                                        GeoCoordinates(centerLat, centerLon),
                                        MapMeasure(MapMeasure.Kind.ZOOM_LEVEL, 13.0)
                                )

                                // Agregar marcadores
                                addMarker(mapView, originLat, originLon, "🍕", ctx)
                                addMarker(mapView, destLat, destLon, "🏠", ctx)

                                // Cargar ruta
                                isLoadingRoute = true
                                scope.launch {
                                    try {
                                        val hereRepo = HereRepository(ctx)
                                        val result =
                                                hereRepo.getRoute(
                                                        originLat,
                                                        originLon,
                                                        destLat,
                                                        destLon
                                                )

                                        result.fold(
                                                onSuccess = { (route, summary) ->
                                                    // Mostrar tiempo estimado
                                                    val minutes =
                                                            (summary.durationSeconds / 60).toInt()
                                                    estimatedTime = "$minutes min"

                                                    // Dibujar ruta en el mapa usando HERE SDK
                                                    val routeGeoPolyline = route.geometry

                                                    // Crear representación visual de la ruta
                                                    val lineWidth =
                                                            com.here.sdk.mapview
                                                                    .MapMeasureDependentRenderSize(
                                                                            com.here.sdk.mapview
                                                                                    .RenderSize.Unit
                                                                                    .PIXELS,
                                                                            20.0
                                                                    )
                                                    val lineColor =
                                                            com.here.sdk.core.Color.valueOf(
                                                                    0xFF.toFloat(),
                                                                    0x00.toFloat(),
                                                                    0x00.toFloat(),
                                                                    0xFF.toFloat()
                                                            ) // Azul
                                                    val representation =
                                                            com.here.sdk.mapview.MapPolyline
                                                                    .SolidRepresentation(
                                                                            lineWidth,
                                                                            lineColor,
                                                                            com.here.sdk.mapview
                                                                                    .LineCap.ROUND
                                                                    )

                                                    val routeMapPolyline =
                                                            com.here.sdk.mapview.MapPolyline(
                                                                    routeGeoPolyline,
                                                                    representation
                                                            )
                                                    mapView.mapScene.addMapPolyline(
                                                            routeMapPolyline
                                                    )
                                                },
                                                onFailure = { e ->
                                                    android.util.Log.e(
                                                            "MapPreview",
                                                            "Error loading route",
                                                            e
                                                    )
                                                }
                                        )
                                    } finally {
                                        isLoadingRoute = false
                                    }
                                }
                            }
                        }

                        frame
                    }
            )

            // Overlay con tiempo estimado
            if (isLoadingRoute) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = terracota)
                }
            } else {
                estimatedTime?.let { time ->
                    Box(
                            modifier = Modifier.fillMaxSize().padding(16.dp),
                            contentAlignment = Alignment.TopEnd
                    ) {
                        Card(
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                    text = "⏱️ $time",
                                    modifier =
                                            Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    fontWeight = FontWeight.Bold,
                                    color = terracota
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
                onClick = onConfirm,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = terracota)
        ) { Text("Volver al checkout", color = Color.White, fontWeight = FontWeight.Bold) }
    }
}

// Helper para agregar marcadores de texto
private fun addMarker(
        mapView: MapView,
        lat: Double,
        lon: Double,
        emoji: String,
        context: android.content.Context
) {
    try {
        val coords = GeoCoordinates(lat, lon)
        val drawable = android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT)

        // Crear un bitmap con el emoji
        val textPaint =
                android.graphics.Paint().apply {
                    textSize = 60f
                    isAntiAlias = true
                }
        val bounds = android.graphics.Rect()
        textPaint.getTextBounds(emoji, 0, emoji.length, bounds)

        val bitmap =
                android.graphics.Bitmap.createBitmap(
                        bounds.width() + 20,
                        bounds.height() + 20,
                        android.graphics.Bitmap.Config.ARGB_8888
                )
        val canvas = android.graphics.Canvas(bitmap)
        canvas.drawText(emoji, 10f, bounds.height() + 10f, textPaint)

        val image = MapImageFactory.fromBitmap(bitmap)
        val marker = MapMarker(coords, image)
        mapView.mapScene.addMapMarker(marker)
    } catch (e: Exception) {
        android.util.Log.e("MapPreview", "Error adding marker", e)
    }
}

@Preview(showBackground = true)
@Composable
fun MapPreviewScreenPreview() {
    PizzaHub_MobileTheme {
        MapPreviewScreen(destLat = 21.12, destLon = -101.68, onBack = {}, onConfirm = {})
    }
}
