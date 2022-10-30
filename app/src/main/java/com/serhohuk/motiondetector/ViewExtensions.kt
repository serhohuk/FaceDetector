package com.serhohuk.motiondetector

import android.content.Context
import androidx.camera.core.CameraSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.common.util.concurrent.ListenableFuture

fun ListenableFuture<ProcessCameraProvider>.configureCamera(
    previewView: PreviewView,
    lifecycleOwner: LifecycleOwner,
    cameraLens: Int,
    context: Context
): ListenableFuture<ProcessCameraProvider> {
    addListener({
        val preview = androidx.camera.core.Preview.Builder()
            .build()
            .apply {
                setSurfaceProvider(previewView.surfaceProvider)
            }

        try {
            get().apply {
                unbindAll()
                bindToLifecycle(
                    lifecycleOwner,
                    CameraSelector.Builder().requireLensFacing(cameraLens).build(),
                    preview
                )
            }
        } catch (exc: Exception) {
            TODO("process errors")
        }
    }, ContextCompat.getMainExecutor(context))
    return this
}