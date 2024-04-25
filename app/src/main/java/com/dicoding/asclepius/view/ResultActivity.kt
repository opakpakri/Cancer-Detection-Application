package com.dicoding.asclepius.view

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.dicoding.asclepius.R
import com.dicoding.asclepius.database.CancerHistory
import com.dicoding.asclepius.database.LocalDatabase
import com.dicoding.asclepius.databinding.ActivityResultBinding
import com.dicoding.asclepius.helper.ImageClassifierHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.tensorflow.lite.task.vision.classifier.Classifications
import java.io.File
import java.io.FileOutputStream
import java.text.NumberFormat

class ResultActivity : AppCompatActivity(), ImageClassifierHelper.ClassifierListener {
    private lateinit var binding: ActivityResultBinding
    private lateinit var imageClassifierHelper: ImageClassifierHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_result)

        imageClassifierHelper = ImageClassifierHelper(
            context = this,
            classifierListener = this
        )

        val imageUriString = intent.getStringExtra("imageUri")
        val imageUri: Uri? = imageUriString?.let { Uri.parse(it) }

        binding.HistoryButton.setOnClickListener {
            val imageUriString = intent.getStringExtra("imageUri")
            val result = binding.resultText.text.toString()

            if (imageUriString != null) {
                val imageUri = Uri.parse(imageUriString)
                Log.d(TAG, "Images saved successfully: $imageUriString")
                Log.d(TAG, "Result saved successfully: $result")
                SaveToDatabase(imageUri, result)
            } else {
                Log.d(TAG, "Images Null: $imageUriString")
                finish()
            }
        }

        if (imageUri != null) {
            binding.resultImage.setImageURI(imageUri)
            imageClassifierHelper.classifyStaticImage(imageUri)
        } else {
            binding.resultText.text = "Invalid image URI"
        }
    }

    override fun onResult(result: List<Classifications>, inferenceTime: Long) {
        runOnUiThread {
            result?.let { classifications ->
                if (classifications.isNotEmpty() && classifications[0].categories.isNotEmpty()) {
                    val sortedCategories = classifications[0].categories.sortedByDescending { it?.score }
                    val displayResult = sortedCategories.joinToString("\n") {
                        "${it.label} " + NumberFormat.getPercentInstance().format(it.score).trim()
                    }
                    binding.resultText.text = displayResult
                } else {
                    binding.resultText.text = "No classification results available"
                }
            }
        }
    }

    private fun moveToHistory(imageUri: Uri, result: String) {
        val intent = Intent(this, CancerHistoryActivity::class.java)
        intent.putExtra(RESULT_TEXT, result)
        intent.putExtra(IMAGE_URI, imageUri.toString())
        setResult(RESULT_OK, intent)
        startActivity(intent)
        finish()
    }

    private fun SaveToDatabase(imageUri: Uri, result: String) {
        if (result.isNotEmpty()) {
            val fileName = "${System.currentTimeMillis()}.jpg"
            val destinationUri = Uri.fromFile(File(cacheDir, fileName))
            contentResolver.openInputStream(imageUri)?.use { input ->
                FileOutputStream(File(cacheDir, fileName)).use { output ->
                    input.copyTo(output)
                }
            }

            val history = CancerHistory(imagePath = destinationUri.toString(), result = result)
            GlobalScope.launch(Dispatchers.IO) {
                val database = LocalDatabase.getDatabase(applicationContext)
                try {
                    database.cancerHistoryDao().insertHistory(history)
                    Log.d(TAG, "History saved successfully: $history")
                    val historys = database.cancerHistoryDao().getAllHistory()
                    Log.d(TAG, "All predictions after save: $historys")
                    moveToHistory(destinationUri, result)
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to save prediction: $history", e)
                }
            }
        } else {
            Log.e(TAG, "Result is empty, cannot save prediction to database.")
        }
    }

    override fun onError(error: String) {
        runOnUiThread {
            Log.e(TAG, "Error: $error")
        }
    }

    companion object {
        const val IMAGE_URI = "img_uri"
        const val RESULT_TEXT = "result_text"
        private const val TAG = "ResultActivity"
    }
}