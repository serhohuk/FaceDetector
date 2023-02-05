package com.serhohuk.motiondetector

import android.app.Activity
import android.content.ContentValues
import android.graphics.*
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

fun Activity.drawDetectionResult(
    bitmap: Bitmap,
    detectionResults: List<FaceRect>
): Bitmap {
    val outputBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
    val canvas = Canvas(outputBitmap)

    val paint = Paint().apply {
        textAlign = Paint.Align.LEFT
    }
    for (faceRect in detectionResults) {
        paint.apply {
            color = Color.RED
            strokeWidth = 8F
            style = Paint.Style.STROKE
        }
        canvas.drawRect(faceRect.rect, paint)
        val tagSize = Rect(0, 0, 0, 0)

        paint.apply {
            style = Paint.Style.FILL_AND_STROKE
            color = Color.YELLOW
            strokeWidth = 2F

            textSize = 96F
            getTextBounds(faceRect.text, 0, faceRect.text.length, tagSize)
        }
        val fontSize = paint.textSize * faceRect.rect.width() / tagSize.width()

        if (fontSize < paint.textSize) {
            paint.textSize = fontSize
        }

        var margin: Float = (faceRect.rect.width() - tagSize.width()) / 2.0f
        if (margin < 0f) margin = 0f
        canvas.drawText(
            faceRect.text, faceRect.rect.left + margin,
            (faceRect.rect.top + tagSize.height()).toFloat(), paint
        );
    }
    return outputBitmap
}

fun Activity.saveImage(bitmap: Bitmap) {
    var fos: OutputStream? = null
    val name = "IMG_" + System.currentTimeMillis()
    val IMAGES_FOLDER_NAME = getString(R.string.app_name)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
            put(MediaStore.MediaColumns.RELATIVE_PATH, "DCIM/$IMAGES_FOLDER_NAME")
        }
        val imageUri =
            contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        fos = contentResolver.openOutputStream(imageUri!!)
    } else {
        val imagesDir = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_DCIM
        ).toString() + File.separator + IMAGES_FOLDER_NAME

        val file = File(imagesDir)
        if (!file.exists()) {
            file.mkdir()
        }

        val image = File(imagesDir, "$name.png")
        fos = FileOutputStream(image)
    }

    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
    fos?.flush()
    fos?.close()
}