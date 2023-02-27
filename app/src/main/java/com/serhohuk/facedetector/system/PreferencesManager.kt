package com.serhohuk.facedetector.system

import android.content.Context
import com.serhohuk.facedetector.presentation.settings.SettingsFragmentUIState
import javax.inject.Inject

class PreferencesManager @Inject constructor(
    context: Context
) {
    private val sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)

    private val defaultSettings = AppSettings.default

    var withEyeOpen: Boolean
        get() {
            return sharedPreferences.getBoolean(WITH_OPEN_EYES, defaultSettings.withEyeOpen)
        }
        set(value) {
            sharedPreferences.edit().putBoolean(WITH_OPEN_EYES, value).apply()
        }

    var withSmileDetection: Boolean
        get() {
            return sharedPreferences.getBoolean(
                WITH_SMILE_DETECTION,
                defaultSettings.withSmileProbability
            )
        }
        set(value) {
            sharedPreferences.edit().putBoolean(WITH_SMILE_DETECTION, value).apply()
        }

    var frameThickness: Float
        get() {
            return sharedPreferences.getFloat(FRAME_THICK, defaultSettings.frameThickness)
        }
        set(value) {
            sharedPreferences.edit().putFloat(FRAME_THICK, value).apply()
        }

    var textSize: Float
        get() {
            return sharedPreferences.getFloat(TEXT_SIZE, defaultSettings.textSize)
        }
        set(value) {
            sharedPreferences.edit().putFloat(TEXT_SIZE, value).apply()
        }

    var frameColor: ULong
        get() {
            return sharedPreferences.getLong(FRAME_COLOR, defaultSettings.frameColor.toLong())
                .toULong()
        }
        set(value) {
            sharedPreferences.edit().putLong(FRAME_COLOR, value.toLong()).apply()
        }

    var textColor: ULong
        get() {
            return sharedPreferences.getLong(TEXT_COLOR, defaultSettings.textColor.toLong())
                .toULong()
        }
        set(value) {
            sharedPreferences.edit().putLong(TEXT_COLOR, value.toLong()).apply()
        }

    val settings: AppSettings
        get() {
            return AppSettings.default.copy(
                withEyeOpen = withEyeOpen,
                withSmileProbability = withSmileDetection,
                frameThickness = frameThickness,
                textSize = textSize,
                frameColor = frameColor,
                textColor = textColor
            )
        }

    companion object {
        private const val WITH_OPEN_EYES = "with_open_eyes"
        private const val WITH_SMILE_DETECTION = "with_smile_detection"
        private const val FRAME_THICK = "frame_thick"
        private const val TEXT_SIZE = "text_size"
        private const val TEXT_COLOR = "text_color"
        private const val FRAME_COLOR = "frame_color"
    }
}