package com.serhohuk.facedetector.presentation.detection.gallery

sealed class GalleryDetectionUIState {
    object Start: GalleryDetectionUIState()
    object Loading: GalleryDetectionUIState()
    data class Success(val imagePath: String, val saved: Boolean) : GalleryDetectionUIState()
}