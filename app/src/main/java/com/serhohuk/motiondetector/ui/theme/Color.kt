package com.serhohuk.motiondetector.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

data class AppColors(
    val colors: ColorScheme,
    val textPrimary: Color,
    val warningColor: Color
) {
    val background get() = colors.background
    val surface get() = colors.surface
}