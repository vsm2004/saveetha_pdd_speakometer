package com.example.speakometerfrontend


import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton

/**
 * Slide #17: Improvement Tips Activity.
 * Provides detailed advice and practice exercises.
 */
class ImprovementTipsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_improvement_tips)

        // Modern Back Press Logic: Fixes the deprecation error
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)

        // 1. Initialize Views
        val btnBack = findViewById<ImageView>(R.id.btn_back_to_details)
        val btnTryExercise = findViewById<AppCompatButton>(R.id.btn_try_exercise)

        // 2. Handle Back Navigation (Top Arrow)
        btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // 3. Handle 'Try Practice Exercise' Button Click
        // Inside ImprovementTipsActivity.kt
        // Inside ImprovementTipsActivity.kt

        btnTryExercise.setOnClickListener {
            val intent = Intent(this, RecordingAudioActivity::class.java)

            // Pass a flag so the recorder knows this is for practice
            intent.putExtra("IS_PRACTICE_MODE", true)
            intent.putExtra("PRACTICE_TOPIC", "Filler Words")

            startActivity(intent)
        }
    }
}