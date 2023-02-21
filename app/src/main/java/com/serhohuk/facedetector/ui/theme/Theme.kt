package com.serhohuk.facedetector.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.ViewCompat

private val DarkColorScheme = AppColors(
    colors = darkColorScheme(
        primary = Color(0xFF005b9f),
        secondary = Color(0xFFc9bc1f),
        tertiary = Color(0xFF33A5FF),
        background = Color.Black,
        onBackground = Color.White,
        surface = Color(0xFF2B2A2A)
    ),
    textPrimary = Color.White,
    warningColor = Color.Red
)

private val LightColorScheme = AppColors(
    colors = lightColorScheme(
        primary = Color(0xFF0288d1),
        secondary = Color(0xFFffee58),
        tertiary = Color(0xFF5eb8ff),
        background = Color.White,
        onBackground = Color.Black,
        surface = Color(0xFFDBDBDB)
    ),
    textPrimary = Color.Black,
    warningColor = Color.Red
)

val MaterialTheme.appColors: AppColors
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) LightColorScheme else DarkColorScheme


@Composable
fun FaceDetectionTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme.colors
        else -> LightColorScheme.colors
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            (view.context as Activity).window.statusBarColor = colorScheme.primary.toArgb()
            ViewCompat.getWindowInsetsController(view)?.isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}