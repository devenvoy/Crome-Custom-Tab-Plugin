package com.demo.hybrid.faceUtilities

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.core.net.toUri
import androidx.exifinterface.media.ExifInterface
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.android.gms.maps.model.LatLng
import com.google.mlkit.vision.face.Face
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import okio.IOException
import kotlin.coroutines.resume
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

class FaceProcessingWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        Log.e("FaceScan", "Started face processing worker")
        return try {
            val faceDao = DatabaseProvider.getDatabase(applicationContext).faceDao()
            val embeddingExtractor = EmbeddingExtractor(applicationContext)
            scanGallery(applicationContext, faceDao, embeddingExtractor)
            Log.e("FaceScan", "Completed face processing worker")
            Result.success()
        } catch (e: Exception) {
            Log.e("FaceScan", "Worker failed", e)
            Result.failure()
        }
    }

    private suspend fun scanGallery(
        context: Context,
        faceDao: FaceDao,
        embeddingExtractor: EmbeddingExtractor
    ) = withContext(Dispatchers.IO) {
        val imageUris = async { getAllImageUris(context) }.await()
        val existingFaces = faceDao.getAllFaces().toMutableList()
        var nextClusterId = (existingFaces.maxOfOrNull { it.clusterId ?: 0 } ?: 0) + 1

        Log.e("FaceScan", "Found ${imageUris.size} images to scan")
        for (uri in imageUris) {
            val result = detectFacesSuspending(context, uri)
            if (result == null) {
                Log.e("FaceScan", "No faces or bitmap for $uri")
                continue
            }

            val (faces, bitmap) = result
            Log.e("FaceScan", "Found ${faces.size} face(s) in image $uri")

            for (face in faces) {
                val cropped = cropFace(bitmap, face.boundingBox)
                val embedding = embeddingExtractor.getEmbedding(cropped)

                val existingMatch = findMatchingFace(embedding, existingFaces, threshold = 0.8)

                val clusterId = if (existingMatch != null) {
                    existingMatch.clusterId ?: nextClusterId++
                } else {
                    nextClusterId++
                }
                val faceEntity = FaceEntity(
                    imageUri = uri.toString(),
                    embedding = embedding.joinToString(","),
                    clusterId = clusterId
                )

                if (existingMatch == null) {
                    faceDao.insertFaces(listOf(faceEntity))
                    existingFaces += faceEntity
                    Log.e("FaceScan", "Inserted face for image $uri with cluster $clusterId")
                } else {
                    Log.e(
                        "FaceScan",
                        "Duplicate face skipped for image $uri (matched with cluster $clusterId)"
                    )
                }
            }
        }
    }

    private fun cropFace(bitmap: Bitmap, box: Rect): Bitmap {
        val margin = 500
        val left = max(box.left - margin, 0)
        val top = max(box.top - margin, 0)
        val right = min(box.right + margin, bitmap.width)
        val bottom = min(box.bottom + margin, bitmap.height)

        // Calculate the new width and height
        val width = right - left
        val height = bottom - top

        return Bitmap.createBitmap(bitmap, left, top, width, height)
    }

    private suspend fun detectFacesSuspending(
        context: Context,
        imageUri: Uri
    ): Pair<List<Face>, Bitmap>? = suspendCancellableCoroutine { cont ->
        try {
            val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri)
            val image = com.google.mlkit.vision.common.InputImage.fromFilePath(context, imageUri)

            val detector = com.google.mlkit.vision.face.FaceDetection.getClient(
                com.google.mlkit.vision.face.FaceDetectorOptions.Builder()
                    .setPerformanceMode(com.google.mlkit.vision.face.FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                    .build()
            )

            detector.process(image)
                .addOnSuccessListener { faces ->
                    cont.resume(faces to bitmap)
                }
                .addOnFailureListener { e ->
                    Log.e("FaceScan", "Face detection failed for $imageUri", e)
                    cont.resume(null)
                }
        } catch (e: Exception) {
            Log.e("FaceScan", "Exception processing $imageUri", e)
            cont.resume(null)
        }
    }


    private fun findMatchingFace(
        embedding: DoubleArray,
        existing: List<FaceEntity>,
        threshold: Double
    ): FaceEntity? {
        fun cosineSimilarity(a: DoubleArray, b: DoubleArray): Float {
            val dot = a.zip(b).sumOf { it.first * it.second }
            val magA = sqrt(a.sumOf { it * it })
            val magB = sqrt(b.sumOf { it * it })
            return (dot / (magA * magB)).toFloat()
        }

        val parsedExisting = existing.mapNotNull {
            try {
                it to it.embedding.split(",").map(String::toDouble).toDoubleArray()
            } catch (e: Exception) {
                null
            }
        }

        return parsedExisting.maxByOrNull { cosineSimilarity(embedding, it.second) }
            ?.takeIf { cosineSimilarity(embedding, it.second) >= threshold }
            ?.first
    }


}

