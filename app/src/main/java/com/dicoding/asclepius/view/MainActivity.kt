package com.dicoding.asclepius.view

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.dicoding.asclepius.databinding.ActivityMainBinding
import com.yalantis.ucrop.UCrop
import java.io.File

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private var currentImageUri: Uri? = null
    private var croppedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.galleryButton.setOnClickListener {
            startGallery()
        }

        binding.analyzeButton.setOnClickListener {
            analyzeImage()
        }

        binding.HistoryButton.setOnClickListener {
            moveToHistory()
        }
    }

    private fun startGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg = result.data?.data
            selectedImg?.let { uri ->
                currentImageUri = uri
                showImage()
                startUCrop(uri)
            } ?: showToast("Failed to get image URI")
        }
    }

    private fun startUCrop(sourceUri: Uri) {
        val fileName = "cropped_image_${System.currentTimeMillis()}.jpg"
        val destinationUri = Uri.fromFile(File(cacheDir, fileName))
        UCrop.of(sourceUri, destinationUri)
            .withAspectRatio(1f, 1f)
            .withMaxResultSize(1000, 1000)
            .start(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK) {
            val resultUri = UCrop.getOutput(data!!)
            resultUri?.let {
                showCroppedImage(resultUri)
            } ?: showToast("Failed to crop image")
        } else if (resultCode == UCrop.RESULT_ERROR) {
            val cropError = UCrop.getError(data!!)
            showToast("Crop error: ${cropError?.message}")
        }
    }

    private fun showCroppedImage(uri: Uri) {
        binding.previewImageView.setImageURI(uri)
        croppedImageUri = uri
    }

    private fun showImage() {
        currentImageUri?.let { uri ->
            Log.d(TAG, "Displaying image: $uri")
            binding.previewImageView.setImageURI(uri)
        } ?: Log.d(TAG, "No image to display")
    }

    private fun analyzeImage() {
        if (croppedImageUri != null) {
            moveToResult()
        } else {
            showToast("Please select an image first")
        }
    }

    private fun moveToResult() {
        val intent = Intent(this, ResultActivity::class.java)
        intent.putExtra("imageUri", croppedImageUri.toString())
        startActivity(intent)
    }

    private fun moveToHistory() {
        val intent = Intent(this, CancerHistoryActivity::class.java)
        startActivity(intent)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}