package com.simats.speakometer

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.simats.speakometer.network.VoiceApiClient
import com.simats.speakometer.network.AnalyzeResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class RecordedAudioAnalysisActivity : AppCompatActivity() {

    private lateinit var progressBar: ProgressBar
    private lateinit var statusFiller: TextView
    private lateinit var statusTone: TextView
    private lateinit var statusScore: TextView

    private val handler = Handler(Looper.getMainLooper())
    private var currentProgress = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recorded_audio_analysis)

        // Initialize Views
        progressBar = findViewById(R.id.analysis_progress_bar)
        statusFiller = findViewById(R.id.status_filler)
        statusTone = findViewById(R.id.status_tone)
        statusScore = findViewById(R.id.status_score)

        startAnalysisSimulation()
    }

    private fun startAnalysisSimulation() {
        val activeColor = ContextCompat.getColor(this, R.color.cyan_main)

        // 1. Visually trigger all the loaders
        progressBar.progress = 50
        statusFiller.setTextColor(activeColor)
        statusTone.setTextColor(activeColor)

        // 2. Get the real audio file path from RecordingAudioActivity
        val audioFilePath = intent.getStringExtra("AUDIO_FILE_PATH")

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val audioFile = if (audioFilePath != null) {
                    File(audioFilePath)
                } else {
                    // Fallback: create dummy file if no path provided
                    val dummy = File(cacheDir, "test_audio.mp4")
                    if (!dummy.exists()) dummy.writeText("fake audio file bytes")
                    dummy
                }

                if (!audioFile.exists()) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@RecordedAudioAnalysisActivity, "Audio file not found", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    return@launch
                }

                val requestFile = audioFile.asRequestBody("audio/mp4".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("file", audioFile.name, requestFile)

                // 3. Make the API Call to Python
                val response = VoiceApiClient.analysisService.analyzeSpeech(body)

                withContext(Dispatchers.Main) {
                    progressBar.progress = 100
                    statusScore.setTextColor(activeColor)

                    if (response.isSuccessful) {
                        val analysis = response.body()
                        if (analysis != null) {
                            navigateToResults(analysis)
                        } else {
                            Toast.makeText(this@RecordedAudioAnalysisActivity, "Empty response", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    } else {
                        Toast.makeText(this@RecordedAudioAnalysisActivity, "Error: ${response.code()}", Toast.LENGTH_LONG).show()
                        finish()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@RecordedAudioAnalysisActivity, "Upload Failed: ${e.message}", Toast.LENGTH_LONG).show()
                    finish()
                }
            }
        }
    }

    private fun updateStatusMilestones(progress: Int) {
        // This now uses the color you just added to colors.xml
        val activeColor = ContextCompat.getColor(this, R.color.cyan_main)

        if (progress >= 30) statusFiller.setTextColor(activeColor)
        if (progress >= 60) statusTone.setTextColor(activeColor)
        if (progress >= 90) statusScore.setTextColor(activeColor)
    }

    private fun navigateToResults(analysis: AnalyzeResponse) {
        // Capture the incoming intent's practice flags BEFORE the apply block
        val isPractice = this.intent.getBooleanExtra("IS_PRACTICE_MODE", false)
        val practiceTopic = this.intent.getStringExtra("PRACTICE_TOPIC")

        val intent = Intent(this, AnalysisResultsActivity::class.java).apply {
            putExtra("EXTRA_SCORE", analysis.confidenceScore ?: 0)
            putExtra("EXTRA_WPM", analysis.wpm ?: 0.0)
            putExtra("EXTRA_TONE", analysis.toneAnalysis ?: "Unknown")
            putExtra("EXTRA_FILLERS", analysis.fillerCount ?: 0)
            putExtra("EXTRA_ACCENT", analysis.accentScore ?: 0.0)

            // Forward practice flags with keys that AnalysisResultsActivity reads
            putExtra("EXTRA_IS_PRACTICE", isPractice)
            putExtra("EXTRA_PRACTICE_TOPIC", practiceTopic)
        }
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}