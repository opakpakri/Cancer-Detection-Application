package com.dicoding.asclepius.helper

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.SystemClock
import android.provider.MediaStore
import android.util.Log
import com.dicoding.asclepius.R
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.common.ops.CastOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.task.core.BaseOptions
import org.tensorflow.lite.task.vision.classifier.Classifications
import org.tensorflow.lite.task.vision.classifier.ImageClassifier
import java.lang.IllegalStateException

class ImageClassifierHelper(
    val threshold: Float = 0.1f,
    var maxResult: Int = 2,
    val nameModel: String = "cancer_classification.tflite",
    val context: Context,
    val classifierListener: ClassifierListener?
) {
    private var imageClassifier: ImageClassifier? = null

    interface ClassifierListener {
        fun onError(errorMsg: String)
        fun onResult(
            result: List<Classifications>,
            inferenceTime: Long
        )
    }

    init {
        setupImageClassifier()
    }

    private fun setupImageClassifier() {
        val optionBuilder = ImageClassifier.ImageClassifierOptions.builder()
            .setScoreThreshold(threshold)
            .setMaxResults(maxResult)
        val baseOptionBuilder = BaseOptions.builder()
            .setNumThreads(6)
        optionBuilder.setBaseOptions(baseOptionBuilder.build())

        try {
            imageClassifier = ImageClassifier.createFromFileAndOptions(
                context,
                nameModel,
                optionBuilder.build()
            )
        } catch (e: IllegalStateException) {
            val errorMessage = context.getString(R.string.image_classifier_failed)
            classifierListener?.onError(errorMessage)
            Log.e(TAG, errorMessage, e)
        }
    }

    fun classifyStaticImage(imageUri: Uri) {
        if (imageClassifier == null) {
            setupImageClassifier()
        }

        val bitmap = getImageBitmap(imageUri)
        val tensorImage = preprocessImage(bitmap)
        val result = performInference(tensorImage)
        notifyResults(result)
    }

    private fun notifyResults(result: List<Classifications>?) {
        val inferenceTime = SystemClock.uptimeMillis()
        classifierListener?.onResult(result ?: emptyList(), inferenceTime)
    }

    private fun performInference(tensorImage: TensorImage): List<Classifications>? {
        val inferenceTime = SystemClock.uptimeMillis()
        val result = imageClassifier?.classify(tensorImage)
        Log.d(TAG, "Inference Time: ${SystemClock.uptimeMillis() - inferenceTime} ms")
        return result
    }

    private fun preprocessImage(bitmap: Bitmap): TensorImage {
        val imageProcessor = ImageProcessor.Builder()
            .add(ResizeOp(224, 224, ResizeOp.ResizeMethod.NEAREST_NEIGHBOR))
            .add(CastOp(DataType.UINT8))
            .build()

        return imageProcessor.process(TensorImage.fromBitmap(bitmap))
    }

    private fun checkVersion(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
    }

    private fun getImageBitmap(imageUri: Uri): Bitmap {
        return if (checkVersion()) {
            val source = ImageDecoder.createSource(context.contentResolver, imageUri)
            ImageDecoder.decodeBitmap(source)
        } else {
            MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri)
        }.copy(Bitmap.Config.ARGB_8888, true)
    }

    companion object {
        private const val TAG = "ImageClassifierHelper"
    }
}
