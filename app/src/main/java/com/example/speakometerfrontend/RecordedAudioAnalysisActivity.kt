package com.example.speakometerfrontend

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

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
        val tickInterval = 40L
        // We remove the unused 'increment' variable to clear the warning

        val progressRunnable = object : Runnable {
            override fun run() {
                if (currentProgress < 100) {
                    currentProgress += 1
                    progressBar.progress = currentProgress

                    updateStatusMilestones(currentProgress)
                    handler.postDelayed(this, tickInterval)
                } else {
                    navigateToResults()
                }
            }
        }
        handler.post(progressRunnable)
    }

    private fun updateStatusMilestones(progress: Int) {
        // This now uses the color you just added to colors.xml
        val activeColor = ContextCompat.getColor(this, R.color.cyan_main)

        if (progress >= 30) statusFiller.setTextColor(activeColor)
        if (progress >= 60) statusTone.setTextColor(activeColor)
        if (progress >= 90) statusScore.setTextColor(activeColor)
    }

    private fun navigateToResults() {
        // Simulate a calculated score (e.g., 78)
        val calculatedScore = 78

        val intent = Intent(this, AnalysisResultsActivity::class.java)
        // Pass the score to the next activity
        intent.putExtra("EXTRA_SCORE", calculatedScore)
        startActivity(intent)

        // Finish so the user can't "back" into the loading spinner
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}