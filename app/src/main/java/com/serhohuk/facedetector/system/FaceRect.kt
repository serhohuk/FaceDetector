package com.serhohuk.facedetector.system

import android.graphics.Rect

data class FaceRect(
    val text: String,
    val smileProbability: String,
    val leftEyeOpenProbability: String,
    val rightEyeOpenProbability: String,
    val rect: Rect
)