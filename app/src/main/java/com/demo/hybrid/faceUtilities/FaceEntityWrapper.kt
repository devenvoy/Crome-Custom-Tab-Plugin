package com.demo.hybrid.faceUtilities

import androidx.core.net.toUri

fun FaceEntity.toFaceEntry(): FaceEntry {
    return FaceEntry(
        embedding = embedding.split(",").map { it.toDouble() }.toDoubleArray(),
        imageUri = imageUri.toUri()
    )
}

fun FaceEntry.toEntity(): FaceEntity {
    return FaceEntity(
        imageUri = imageUri.toString(),
        embedding = embedding.joinToString(",")
    )
}
