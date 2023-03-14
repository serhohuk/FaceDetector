package com.serhohuk.facedetector.ui.theme

import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.dp

class AppShapes(
    val imageShape: CornerBasedShape,
    val bigButtonShape: CornerBasedShape,
    val buttonShape: CornerBasedShape,
    val dialogShape: CornerBasedShape
)

internal val LocalShapes = staticCompositionLocalOf {
    AppShapes(
        imageShape = RoundedCornerShape(8.dp),
        bigButtonShape = RoundedCornerShape(16.dp),
        dialogShape = RoundedCornerShape(24.dp),
        buttonShape = RoundedCornerShape(8.dp)
    )
}

val MaterialTheme.appShapes: AppShapes
    @Composable
    @ReadOnlyComposable
    get() = LocalShapes.current