package com.example.epilepsytestapp.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.epilepsytestapp.R

// Définition des polices
val BRLNSDB = FontFamily(
    Font(R.font.berlin, FontWeight.Bold) // Police pour le titre principal
)

val CandaraBold = FontFamily(
    Font(R.font.candarabold, FontWeight.Bold) // Police pour le texte général
)

// Couleurs principales
val PrimaryColor = Color(0xFF2B4765)
val BackgroundColor = Color(0xFFF8F8F8)
val TextColor = Color(0xFF2B4765)
val ButtonTextColor = Color(0xFFFFFFFF)

// Palette de couleurs claires
val LightColorScheme = lightColorScheme(
    primary = PrimaryColor,
    onPrimary = ButtonTextColor,
    background = BackgroundColor,
    onBackground = TextColor,
    surface = BackgroundColor,
    onSurface = TextColor
)

// Typographie personnalisée
val AppTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = BRLNSDB,
        fontWeight = FontWeight.Bold,
        fontSize = 36.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = CandaraBold,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = CandaraBold,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    labelLarge = TextStyle(
        fontFamily = CandaraBold,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        color = ButtonTextColor
    )
)

// Thème principal
@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = AppTypography,
        content = content
    )
}
