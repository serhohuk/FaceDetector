package com.serhohuk.facedetector.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = AppColors(
    colors = darkColorScheme(
        primary = Color(0xFF005b9f),
        secondary = Color(0xFFc9bc1f),
        tertiary = Color(0xFF33A5FF),
        background = Color.Black,
        onBackground = Color.White,
        surface = Color.Black,
        onSurface = Color.White
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
        surface = Color.White,
        onSurface = Color.Black,
        inverseSurface = Color(0xFFDBDBDB),
        surfaceVariant = Color(0xFFFFF9C7),
    ),
    textPrimary = Color.Black,
    warningColor = Color.Red
)

val MaterialTheme.appColors: AppColors
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) DarkColorScheme else LightColorScheme


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
//    val view = LocalView.current
//    if (!view.isInEditMode) {
//        SideEffect {
//            (view.context as Activity).window.statusBarColor = colorScheme.primary.toArgb()
//            ViewCompat.getWindowInsetsController(view)?.isAppearanceLightStatusBars = darkTheme
//        }
//    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}