package com.serhohuk.facedetector.presentation.detection.gallery

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.mlkit.vision.face.FaceDetector
import com.serhohuk.facedetector.system.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GalleryDetectionViewModel @Inject constructor(
    private val faceDetector: FaceDetector,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _uiState = MutableLiveData<GalleryDetectionUIState>()
    val uiState: LiveData<GalleryDetectionUIState>
        get() = _uiState

    var imagePath = ""

    fun setPhotoSelected(uri: String, state: SavingState) {
        _uiState.postValue(GalleryDetectionUIState.Success(uri, state))
    }

    fun setIsLoading() {
        _uiState.postValue(GalleryDetectionUIState.Loading)
    }

    fun getAppSettings() = preferencesManager.settings

}