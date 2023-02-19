package com.serhohuk.motiondetector.presentation.detection.gallery

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import coil.compose.rememberAsyncImagePainter
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.serhohuk.motiondetector.detection.FaceRect
import com.serhohuk.motiondetector.R
import com.serhohuk.motiondetector.extensions.drawDetectionResult
import com.serhohuk.motiondetector.ui.theme.FaceDetectionTheme
import com.serhohuk.motiondetector.ui.theme.appColors
import com.serhohuk.motiondetector.ui.theme.appShapes
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.internal.closeQuietly
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream


@AndroidEntryPoint
class GalleryDetectionFragment : Fragment() {

    private val viewModel: GalleryDetectionViewModel by viewModels()

    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                viewModel.setIsLoading()
                val highAccuracyOpts = FaceDetectorOptions.Builder()
                    .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                    .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                    .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                    .setContourMode(FaceDetectorOptions.CONTOUR_MODE_NONE)
                    .build()

                val faceDetector = FaceDetection.getClient(highAccuracyOpts);
                val bitmap = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                    MediaStore.Images.Media.getBitmap(
                        requireContext().contentResolver,
                        uri
                    )
                } else {
                    val source = ImageDecoder.createSource(requireContext().contentResolver, uri)
                    ImageDecoder.decodeBitmap(source)
                }

                faceDetector.process(bitmap, 0)
                    .addOnSuccessListener {
                        val boxes = mutableListOf<FaceRect>()
                        for (face in it) {
                            boxes.add(
                                FaceRect(
                                    face.smilingProbability.toString(),
                                    face.boundingBox
                                )
                            )
                        }
                        lifecycleScope.launch(Dispatchers.IO) {
                            val resultBitmap = requireActivity().drawDetectionResult(bitmap, boxes)
                            val file =
                                File(requireContext().externalCacheDir.toString() + File.separator + "IMG_${System.currentTimeMillis()}.jpg")
                            val os: OutputStream = BufferedOutputStream(FileOutputStream(file))
                            resultBitmap.compress(Bitmap.CompressFormat.JPEG, 100, os)
                            os.close()
                            withContext(Dispatchers.Main) {
                                viewModel.setPhotoSelected(file.path)
                                viewModel.imagePath = file.path

                            }
                        }

                    }
                    .addOnCompleteListener{
                        faceDetector.close()
                        faceDetector.closeQuietly()
                    }
            } else {

            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = ComposeView(requireContext()).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        setContent {
            val state by viewModel.uiState.observeAsState(GalleryDetectionUIState.Start)

            GalleryDetectionScreen(
                uiState = state,
                onSelectImageClick = {
                    pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                },
                onBackClick = {
                    requireActivity().supportFragmentManager.popBackStack()
                }
            )
        }
    }


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GalleryDetectionScreen(
    uiState: GalleryDetectionUIState,
    onSelectImageClick: () -> Unit,
    onBackClick: () -> Unit
) {
    Scaffold(
        contentColor = MaterialTheme.appColors.background
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(it),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.appColors.surface)
                    .height(52.dp)
                    .padding(start = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                IconButton(modifier = Modifier.size(24.dp), onClick = onBackClick) {
                    Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = null)
                }
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 48.dp),
                    text = stringResource(id = R.string.gallery_detection),
                    color = MaterialTheme.appColors.textPrimary,
                    style = MaterialTheme.typography.titleMedium
                )
            }
            Spacer(Modifier.height(160.dp))
            when (uiState) {
                is GalleryDetectionUIState.Start -> {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .padding(horizontal = 16.dp)
                            .background(MaterialTheme.appColors.colors.primary, MaterialTheme.appShapes.imageShape)
                            .clip(MaterialTheme.appShapes.imageShape)
                            .clickable { onSelectImageClick() },
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            modifier = Modifier.size(52.dp),
                            imageVector = Icons.Filled.Upload,
                            contentDescription = null,
                            tint = Color.White
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = "Select Image",
                            color = Color.White,
                            style = MaterialTheme.typography.displaySmall
                        )
                    }
                }
                is GalleryDetectionUIState.Loading -> {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .padding(horizontal = 16.dp)
                            .background(MaterialTheme.appColors.colors.primary, MaterialTheme.appShapes.imageShape)
                            .clip(MaterialTheme.appShapes.imageShape)
                            .clickable { onSelectImageClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is GalleryDetectionUIState.Success -> {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .padding(horizontal = 16.dp)
                            .background(MaterialTheme.appColors.colors.primary, MaterialTheme.appShapes.imageShape)
                            .clip(MaterialTheme.appShapes.imageShape),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(uiState.imagePath),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun GalleryDetectionScreenPreview() {
    FaceDetectionTheme {
        GalleryDetectionScreen(
            uiState = GalleryDetectionUIState.Success(""),
            onBackClick = {},
            onSelectImageClick = {}
        )
    }
}