package com.example.speakometerfrontend

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.semantics.text
import java.util.Locale

class RecordingAudioActivity : AppCompatActivity() {

    private lateinit var tvTimer: TextView
    private lateinit var tvSubtitle: TextView
    private lateinit var tvTitle: TextView
    private var isRecording = false
    private var secondsElapsed = 0
    private val handler = Handler(Looper.getMainLooper())

    private val timerRunnable = object : Runnable {
        override fun run() {
            secondsElapsed++
            val minutes = secondsElapsed / 60
            val seconds = secondsElapsed % 60
            tvTimer.text = String.format(Locale.getDefault(), "%d:%02d", minutes, seconds)
            handler.postDelayed(this, 1000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recording_audio)

        tvTitle = findViewById(R.id.tv_record_title)
        tvTimer = findViewById(R.id.tv_timer)
        tvSubtitle = findViewById(R.id.tv_record_subtitle)
        val btnBack = findViewById<ImageButton>(R.id.btn_back)
        val micBg = findViewById<View>(R.id.view_mic_bg)

        // PERSONALIZATION LOGIC: Check if we came from the "Tips" screen
        val isPracticeMode = intent.getBooleanExtra("IS_PRACTICE_MODE", false)
        if (isPracticeMode) {
            val topic = intent.getStringExtra("PRACTICE_TOPIC") ?: "Exercise"
            tvTitle.text = "Practice: $topic"
            tvSubtitle.text = "Tap to start exercise"
        }

        btnBack.setOnClickListener { finish() }

        micBg.setOnClickListener {
            if (!isRecording) {
                isRecording = true
                secondsElapsed = 0
                tvSubtitle.text = getString(R.string.recording_in_progress)
                handler.post(timerRunnable)
            } else {
                stopAndCheckDuration()
            }
        }
    }

    private fun stopAndCheckDuration() {
        isRecording = false
        handler.removeCallbacks(timerRunnable)

        if (secondsElapsed < 5) {
            // Logic for Slide #13: Too Short
            val intent = Intent(this, TooShortAudioActivity::class.java)
            startActivity(intent)
        } else {
            // Logic for Slide #12: Proceed to Analysis
            val intent = Intent(this, RecordedAudioAnalysisActivity::class.java)
            startActivity(intent)
        }
        finish() // Ensure the recorder resets for the next use
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(timerRunnable)
    }
}