package com.serhohuk.facedetector.presentation.detection.gallery

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import androidx.compose.material.icons.filled.Done
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
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import coil.compose.rememberAsyncImagePainter
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.serhohuk.facedetector.BuildConfig
import com.serhohuk.facedetector.R
import com.serhohuk.facedetector.system.FaceRect
import com.serhohuk.facedetector.extensions.drawDetectionResult
import com.serhohuk.facedetector.extensions.round
import com.serhohuk.facedetector.extensions.saveImage
import com.serhohuk.facedetector.extensions.scaleBitmap
import com.serhohuk.facedetector.ui.theme.FaceDetectionTheme
import com.serhohuk.facedetector.ui.theme.appColors
import com.serhohuk.facedetector.ui.theme.appShapes
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.internal.closeQuietly
import java.io.*


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
                    val source =
                        ImageDecoder.createSource(requireContext().contentResolver, uri)
                    ImageDecoder.decodeBitmap(source)
                }
                val scaledBitmap = requireActivity().scaleBitmap(bitmap)

                faceDetector.process(scaledBitmap, 0)
                    .addOnSuccessListener {
                        lifecycleScope.launch(Dispatchers.IO) {
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
                            val resultBitmap =
                                requireActivity().drawDetectionResult(
                                    scaledBitmap,
                                    boxes,
                                    viewModel.getAppSettings()
                                )
                            val file =
                                File(requireContext().externalCacheDir.toString() + File.separator + "IMG_${System.currentTimeMillis()}.png")
                            val os: OutputStream = BufferedOutputStream(FileOutputStream(file))
                            resultBitmap.compress(Bitmap.CompressFormat.PNG, 70, os)
                            os.close()
                            viewModel.setPhotoSelected(file.path, SavingState.NOT_SAVED)
                            viewModel.imagePath = file.path
                        }
                    }.addOnCompleteListener {
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
                },
                onSaveClick = {
                    lifecycleScope.launch(Dispatchers.IO) {
                        viewModel.setPhotoSelected(viewModel.imagePath, SavingState.SAVING)
                        val bitmap = BitmapFactory.decodeFile(viewModel.imagePath)
                        requireActivity().saveImage(bitmap)
                        viewModel.setPhotoSelected(viewModel.imagePath, SavingState.SAVED)
                    }
                },
                onShareClick = {
                    startFileShareIntent(viewModel.imagePath)
                }
            )
        }
    }


    private fun startFileShareIntent(filePath: String) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "image/*"
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            putExtra(
                Intent.EXTRA_SUBJECT,
                getString(R.string.sharing_file, getString(R.string.app_name))
            )
            val fileURI = FileProvider.getUriForFile(
                requireContext(), BuildConfig.APPLICATION_ID + ".provider",
                File(filePath)
            )
            putExtra(Intent.EXTRA_STREAM, fileURI)
        }
        startActivity(shareIntent)
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GalleryDetectionScreen(
    uiState: GalleryDetectionUIState,
    onSelectImageClick: () -> Unit,
    onSaveClick: () -> Unit,
    onShareClick: () -> Unit,
    onBackClick: () -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.appColors.background
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
                    .background(MaterialTheme.appColors.colors.onBackground)
                    .height(52.dp)
                    .padding(start = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                IconButton(modifier = Modifier.size(24.dp), onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 48.dp),
                    text = stringResource(id = R.string.gallery_detection),
                    color = Color.White,
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
                            .background(
                                MaterialTheme.appColors.colors.primary,
                                MaterialTheme.appShapes.imageShape
                            )
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
                            text = stringResource(id = R.string.select_image),
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
                            .background(
                                MaterialTheme.appColors.colors.primary,
                                MaterialTheme.appShapes.imageShape
                            )
                            .clip(MaterialTheme.appShapes.imageShape),
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
                            .background(
                                MaterialTheme.appColors.colors.primary,
                                MaterialTheme.appShapes.imageShape
                            )
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
                    Spacer(Modifier.height(32.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Button(
                            modifier = Modifier.weight(1f),
                            onClick = { onSaveClick() },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.appColors.colors.primary),
                            shape = MaterialTheme.appShapes.buttonShape,
                            enabled = uiState.savingState == SavingState.NOT_SAVED
                        ) {
                            when (uiState.savingState) {
                                SavingState.SAVED -> {
                                    Icon(
                                        imageVector = Icons.Filled.Done,
                                        contentDescription = null,
                                        tint = Color.White
                                    )
                                }
                                SavingState.SAVING -> {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp),
                                        color = MaterialTheme.appColors.colors.primary
                                    )
                                }
                                else -> {
                                    Text(
                                        text = stringResource(id = R.string.save_image),
                                        color = Color.White,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                        Spacer(Modifier.width(16.dp))
                        Button(
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.appColors.colors.primary),
                            shape = MaterialTheme.appShapes.buttonShape,
                            onClick = { onShareClick() }
                        ) {
                            Text(
                                text = stringResource(id = R.string.share_image),
                                color = Color.White,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
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
            uiState = GalleryDetectionUIState.Success("", SavingState.NOT_SAVED),
            onBackClick = {},
            onSelectImageClick = {},
            onSaveClick = {},
            onShareClick = {}
        )
    }
}