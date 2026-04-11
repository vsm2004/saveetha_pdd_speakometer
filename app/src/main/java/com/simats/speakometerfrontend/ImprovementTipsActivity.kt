package com.simats.speakometerfrontend

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton


/**
 * Slide #17: Improvement Tips Activity.
 * Provides accent-aware coaching advice and practice exercises.
 */
class ImprovementTipsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_improvement_tips)

        // 1. Modern Back Press Logic
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })

        // 2. Initialize Views
        val btnBack = findViewById<ImageView>(R.id.btn_back_to_details)
        val btnTryExercise = findViewById<AppCompatButton>(R.id.btn_try_exercise)

        // 3. Read accent and apply accent-specific tips
        val prefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val accent = prefs.getString("ACCENT_PREF", "Indian") ?: "Indian"
        applyAccentTips(accent)

        // 4. Handle Back Navigation
        btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // 5. Handle 'Try Practice Exercise' Button Click
        btnTryExercise.setOnClickListener {
            val intent = Intent(this, RecordingAudioActivity::class.java)
            intent.putExtra("IS_PRACTICE_MODE", true)
            intent.putExtra("PRACTICE_TOPIC", "Filler Words")
            startActivity(intent)
        }
    }

    /**
     * Updates text content based on user accent preference using string resources.
     */
    private fun applyAccentTips(accent: String) {
        val tvTipContent = findViewById<TextView>(R.id.tv_tip_content)
        val tvTipSubtitle = findViewById<TextView>(R.id.tv_tip_subtitle)
        val tvAccentBadge = findViewById<TextView>(R.id.tv_accent_badge)

        tvAccentBadge?.visibility = View.VISIBLE

        when (accent) {
            "American" -> {
                tvAccentBadge?.text = getString(R.string.accent_badge_american)
                tvTipSubtitle?.text = getString(R.string.tip_subtitle_american)
                tvTipContent?.text = getString(R.string.tip_content_american)
            }
            "British" -> {
                tvAccentBadge?.text = getString(R.string.accent_badge_british)
                tvTipSubtitle?.text = getString(R.string.tip_subtitle_british)
                tvTipContent?.text = getString(R.string.tip_content_british)
            }
            else -> { // Default: Indian English
                tvAccentBadge?.text = getString(R.string.accent_badge_indian)
                tvTipSubtitle?.text = getString(R.string.tip_subtitle_indian)
                tvTipContent?.text = getString(R.string.tip_content_indian)
            }
        }
    }
}