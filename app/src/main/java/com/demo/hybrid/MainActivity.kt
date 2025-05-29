package com.demo.hybrid


import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.CompositionLocalProvider
import com.demo.hybrid.databinding.ActivityMainBinding
import kotlin.math.max
import kotlin.math.min


class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private lateinit var embeddingExtractor: EmbeddingExtractor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        embeddingExtractor = EmbeddingExtractor(this)
        initImagePicker()
        binding.someBtn.setOnClickListener { pickImageAndGetUri() }

        binding.composeCheckbox.setContent {


            val theme = UseTheme()
            CompositionLocalProvider(LocalTheme provides theme){

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


    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>

    private fun initImagePicker() {
        imagePickerLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageUri: Uri? = result.data?.data
                if (imageUri != null) {
                    try {
                        contentResolver.takePersistableUriPermission(
                            imageUri,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION
                        )
                    } catch (e: SecurityException) {
                        e.message?.let { Log.d("Embedding", it) }
                        Toast.makeText(this, "Permission error", Toast.LENGTH_SHORT).show()
                    }


                    FaceDetector.detectFaces(this, imageUri) { faces, bitmap ->
                        for (face in faces) {

                            val cropped = cropFace(bitmap, face.boundingBox)

                            binding.imageView.setImageBitmap(cropped)

                            val embedding = embeddingExtractor.getEmbedding(cropped)

                            Log.d("Embedding", embedding.joinToString())
                        }
                    }


                } else {
                    Log.d("Embedding", "No image selected")
                    Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show()
                }
            } else {
                Log.d("Embedding", "Cancelled")
                Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun pickImageAndGetUri() {
        val intent =
            Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "image/*"
            }
        imagePickerLauncher.launch(intent)
    }

}
