package com.serhohuk.facedetector.extensions

import android.app.Activity
import android.content.ContentValues
import android.graphics.*
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.serhohuk.facedetector.detection.FaceRect
import com.serhohuk.facedetector.R
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import kotlin.math.sqrt

fun Activity.drawDetectionResult(
    bitmap: Bitmap,
    detectionResults: List<FaceRect>
): Bitmap {
    val outputBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
    val canvas = Canvas(outputBitmap)
    val relation =  sqrt((canvas.width * canvas.height).toDouble()) / 450

    val paint = Paint().apply {
        textAlign = Paint.Align.LEFT
        isAntiAlias = true
    }
    for (faceRect in detectionResults) {
        paint.apply {
            color = Color.RED
            strokeWidth = (8 * relation).toFloat()
            style = Paint.Style.STROKE
        }
        canvas.drawRect(faceRect.rect, paint)
        val tagSize = Rect()

        paint.apply {
            style = Paint.Style.FILL_AND_STROKE
            color = Color.YELLOW
            strokeWidth = (1 * relation).toFloat()
            typeface = Typeface.DEFAULT

            textSize = (14 * relation).toFloat()
            getTextBounds("l: ${faceRect.leftEyeOpenProbability} r: ${faceRect.rightEyeOpenProbability}", 0, faceRect.text.length, tagSize)
        }
        val fontSize = paint.textSize * faceRect.rect.width() / tagSize.width()

        if (fontSize < paint.textSize) {
            paint.textSize = fontSize
        }

        var margin: Float = (faceRect.rect.width() - tagSize.width()) / 2.0f
        if (margin < 0f) margin = 0f
        canvas.drawText(
            "l: ${faceRect.leftEyeOpenProbability} r: ${faceRect.rightEyeOpenProbability}", faceRect.rect.left.toFloat(),
            (faceRect.rect.top + tagSize.height() - (20 * relation).toFloat()), paint
        )
        paint.apply {
            style = Paint.Style.FILL_AND_STROKE
            color = Color.YELLOW
            strokeWidth = (1 * relation).toFloat()

            textSize = (14 * relation).toFloat()
            getTextBounds("smile: ${faceRect.smileProbability}", 0, faceRect.text.length, tagSize)
        }
        canvas.drawText(
            "smile: ${faceRect.smileProbability}", faceRect.rect.left.toFloat(),
            (faceRect.rect.bottom + tagSize.height() + (6 * relation).toFloat()), paint
        )
    }
    return outputBitmap
}

fun Activity.scaleBitmap(bitmap: Bitmap): Bitmap {
    val desiredHeight = 1280
    return if(bitmap.height > desiredHeight) {
        bitmap
    } else {
        val scale = desiredHeight.toFloat() / bitmap.height.toFloat()
        val matrix = Matrix()
        matrix.postScale(scale, scale)
        val scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        scaledBitmap
    }
}

fun Activity.saveImage(bitmap: Bitmap) {
    val fos: OutputStream?
    val name = "IMG_" + System.currentTimeMillis()
    val folderName = getString(R.string.app_name)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
            put(MediaStore.MediaColumns.RELATIVE_PATH, "DCIM/$folderName")
        }
        val imageUri =
            contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        fos = contentResolver.openOutputStream(imageUri!!)
    } else {
        val imagesDir = Environment.getExternalStorageDirectory().toString() + File.separator + folderName

        val file = File(imagesDir)
        if (!file.exists()) {
            file.mkdir()
        }

        val image = File(file, "$name.png")
        fos = FileOutputStream(image)
    }

    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
    fos?.flush()
    fos?.close()
}