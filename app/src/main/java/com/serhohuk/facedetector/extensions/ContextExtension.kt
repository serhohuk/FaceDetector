package com.serhohuk.facedetector

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

private suspend fun Context.getCameraProvider(): ProcessCameraProvider = suspendCoroutine { continuation ->
    ProcessCameraProvider.getInstance(this).also { cameraProvider ->
        cameraProvider.addListener({
            continuation.resume(cameraProvider.get())
        }, ContextCompat.getMainExecutor(this))
    }
}

fun AppCompatActivity.isFragmentExistsAndVisible(tag: String): Boolean {
    val fragment = this.supportFragmentManager.findFragmentByTag(tag)
    return fragment!=null && fragment.isVisible
}