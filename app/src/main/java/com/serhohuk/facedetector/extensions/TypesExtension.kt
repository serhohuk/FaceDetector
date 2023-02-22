package com.serhohuk.facedetector.extensions

import kotlin.math.floor

fun Float.round(digits: Int): Float {
    var multiplier = 1f
    repeat(digits) { multiplier *= 10 }
    return floor(this * multiplier) / multiplier
}