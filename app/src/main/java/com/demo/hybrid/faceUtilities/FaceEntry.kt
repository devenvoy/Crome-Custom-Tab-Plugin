package com.demo.hybrid.faceUtilities

import android.net.Uri

data class FaceEntry(
    val embedding: DoubleArray,
    val imageUri: Uri
)
