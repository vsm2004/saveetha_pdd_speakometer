package com.simats.speakometerfrontend

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.google.android.material.card.MaterialCardView
import java.util.Locale

class UploadingAudioActivity : AppCompatActivity() {

    private lateinit var tvMaxDuration: TextView
    private lateinit var btnSubmit: AppCompatButton
    private var selectedFilePath: String? = null

    private val filePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri = result.data?.data
            uri?.let { validateAndProcessFile(it) }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_uploading_audio)

        val btnBack = findViewById<ImageView>(R.id.btn_back)
        val uploadCard = findViewById<MaterialCardView>(R.id.upload_card)
        tvMaxDuration = findViewById(R.id.tv_max_duration)
        btnSubmit = findViewById(R.id.btn_submit_analysis)

        // UI Setup
        btnSubmit.isEnabled = false
        btnSubmit.alpha = 0.5f

        btnBack.setOnClickListener { finish() }
        uploadCard.setOnClickListener { openFilePicker() }

        btnSubmit.setOnClickListener {
            val intent = Intent(this, RecordedAudioAnalysisActivity::class.java)
            intent.putExtra("IS_UPLOAD", true)
            intent.putExtra("AUDIO_FILE_PATH", selectedFilePath)
            startActivity(intent)
            finish()
        }
    }

    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "audio/mpeg"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        filePickerLauncher.launch(intent)
    }

    private fun validateAndProcessFile(uri: Uri) {
        val fileSize = getFileSize(uri)
        val fileName = getFileName(uri)
        val durationMillis = getFileDuration(uri)

        // 1. Format Check
        if (!fileName.lowercase(Locale.ROOT).endsWith(".mp3")) {
            showError(getString(R.string.error_invalid_format))
            return
        }

        // 2. Size Check (5MB)
        if (fileSize > 5 * 1024 * 1024) {
            showError(getString(R.string.error_file_too_large))
            return
        }

        // 3. Duration Check (Min 5s, Max 3 Minutes)
        if (durationMillis > 180000) {
            showError(getString(R.string.error_file_too_long))
            return
        }
        if (durationMillis < 5000) {
            val intent = Intent(this, TooShortAudioActivity::class.java)
            startActivity(intent)
            return
        }

        // 4. Anti-Exploit Keyword Check
        val musicKeywords = listOf("song", "track", "music", "remix", "score")
        if (musicKeywords.any { fileName.lowercase(Locale.ROOT).contains(it) }) {
            showError(getString(R.string.error_music_detected))
            Toast.makeText(this, "It's not correct audio", Toast.LENGTH_LONG).show()
            return
        }

        // SUCCESS: Passing both fileName and fileSizeMb to fix the argument count error
        try {
            val inputStream = contentResolver.openInputStream(uri)
            val cacheFile = java.io.File(cacheDir, "upload_audio.mp3")
            inputStream?.use { input ->
                cacheFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            selectedFilePath = cacheFile.absolutePath
        } catch (e: Exception) {
            showError("Error processing file")
            return
        }
        val fileSizeMb = fileSize / (1024.0 * 1024.0)

        tvMaxDuration.text = getString(R.string.file_selected_info, fileName, fileSizeMb)
        tvMaxDuration.setTextColor(ContextCompat.getColor(this, R.color.cyan_main))

        btnSubmit.isEnabled = true
        btnSubmit.alpha = 1.0f
        Toast.makeText(this, getString(R.string.toast_speech_validated), Toast.LENGTH_SHORT).show()
    }

    private fun getFileDuration(uri: Uri): Long {
        val retriever = android.media.MediaMetadataRetriever()
        return try {
            retriever.setDataSource(this, uri)
            val time = retriever.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_DURATION)
            time?.toLong() ?: 0L
        } catch (e: Exception) {
            0L
        } finally {
            retriever.release()
        }
    }

    private fun getFileSize(uri: Uri): Long {
        return contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
            if (cursor.moveToFirst()) cursor.getLong(sizeIndex) else 0L
        } ?: 0L
    }

    private fun getFileName(uri: Uri): String {
        return contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (cursor.moveToFirst()) cursor.getString(nameIndex) else "unknown.mp3"
        } ?: "unknown.mp3"
    }

    private fun showError(message: String) {
        tvMaxDuration.text = message
        tvMaxDuration.setTextColor(android.graphics.Color.RED)
        btnSubmit.isEnabled = false
        btnSubmit.alpha = 0.5f
    }
}