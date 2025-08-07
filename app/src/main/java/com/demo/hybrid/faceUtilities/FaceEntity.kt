package com.demo.hybrid.faceUtilities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "faces")
data class FaceEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val imageUri: String,
    val clusterId: Int? = null,  // assigned after clustering
    val embedding: String        // store embedding as comma-separated floats
)
