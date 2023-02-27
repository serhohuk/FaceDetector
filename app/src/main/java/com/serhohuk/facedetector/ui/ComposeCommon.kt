package com.serhohuk.facedetector.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.serhohuk.facedetector.ui.theme.appColors
import com.serhohuk.facedetector.ui.theme.appShapes
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThicknessDialog(
    title: String,
    progress: Float,
    range: ClosedFloatingPointRange<Float>,
    onSaveClicked: (Float) -> Unit,
    onCancelClicked: () -> Unit
) {
    var sliderPosition by remember { mutableStateOf(progress) }

    AlertDialog(
        content = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.appColors.colors.background)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = title,
                        color = MaterialTheme.appColors.textPrimary
                    )
                    Spacer(Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = range.start.toString(),
                            color = MaterialTheme.appColors.textPrimary
                        )
                        Text(
                            text = range.endInclusive.toString(),
                            color = MaterialTheme.appColors.textPrimary
                        )
                    }
                    Slider(
                        modifier = Modifier.fillMaxWidth(),
                        value = sliderPosition,
                        valueRange = range,
                        steps = (range.endInclusive - 2).roundToInt(),
                        onValueChange = {
                            sliderPosition = it
                        }
                    )
                    Text(
                        text = sliderPosition.toString(),
                        color = MaterialTheme.appColors.textPrimary
                    )
                    Spacer(Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Button(onClick = { onSaveClicked(sliderPosition.roundToInt().toFloat()) }) {
                            Text(text = "Save")
                        }
                        Spacer(Modifier.width(12.dp))
                        Button(onClick = { onCancelClicked() }) {
                            Text(text = "Cancel")
                        }
                    }
                }
        },
        onDismissRequest = {}
    )
}