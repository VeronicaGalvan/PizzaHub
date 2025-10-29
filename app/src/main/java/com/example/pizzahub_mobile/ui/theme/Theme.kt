package com.example.pizzahub_mobile.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Palette de colores tipo "pizza"
val PizzaCream = Color(0xFFFDF7F0)
val PizzaBrown = Color(0xFF5A2D0C)
val PizzaLightBrown = Color(0xFFDFAE84)
val PizzaRed = Color(0xFFB94E3A)
val PizzaGray = Color(0xFFBDB5AA)

// Esquemas de color claros/oscuro usando la paleta
private val DarkColorScheme = darkColorScheme(
    primary = PizzaRed,
    secondary = PizzaLightBrown,
    background = PizzaBrown,
    surface = PizzaBrown,
    onPrimary = Color.White,
    onBackground = PizzaCream,
    outline = PizzaGray
)

private val LightColorScheme = lightColorScheme(
    primary = PizzaRed,
    secondary = PizzaBrown,
    background = PizzaCream,
    surface = Color.White,
    onPrimary = Color.White,
    onBackground = PizzaBrown,
    outline = PizzaGray
)

// Tipografía personalizada para la app (ajustada al diseño: títulos grandes, cuerpo legible)
private val PizzaTypography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        color = PizzaBrown
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 30.sp,
        color = PizzaBrown
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        color = PizzaBrown
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        color = PizzaBrown
    )
)

@Composable
fun PizzaHubTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = PizzaTypography,
        content = content
    )
}

// Alias para compatibilidad con el nombre anterior del tema
@Composable
fun PizzaHub_MobileTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) = PizzaHubTheme(darkTheme = darkTheme, dynamicColor = dynamicColor, content = content)
