package com.demo.hybrid

import android.content.Context
import android.graphics.Bitmap
import org.tensorflow.lite.Interpreter
import java.nio.ByteBuffer
import java.nio.ByteOrder
import androidx.core.graphics.scale
import androidx.core.graphics.get

class EmbeddingExtractor(context: Context) {
    private val interpreter: Interpreter

    init {
        val model = context.assets.open("facenet.tflite").readBytes()
        val buffer = ByteBuffer.allocateDirect(model.size).apply {
            order(ByteOrder.nativeOrder())
            put(model)
            rewind()
        }
        interpreter = Interpreter(buffer)
    }

    fun getEmbedding(bitmap: Bitmap): FloatArray {
        val input = preprocess(bitmap)
        val output = Array(1) { FloatArray(128) }
        interpreter.run(input, output)
        return output[0]
    }

    private fun preprocess(bitmap: Bitmap): ByteBuffer {
        val resized = bitmap.scale(160, 160)
        val input = ByteBuffer.allocateDirect(1 * 160 * 160 * 3 * 4).apply {
            order(ByteOrder.nativeOrder())
        }

        for (y in 0 until 160) {
            for (x in 0 until 160) {
                val px = resized[x, y]
                input.putFloat(((px shr 16 and 0xFF) - 127.5f) / 128f) // R
                input.putFloat(((px shr 8 and 0xFF) - 127.5f) / 128f)  // G
                input.putFloat(((px and 0xFF) - 127.5f) / 128f)       // B
            }
        }
        input.rewind()
        return input
    }
}