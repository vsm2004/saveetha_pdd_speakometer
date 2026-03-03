package com.example.speakometerfrontend

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton

/**
 * Slide #14: Analysis Results Dashboard.
 * Displays the final score and detected speech issues.
 */
class AnalysisResultsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_analysis_results)

        // Modern Back Press Logic: Redirect to Home instead of Loading screen
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navigateToHome()
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)

        // 1. Retrieve and bind the score
        val finalScore = intent.getIntExtra("EXTRA_SCORE", 73)
        findViewById<TextView>(R.id.tv_score_value).text = finalScore.toString()

        // 2. Initialize Buttons
        val btnRecordAnother = findViewById<TextView>(R.id.btn_record_another)
        val btnViewHistory = findViewById<AppCompatButton>(R.id.btn_view_history)

        btnRecordAnother.setOnClickListener {
            val intent = Intent(this, RecordingAudioActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }

        btnViewHistory.setOnClickListener {
            // Navigate to HistoryActivity (Slide #15)
            val intent = Intent(this, HistoryActivity::class.java)
            startActivity(intent)
        }
    }

    private fun navigateToHome() {
        val intent = Intent(this, HomePageActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}