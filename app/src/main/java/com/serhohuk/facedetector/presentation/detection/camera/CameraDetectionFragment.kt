package com.serhohuk.facedetector.presentation.detection.camera

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.sharp.Lens
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.tasks.TaskExecutors
import com.google.common.util.concurrent.ListenableFuture
import com.google.mlkit.common.MlKitException
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.serhohuk.facedetector.detection.FaceDetectorProcessor
import com.serhohuk.facedetector.detection.FaceRect
import com.serhohuk.facedetector.extensions.drawDetectionResult
import com.serhohuk.facedetector.extensions.round
import com.serhohuk.facedetector.extensions.saveImage
import com.serhohuk.facedetector.system.AppSettings
import com.serhohuk.facedetector.ui.theme.FaceDetectionTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.nio.ByteBuffer
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class CameraDetectionFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = ComposeView(requireContext()).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        setContent {
            FaceDetectionTheme {
                val imageCapture: ImageCapture = remember { ImageCapture.Builder().build() }
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        var lens = remember {
                            mutableStateOf(CameraSelector.LENS_FACING_FRONT)
                        }
                        CameraPreview(
                            cameraLens = lens.value,
                            imageCapture = imageCapture
                        )
                        Controls(
                            onLensChange = { lens.value = switchLens(lens.value) },
                            onTakePictureClick = {
                                takePhoto(
                                    filenameFormat = "yyyy-MM-dd-HH-mm-ss-SSS",
                                    imageCapture = imageCapture,
                                    outputDirectory = File(""),
                                    executor = Executors.newSingleThreadExecutor(),
                                    onImageCaptured = {},
                                    onError = {}
                                )
                            }
                        )
                    }
                }
            }
        }
    }


    private fun switchLens(lens: Int) = if (CameraSelector.LENS_FACING_FRONT == lens) {
        CameraSelector.LENS_FACING_BACK
    } else {
        CameraSelector.LENS_FACING_FRONT
    }

    @Composable
    private fun CameraPreview(previewView: PreviewView) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = {
                previewView.apply {
                    this.scaleType = PreviewView.ScaleType.FILL_CENTER
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                }

                previewView
            })
    }

    @Composable
    fun DetectedFaces(
        faces: List<Face>,
        sourceInfo: SourceInfo
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val needToMirror = sourceInfo.isImageFlipped
            for (face in faces) {
                val left =
                    if (needToMirror) size.width - face.boundingBox.right.toFloat() else face.boundingBox.left.toFloat()
                drawRect(
                    color = Color.Red, style = Stroke(2.dp.toPx()),
                    topLeft = Offset(left, face.boundingBox.top.toFloat()),
                    size = Size(
                        face.boundingBox.width().toFloat(),
                        face.boundingBox.height().toFloat()
                    )
                )
            }
        }
    }

    @Composable
    fun CameraPreview(
        modifier: Modifier = Modifier,
        imageCapture: ImageCapture,
        cameraLens: Int
    ) {
        val lifecycleOwner = LocalLifecycleOwner.current
        val context = LocalContext.current
        val previewView = remember { PreviewView(context) }
        var sourceInfo by remember { mutableStateOf(SourceInfo(10, 10, false)) }
        var detectedFaces by remember { mutableStateOf<List<Face>>(emptyList()) }
        val cameraProviderFuture = remember(sourceInfo, cameraLens) {
            ProcessCameraProvider.getInstance(context)
                .configureCamera(
                    previewView, lifecycleOwner, imageCapture, cameraLens, context,
                    setSourceInfo = { sourceInfo = it },
                    onFacesDetected = { detectedFaces = it },
                )
        }
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            with(LocalDensity.current) {
                Box(
                    modifier = Modifier
                        .size(
                            height = sourceInfo.height.toDp(),
                            width = sourceInfo.width.toDp()
                        )
                        .scale(
                            calculateScale(
                                constraints,
                                sourceInfo,
                                PreviewScaleType.CENTER_CROP
                            )
                        )
                )
                {
                    CameraPreview(previewView)
                    DetectedFaces(faces = detectedFaces, sourceInfo = sourceInfo)
                }
            }
        }
    }

    @Composable
    fun Controls(
        onLensChange: () -> Unit,
        onTakePictureClick: () -> Unit
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.BottomCenter,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .padding(vertical = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 36.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    IconButton(
                        onClick = { onLensChange() },
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Color.Black),
                        content = {
                            Icon(
                                imageVector = Icons.Filled.Cameraswitch,
                                contentDescription = "Switch camera",
                                modifier = Modifier.size(20.dp),
                                tint = Color.White
                            )
                        })
                }

                IconButton(
                    modifier = Modifier.size(60.dp),
                    onClick = {
                        onTakePictureClick()
                    },
                    content = {
                        Icon(
                            imageVector = Icons.Sharp.Lens,
                            contentDescription = "Take picture",
                            tint = Color.White,
                            modifier = Modifier
                                .size(60.dp)
                                .padding(1.dp)
                                .border(1.dp, Color.White, CircleShape)
                        )
                    }
                )
            }
        }
    }

    private fun bindAnalysisUseCase(
        lens: Int,
        setSourceInfo: (SourceInfo) -> Unit,
        onFacesDetected: (List<Face>) -> Unit
    ): ImageAnalysis? {

        val imageProcessor = try {
            FaceDetectorProcessor()
        } catch (e: Exception) {
            Log.e("CAMERA", "Can not create image processor", e)
            return null
        }
        val builder = ImageAnalysis.Builder()
        val analysisUseCase = builder.build()

        var sourceInfoUpdated = false

        analysisUseCase.setAnalyzer(
            TaskExecutors.MAIN_THREAD
        ) { imageProxy: ImageProxy ->
            if (!sourceInfoUpdated) {
                setSourceInfo(obtainSourceInfo(lens, imageProxy))
                sourceInfoUpdated = true
            }
            try {
                imageProcessor.processImageProxy(imageProxy, onFacesDetected)
            } catch (e: MlKitException) {
                Log.e(
                    "CAMERA", "Failed to process image. Error: " + e.localizedMessage
                )
            }
        }
        return analysisUseCase
    }


    private fun obtainSourceInfo(lens: Int, imageProxy: ImageProxy): SourceInfo {
        val isImageFlipped = lens == CameraSelector.LENS_FACING_FRONT
        val rotationDegrees = imageProxy.imageInfo.rotationDegrees
        return if (rotationDegrees == 0 || rotationDegrees == 180) {
            SourceInfo(
                height = imageProxy.height,
                width = imageProxy.width,
                isImageFlipped = isImageFlipped
            )
        } else {
            SourceInfo(
                height = imageProxy.width,
                width = imageProxy.height,
                isImageFlipped = isImageFlipped
            )
        }
    }

    private fun takePhoto(
        filenameFormat: String,
        imageCapture: ImageCapture,
        outputDirectory: File,
        executor: Executor,
        onImageCaptured: (Uri) -> Unit,
        onError: (ImageCaptureException) -> Unit
    ) {
        val highAccuracyOpts = FaceDetectorOptions.Builder()
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .setContourMode(FaceDetectorOptions.CONTOUR_MODE_NONE)
            .build()

        val faceDetector = FaceDetection.getClient(highAccuracyOpts)

        imageCapture.takePicture(executor,  object : ImageCapture.OnImageCapturedCallback() {
            override fun onError(exception: ImageCaptureException) {
                Log.e("kilo", "Take photo error:", exception)
                onError(exception)
            }

            override fun onCaptureSuccess(image: ImageProxy) {
                val bitmap = imageProxyToBitmap(image)
                val inputImage =
                    InputImage.fromMediaImage(image.image!!, image.imageInfo.rotationDegrees)

                faceDetector.process(inputImage)
                    .addOnSuccessListener {
                        val boxes = mutableListOf<FaceRect>()
                        for (face in it) {
                            boxes.add(
                                FaceRect(
                                    face.trackingId.toString(),
                                    face.smilingProbability?.round(2).toString(),
                                    face.leftEyeOpenProbability?.round(2).toString(),
                                    face.leftEyeOpenProbability?.round(2).toString(),
                                    face.boundingBox
                                )
                            )
                        }
                        lifecycleScope.launch(Dispatchers.IO) {
                            val resultBitmap = requireActivity().drawDetectionResult(bitmap, boxes, AppSettings.default)
                            requireActivity().saveImage(resultBitmap)
                        }

                    }
                    .addOnCompleteListener {
                        image.close()
                    }
                super.onCaptureSuccess(image)
            }
        })
    }

    private fun imageProxyToBitmap(image: ImageProxy): Bitmap {
        val planeProxy = image.planes[0]
        val buffer: ByteBuffer = planeProxy.buffer
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    fun ListenableFuture<ProcessCameraProvider>.configureCamera(
        previewView: PreviewView,
        lifecycleOwner: LifecycleOwner,
        imageCapture: ImageCapture,
        cameraLens: Int,
        context: Context,
        setSourceInfo: (SourceInfo) -> Unit,
        onFacesDetected: (List<Face>) -> Unit
    ): ListenableFuture<ProcessCameraProvider> {
        addListener({
            val cameraSelector = CameraSelector.Builder().requireLensFacing(cameraLens).build()

            val preview = androidx.camera.core.Preview.Builder()
                .build()
                .apply {
                    setSurfaceProvider(previewView.surfaceProvider)
                }
            val analysis = bindAnalysisUseCase(cameraLens, setSourceInfo, onFacesDetected)


            try {
                get().apply {
                    unbindAll()
                    bindToLifecycle(lifecycleOwner, cameraSelector, preview)
                    bindToLifecycle(lifecycleOwner, cameraSelector, imageCapture)
                    bindToLifecycle(lifecycleOwner, cameraSelector, analysis)
                }
            } catch (exc: Exception) {
                TODO("process errors")
            }
        }, ContextCompat.getMainExecutor(context))
        return this
    }

    private fun calculateScale(
        constraints: Constraints,
        sourceInfo: SourceInfo,
        scaleType: PreviewScaleType
    ): Float {
        val heightRatio = constraints.maxHeight.toFloat() / sourceInfo.height
        val widthRatio = constraints.maxWidth.toFloat() / sourceInfo.width
        return when (scaleType) {
            PreviewScaleType.FIT_CENTER -> kotlin.math.min(heightRatio, widthRatio)
            PreviewScaleType.CENTER_CROP -> kotlin.math.max(heightRatio, widthRatio)
        }
    }

    data class SourceInfo(
        val width: Int,
        val height: Int,
        val isImageFlipped: Boolean,
    )

    private enum class PreviewScaleType {
        FIT_CENTER,
        CENTER_CROP
    }

    @Preview
    @Composable
    fun ControlsPreview() {
        FaceDetectionTheme() {
            Surface(color = Color.Gray) {
                Controls(
                    onLensChange = { },
                    onTakePictureClick = {})
            }
        }
    }
}