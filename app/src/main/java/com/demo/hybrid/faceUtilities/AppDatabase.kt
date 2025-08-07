
package com.demo.hybrid.faceUtilities

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [FaceEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun faceDao(): FaceDao
}