fun getAllImageUris(context: Context): List<Uri> {
    val imageUris = mutableListOf<Uri>()
    val uri: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

    val projection =
        arrayOf(
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.DATA,
            MediaStore.Files.FileColumns.DISPLAY_NAME,
            MediaStore.Files.FileColumns.DATE_MODIFIED,
            MediaStore.Files.FileColumns.MIME_TYPE,
            MediaStore.Files.FileColumns.SIZE
        )

    val orderBy: String = MediaStore.Video.Media.DATE_TAKEN

    val query = context.contentResolver.query(uri, projection, null, null, "$orderBy DESC")

    if (query == null) {
        Log.e("FaceScan", "Query returned null")
        return emptyList()
    }

    var count = 0
    val columnIndexData = query.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
    while (query.moveToNext()) {
        var photoUri = Uri.withAppendedPath(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            query.getString(columnIndexData)
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            photoUri = MediaStore.setRequireOriginal(photoUri)
        }
        val locationInfo = getLocationInfoFromPhoto(context.contentResolver, photoUri)
        val absolutePathOfImage =
            ContentUris.withAppendedId(uri, query.getLong(columnIndexData)).toString()
        imageUris.add(absolutePathOfImage.toUri())
        count++
    }
    Log.e("FaceScan", "Query returned $count images")
    query.close()
    return imageUris
}

private fun getLocationInfoFromPhoto(contentResolver: ContentResolver, photoUri: Uri): LatLng? {
    return try {
        contentResolver.openInputStream(photoUri).use { inputStream ->
            val exif = inputStream?.let { ExifInterface(it) }
            GeoDegreeConverter(exif).latLng
        }
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }
}

class GeoDegreeConverter(exif: ExifInterface?) {
    var latLng: LatLng? = null

    init {
        val latitude = exif?.getAttribute(ExifInterface.TAG_GPS_LATITUDE)
        val latitudeRef = exif?.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF)
        val longitude = exif?.getAttribute(ExifInterface.TAG_GPS_LONGITUDE)
        val longitudeRef = exif?.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF)
        if (latitude != null
            && latitudeRef != null
            && longitude != null
            && longitudeRef != null
        ) {
            val lat = if (latitudeRef == "N") {
                convertToDegree(latitude).toDouble()
            } else {
                (0 - convertToDegree(latitude)).toDouble()
            }
            val lng = if (longitudeRef == "E") {
                convertToDegree(longitude).toDouble()
            } else {
                (0 - convertToDegree(longitude)).toDouble()
            }

            latLng = LatLng(lat, lng)
        }
    }


    private fun convertToDegree(stringDMS: String): Float {
        val dms = stringDMS.split(",")
        val stringD = dms[0].split("/")
        val d0 = stringD[0].toDouble()
        val d1 = stringD[1].toDouble()
        val d = d0 / d1
        val stringM = dms[1].split("/")
        val m0 = stringM[0].toDouble()
        val m1 = stringM[1].toDouble()
        val m = m0 / m1
        val stringS = dms[2].split("/")
        val s0 = stringS[0].toDouble()
        val s1 = stringS[1].toDouble()
        val s = s0 / s1
        return (d + m / 60 + s / 3600).toFloat()
    }
}