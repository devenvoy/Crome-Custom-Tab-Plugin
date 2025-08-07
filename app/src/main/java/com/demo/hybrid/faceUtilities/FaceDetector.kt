package com.demo.hybrid.faceUtilities

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions

object FaceDetector {
    private val options = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
        .build()

    private val detector = FaceDetection.getClient(options)

    fun detectFaces(
        context: Context,
        imageUri: Uri,
        onFacesDetected: (List<Face>, Bitmap) -> Unit
    ) {
        val image = InputImage.fromFilePath(context, imageUri)

        val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri)

        detector.process(image)
            .addOnSuccessListener { faces ->
                onFacesDetected(faces, bitmap)
            }
            .addOnFailureListener {
                it.printStackTrace()
            }
    }
}