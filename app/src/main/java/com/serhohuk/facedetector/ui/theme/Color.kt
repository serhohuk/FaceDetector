package com.serhohuk.facedetector.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color

data class AppColors(
    val colors: ColorScheme,
    val textPrimary: Color,
    val warningColor: Color
) {
    val background get() = colors.background
    val surface get() = colors.surface
}