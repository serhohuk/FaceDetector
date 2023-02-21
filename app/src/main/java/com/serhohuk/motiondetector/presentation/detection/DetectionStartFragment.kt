package com.serhohuk.motiondetector.presentation.detection

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Build
import android.os.Build.VERSION_CODES.Q
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.serhohuk.motiondetector.AppRouter
import com.serhohuk.motiondetector.R
import com.serhohuk.motiondetector.ui.theme.FaceDetectionTheme
import com.serhohuk.motiondetector.ui.theme.appColors
import com.serhohuk.motiondetector.ui.theme.appShapes

class DetectionStartFragment : Fragment() {

    private val router by lazy {
        AppRouter()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = ComposeView(requireContext()).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        setContent {
            FaceDetectionTheme {
                DetectionStartScreen(
                    onCameraButtonClick = {
                        if (permissionGranted()) {
                            router.navigateToCameraRecognitionScreen(
                                requireActivity().supportFragmentManager
                            )
                        } else {
                            requestPermission()
                        }
                    },
                    onGalleryButtonClick = {
                        if (permissionGranted()) {
                            router.navigateToGalleryRecognitionScreen(
                                requireActivity().supportFragmentManager
                            )
                        } else {
                            requestPermission()
                        }
                    })
            }
        }
    }

    private fun requestPermission() {
        val permissions = if (Build.VERSION.SDK_INT >= Q) {
            arrayOf(Manifest.permission.CAMERA)
        } else {
            arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        ActivityCompat.requestPermissions(requireActivity(), permissions, 0)
    }

    private fun permissionGranted() = if (Build.VERSION.SDK_INT >= Q) {
        ContextCompat.checkSelfPermission(
            requireActivity(), Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    } else {
        ContextCompat.checkSelfPermission(
            requireActivity(), Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(
                    requireContext(),
                    "you can navigate to camera now",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(requireContext(), "camera permission denied", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetectionStartScreen(
    onCameraButtonClick: () -> Unit,
    onGalleryButtonClick: () -> Unit
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
            Row(
                Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.appColors.surface)
                    .height(52.dp)
                    .padding(start = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.app_name),
                    color = MaterialTheme.appColors.textPrimary,
                    style = MaterialTheme.typography.titleMedium
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(48.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(MaterialTheme.appShapes.bigButtonShape)
                        .clickable { onCameraButtonClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        modifier = Modifier.fillMaxSize(),
                        painter = painterResource(id = R.drawable.camera_bg),
                        contentDescription = null,
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        Modifier
                            .fillMaxSize()
                            .background(
                                MaterialTheme.appColors.colors.primary.copy(alpha = 0.8f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(id = R.string.camera),
                            color = Color.White,
                            style = MaterialTheme.typography.displayMedium
                        )
                    }
                }
                Spacer(Modifier.height(32.dp))
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(MaterialTheme.appShapes.bigButtonShape)
                        .clickable { onGalleryButtonClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        modifier = Modifier.fillMaxSize(),
                        painter = painterResource(id = R.drawable.gallery_bg),
                        contentDescription = null,
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        Modifier
                            .matchParentSize()
                            .background(
                                MaterialTheme.appColors.colors.primary.copy(alpha = 0.8f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(id = R.string.select_photo),
                            color = Color.White,
                            style = MaterialTheme.typography.displayMedium
                        )
                    }

                }
            }
        }
    }
}

@Preview(uiMode = UI_MODE_NIGHT_NO)
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun DetectionStartScreenPreview() {
    FaceDetectionTheme {
        DetectionStartScreen(
            onCameraButtonClick = {

            },
            onGalleryButtonClick = {

            }
        )
    }
}