package com.simats.speakometerfrontend

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import java.util.Locale
import androidx.core.content.edit
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.simats.speakometerfrontend.network.VoiceApiClient
import com.simats.speakometerfrontend.network.SaveSessionRequest

/**
 * Slide #14: Analysis Results Dashboard.
 * Displays the final score and detected speech issues.
 */
class AnalysisResultsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_analysis_results)

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navigateToHome()
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)

        val finalScore = intent.getIntExtra("EXTRA_SCORE", 73)
        val wpm = intent.getDoubleExtra("EXTRA_WPM", 0.0)
        val tone = intent.getStringExtra("EXTRA_TONE") ?: "Neutral"
        val fillers = intent.getIntExtra("EXTRA_FILLERS", 0)
        val isPracticeMode = intent.getBooleanExtra("EXTRA_IS_PRACTICE", false)
        val practiceTopic = intent.getStringExtra("EXTRA_PRACTICE_TOPIC")

        val tvScore = findViewById<TextView>(R.id.tv_score_value)
        tvScore.text = finalScore.toString()

        // Card 1: Filler Words
        val fillerCard = findViewById<ConstraintLayout>(R.id.card_filler_words)
        val tvFillerCount = fillerCard.findViewById<TextView>(R.id.tv_instances)
        val tvFillerTag = fillerCard.findViewById<TextView>(R.id.tv_tag)

        tvFillerCount.text = getString(R.string.filler_instances_format, fillers)

        if (fillers > 3) {
            tvFillerTag.text = getString(R.string.status_high)
            tvFillerTag.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark))
        } else {
            tvFillerTag.text = getString(R.string.status_low)
            tvFillerTag.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_dark))
        }

        // Card 2: Tone / Hesitation
        val toneCard = findViewById<ConstraintLayout>(R.id.card_hesitation)
        val tvToneTitle = toneCard.findViewById<TextView>(R.id.tv_issue_title)
        val tvToneDetail = toneCard.findViewById<TextView>(R.id.tv_issue_count)
        val tvToneTag = toneCard.findViewById<TextView>(R.id.tv_severity_tag)
        tvToneTitle.text = getString(R.string.label_audio_tone)
        tvToneDetail.text = tone
        tvToneTag.text = getString(R.string.status_info)

        // Card 3: WPM / Pace
        val paceCard = findViewById<ConstraintLayout>(R.id.card_pace_variation)
        val tvPaceTitle = paceCard.findViewById<TextView>(R.id.tv_issue_title)
        val tvPaceCount = paceCard.findViewById<TextView>(R.id.tv_issue_count)
        val tvPaceTag = paceCard.findViewById<TextView>(R.id.tv_severity_tag)
        tvPaceTitle.text = getString(R.string.label_speaking_pace)
        tvPaceCount.text = String.format(Locale.getDefault(), "%.1f WPM", wpm)

        if (wpm in 120.0..160.0) {
            tvPaceTag.text = getString(R.string.status_optimal)
            tvPaceTag.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_dark))
        } else {
            tvPaceTag.text = getString(R.string.status_needs_work)
            tvPaceTag.setTextColor(ContextCompat.getColor(this, android.R.color.holo_orange_dark))
        }

        saveProgress(isPracticeMode, practiceTopic, fillers, wpm, tone, finalScore)

        val btnRecordAnother = findViewById<TextView>(R.id.btn_record_another)
        val btnViewHistory = findViewById<AppCompatButton>(R.id.btn_view_history)
        val btnGiveFeedback = findViewById<AppCompatButton>(R.id.btn_give_feedback)

        if (isPracticeMode) {
            btnRecordAnother.text = getString(R.string.btn_try_another_practice)
        }

        btnRecordAnother.setOnClickListener {
            val nextIntent = if (isPracticeMode) {
                Intent(this, PracticeHubActivity::class.java)
            } else {
                Intent(this, RecordingAudioActivity::class.java)
            }
            nextIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(nextIntent)
            finish()
        }

        btnViewHistory.setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }

        btnGiveFeedback.setOnClickListener {
            startActivity(Intent(this, FeedbackActivity::class.java))
        }
    }

    private fun saveProgress(isPractice: Boolean, topic: String?, fillers: Int, wpm: Double, tone: String, score: Int) {
        val prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val userId = prefs.getInt("USER_ID", -1)

        // Silently submit the session to our backend to empower the profile statistics Engine
        if (userId != -1) {
            val stretching = if (wpm < 110.0) "high" else "none"
            val request = SaveSessionRequest(
                userId = userId,
                score = score,
                fillersCount = fillers,
                stretchingLevel = stretching,
                confidence = score
            )
            
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    VoiceApiClient.analysisService.saveSession(request)
                } catch (e: Exception) {
                    // Ignore background network errors
                }
            }
        }

        prefs.edit {
            if (isPractice && topic != null) {
                putBoolean("COMPLETED_PRACTICE_$topic", true)
                putInt("PRACTICE_SCORE_$topic", score)
                putInt("PRACTICE_FILLER_$topic", fillers)
                // Pace saved as integer WPM
                putInt("PRACTICE_PACE_$topic", wpm.toInt())
                // Derive hesitation count from tone string (heuristic)
                val hesCount = if (tone.contains("Hesitant", ignoreCase = true) ||
                    tone.contains("Nervous", ignoreCase = true)) 1 else 0
                putInt("PRACTICE_HES_$topic", hesCount)
                Toast.makeText(this@AnalysisResultsActivity, "Practice Completed!", Toast.LENGTH_SHORT).show()
            }

            // FIX: Removed 'Array.getBoolean'. In Kotlin, inside 'edit {}',
            // you should access the outer 'prefs' object to check current values.
            val isPremium = prefs.getBoolean("IS_PREMIUM", false)

            val suggestion: String = when {
                fillers > 5 -> "Filler Words"
                wpm < 110.0 || wpm > 170.0 -> "Pace Variation"
                tone.contains("Hesitant", ignoreCase = true) ||
                        tone.contains("Nervous", ignoreCase = true) -> "Hesitation"
                else -> {
                    if (isPremium) "Art & Storytelling" else "Filler Words"
                }
            }
            putString("SUGGESTED_PRACTICE", suggestion)
        }
    }

    private fun navigateToHome() {
        val intent = Intent(this, HomePageActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}