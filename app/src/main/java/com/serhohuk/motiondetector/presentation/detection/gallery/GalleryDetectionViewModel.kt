package com.serhohuk.motiondetector.presentation.detection.gallery

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.mlkit.vision.face.FaceDetector
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GalleryDetectionViewModel @Inject constructor(
    private val faceDetector: FaceDetector
) : ViewModel() {

    private val _uiState = MutableLiveData<GalleryDetectionUIState>()
    val uiState: LiveData<GalleryDetectionUIState>
        get() = _uiState

    var imagePath = ""



    fun setPhotoSelected(uri: String) {
        _uiState.value = GalleryDetectionUIState.Success(uri)
    }

    fun setIsLoading() {
        _uiState.value = GalleryDetectionUIState.Loading
    }

}