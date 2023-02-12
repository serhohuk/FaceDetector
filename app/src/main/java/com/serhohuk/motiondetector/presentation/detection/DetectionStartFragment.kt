package com.serhohuk.motiondetector.presentation.detection

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.serhohuk.motiondetector.AppRouter
import com.serhohuk.motiondetector.ui.theme.MotionDetectorTheme

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
            MotionDetectorTheme {
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

                })
            }
        }
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CAMERA), 0)
    }

    private fun permissionGranted() = ContextCompat.checkSelfPermission(
        requireActivity(), Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED

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
    Scaffold() {
        Column(
            Modifier
                .fillMaxSize()
                .padding(it),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = onCameraButtonClick) {
                Text(text = "Camera")
            }
            Button(onClick = onGalleryButtonClick) {
                Text(text = "Select File")
            }
        }
    }
}