package com.serhohuk.facedetector.presentation.settings

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Colorize
import androidx.compose.material.icons.filled.LineWeight
import androidx.compose.material.icons.filled.Sick
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.alorma.compose.settings.storage.base.rememberBooleanSettingState
import com.alorma.compose.settings.ui.SettingsMenuLink
import com.alorma.compose.settings.ui.SettingsSwitch
import com.serhohuk.facedetector.R
import com.serhohuk.facedetector.ui.ThicknessDialog
import com.serhohuk.facedetector.ui.theme.FaceDetectionTheme
import com.serhohuk.facedetector.ui.theme.appColors
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.color.ColorPalette
import com.vanpra.composematerialdialogs.color.colorChooser
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private val viewModel by viewModels<SettingsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = ComposeView(requireContext()).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        setContent {
            FaceDetectionTheme() {

                val uiState by viewModel.uiState.collectAsState()

                SettingsScreen(
                    uiState = uiState,
                    onEyeStateChanged = {
                        viewModel.setEyeDetection(it)
                    },
                    onSmileChanged = {
                        viewModel.setWithSmileDetection(it)
                    },
                    onFrameColorChanged = {
                        viewModel.setFrameColor(it)
                    },
                    onTextColorChanged = {
                        viewModel.setTextColor(it)
                    },
                    onFrameThicknessChanged = {
                        viewModel.setFrameThickness(it)
                    },
                    onTextSizeChanged = {
                        viewModel.setTextSize(it)
                    }
                )
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    uiState: SettingsFragmentUIState,
    onEyeStateChanged: (Boolean) -> Unit,
    onSmileChanged: (Boolean) -> Unit,
    onFrameThicknessChanged: (Float) -> Unit,
    onTextSizeChanged: (Float) -> Unit,
    onTextColorChanged: (ULong) -> Unit,
    onFrameColorChanged: (ULong) -> Unit
) {
    val eyeOpenState = rememberBooleanSettingState(uiState.settings.withEyeOpen)
    val withSmile = rememberBooleanSettingState(uiState.settings.withSmileProbability)

    var thicknessFrameDialogOpened by remember {
        mutableStateOf(false)
    }

    var textSizeDialogOpened by remember {
        mutableStateOf(false)
    }

    var textColorDialogOpened by remember {
        mutableStateOf(false)
    }

    var frameColorDialogOpened by remember {
        mutableStateOf(false)
    }

    var isFirstSelection by remember {
        mutableStateOf(true)
    }

    val dialogState = rememberMaterialDialogState()

    Scaffold(
        contentColor = MaterialTheme.appColors.background
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
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
                        text = stringResource(id = R.string.setings),
                        color = MaterialTheme.appColors.textPrimary,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                SettingsSwitch(
                    title = {
                        Text(text = "Eye detection")
                    },
                    subtitle = {
                        Text(text = "Detect if eye is open")
                    },
                    state = eyeOpenState,
                    onCheckedChange = onEyeStateChanged
                )
                Divider()
                SettingsSwitch(
                    title = {
                        Text(text = "Smile detection")
                    },
                    subtitle = {
                        Text(text = "Detect smiling probability")
                    },
                    state = withSmile,
                    onCheckedChange = onSmileChanged
                )
                Divider()
                SettingsMenuLink(
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.LineWeight,
                            contentDescription = "LineWeight"
                        )
                    },
                    title = { Text(text = "Frame Detection") },
                    subtitle = { Text(text = "Thickness") },
                    onClick = {
                        thicknessFrameDialogOpened = true
                    },
                )
                Divider()
                SettingsMenuLink(
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.Sick,
                            contentDescription = "LineWeight"
                        )
                    },
                    title = { Text(text = "Text detection") },
                    subtitle = { Text(text = "size") },
                    onClick = {
                        textSizeDialogOpened = true
                    },
                )
                Divider()
                SettingsMenuLink(
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.Colorize,
                            contentDescription = "Colorize"
                        )
                    },
                    title = { Text(text = "Text Detection") },
                    subtitle = { Text(text = "color") },
                    onClick = {
                        textColorDialogOpened = true
                        dialogState.show()
                    },
                )
                Divider()
                SettingsMenuLink(
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.Colorize,
                            contentDescription = "Colorize"
                        )
                    },
                    title = { Text(text = "Frame Detection") },
                    subtitle = { Text(text = "color") },
                    onClick = {
                        frameColorDialogOpened = true
                        dialogState.show()
                    },
                )
                Divider()
            }
            if (thicknessFrameDialogOpened) {
                ThicknessDialog(
                    title = "Frame thickness",
                    progress = uiState.settings.frameThickness,
                    range = 1f..10f,
                    onSaveClicked = {
                        onFrameThicknessChanged(it)
                        thicknessFrameDialogOpened = false
                    },
                    onCancelClicked = {
                        thicknessFrameDialogOpened = false
                    }
                )
            }
            if (textSizeDialogOpened) {
                ThicknessDialog(
                    title = "Text size",
                    progress = uiState.settings.textSize,
                    range = 12f..24f,
                    onSaveClicked = {
                        onTextSizeChanged(it)
                        textSizeDialogOpened = false
                    },
                    onCancelClicked = {
                        textSizeDialogOpened = false
                    }
                )
            }
            MaterialDialog(
                dialogState = dialogState,
                properties = DialogProperties(
                    dismissOnClickOutside = false,
                    dismissOnBackPress = false
                )
            ) {
                colorChooser(colors = ColorPalette.Primary + Color.Transparent,
                    waitForPositiveButton = false,
                    initialSelection = ColorPalette.Primary.size,
                    onColorSelected = {
                        if (!isFirstSelection) {
                            if (textColorDialogOpened) {
                                onTextColorChanged(it.value)
                                textColorDialogOpened = false
                            } else if (frameColorDialogOpened) {
                                onFrameColorChanged(it.value)
                                frameColorDialogOpened = false
                            }
                            isFirstSelection = true
                            dialogState.hide()
                        } else {
                            isFirstSelection = false
                        }
                    })
            }
        }
    }
}

@Preview(uiMode = UI_MODE_NIGHT_NO)
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun SettingsScreenPreview() {
    FaceDetectionTheme {

    }
}