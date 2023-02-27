package com.serhohuk.facedetector.system

import androidx.compose.ui.graphics.Color

data class AppSettings(
    val withEyeOpen: Boolean,
    val withSmileProbability: Boolean,
    val frameThickness: Float,
    val textSize: Float,
    val textColor: ULong,
    val frameColor: ULong
) {
    companion object {
        val default: AppSettings
            get() {
                return AppSettings(
                    withEyeOpen = true,
                    withSmileProbability = true,
                    frameThickness = 4f,
                    textSize = 14f,
                    textColor = Color.Yellow.value,
                    frameColor = Color.Red.value
                )
            }
    }
}