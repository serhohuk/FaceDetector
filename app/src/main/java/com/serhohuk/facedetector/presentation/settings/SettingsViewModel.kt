package com.serhohuk.facedetector.presentation.settings

import androidx.lifecycle.ViewModel
import com.serhohuk.facedetector.system.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsFragmentUIState(preferencesManager.settings))
    val uiState: StateFlow<SettingsFragmentUIState>
        get() = _uiState

    fun setEyeDetection(value: Boolean) {
        preferencesManager.withEyeOpen = value
        updateState()
    }

    fun setWithSmileDetection(value: Boolean) {
        preferencesManager.withSmileDetection = value
        updateState()
    }

    fun setFrameThickness(value: Float) {
        preferencesManager.frameThickness = value
        updateState()
    }

    fun setTextSize(value: Float) {
        preferencesManager.textSize = value
        updateState()
    }

    fun setTextColor(value: ULong) {
        preferencesManager.textColor = value
        updateState()
    }

    fun setFrameColor(value: ULong) {
        preferencesManager.frameColor = value
        updateState()
    }


    private fun updateState() {
        _uiState.value = SettingsFragmentUIState(preferencesManager.settings)
    }

}