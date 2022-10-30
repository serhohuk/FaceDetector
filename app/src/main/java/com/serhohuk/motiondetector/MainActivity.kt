package com.serhohuk.motiondetector

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.camera.core.CameraSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.serhohuk.motiondetector.ui.theme.MotionDetectorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (permissionGranted()) {
            initView()
        } else {
            requestPermission()
        }

    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 0)
    }

    private fun permissionGranted() = ContextCompat.checkSelfPermission(
        this, Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED

    private fun initView() {
        setContent {
            MotionDetectorTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        var lens = remember {
                            mutableStateOf(CameraSelector.LENS_FACING_FRONT)
                        }
                        CameraPreview(cameraLens = lens.value)
                        Controls(onLensChange = { lens.value = switchLens(lens.value) })
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initView()
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

@Composable
fun CameraPreview(
    modifier: Modifier = Modifier,
    cameraLens: Int,
    scaleType: PreviewView.ScaleType = PreviewView.ScaleType.FILL_CENTER,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val previewView = remember { PreviewView(context) }
    val cameraProviderFuture =
        ProcessCameraProvider.getInstance(context)
            .configureCamera(previewView, lifecycleOwner, cameraLens, context)
    AndroidView(
        modifier = modifier,
        factory = {
            previewView.apply {
                this.scaleType = scaleType
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                // Preview is incorrectly scaled in Compose on some devices without this
                implementationMode = PreviewView.ImplementationMode.COMPATIBLE
            }

            previewView
        })
}

@Composable
fun Controls(
    onLensChange: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 24.dp),
        contentAlignment = Alignment.BottomCenter,
    ) {
        Button(
            onClick = { onLensChange() },
            modifier = Modifier.wrapContentSize(),
            shape = CircleShape
        ) { Icon(Icons.Filled.Cameraswitch, contentDescription = "Switch camera") }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MotionDetectorTheme {
        Greeting("Android")
    }
}