package com.demo.hybrid.faceUtilities

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import androidx.room.Update

@Dao
interface FaceDao {
    @Query("SELECT * FROM faces")
    suspend fun getAllFaces(): List<FaceEntity>

    @Insert(onConflict = REPLACE)
    suspend fun insertFaces(faces: List<FaceEntity>)

    @Update
    suspend fun updateFaces(faces: List<FaceEntity>)
}
