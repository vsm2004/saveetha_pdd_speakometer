package com.example.speakometerfrontend

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton

/**
 * Slide #18: Personalized Practice Exercise.
 * Dynamically adjusts content based on the user's most recurring mistake.
 */
class PracticeExerciseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_practice_exercise)

        // 1. Initialize Views
        val btnBack = findViewById<ImageView>(R.id.btn_back_to_tips)
        val tvTitle = findViewById<TextView>(R.id.tv_exercise_title)
        val tvMeta = findViewById<TextView>(R.id.tv_exercise_meta)
        val tvPromptContent = findViewById<TextView>(R.id.tv_prompt_content)
        val tvTipText = findViewById<TextView>(R.id.tv_tip_text)
        val btnStart = findViewById<AppCompatButton>(R.id.btn_start_recording)

        // 2. Logic: Personalize based on Intent Data
        val mistakeType = intent.getStringExtra("MISTAKE_TYPE") ?: "Filler Words"

        setupExerciseContent(mistakeType, tvTitle, tvMeta, tvPromptContent, tvTipText)

        // 3. Navigation
        btnBack.setOnClickListener {
            finish()
        }

        // 4. Start Recording Logic
        btnStart.setOnClickListener {
            val intent = Intent(this, RecordingAudioActivity::class.java)
            intent.putExtra("IS_PRACTICE_MODE", true)
            intent.putExtra("PRACTICE_TOPIC", mistakeType)
            startActivity(intent)

            // Using a string resource with a placeholder for the Toast
            val message = getString(R.string.toast_starting_practice, mistakeType)
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Sets the text content based on the identified speech mistake using string resources.
     */
    private fun setupExerciseContent(
        type: String,
        title: TextView,
        meta: TextView,
        prompt: TextView,
        tip: TextView
    ) {
        when (type) {
            "Filler Words" -> {
                title.text = getString(R.string.reduce_filler_words)
                meta.text = getString(R.string.focus_filler)
                prompt.text = getString(R.string.prompt_filler)
                tip.text = getString(R.string.tip_filler)
            }
            "Pace Variation" -> {
                title.text = getString(R.string.title_pace)
                meta.text = getString(R.string.focus_pace)
                prompt.text = getString(R.string.prompt_pace)
                tip.text = getString(R.string.tip_pace)
            }
            "Hesitation" -> {
                title.text = getString(R.string.title_hesitation)
                meta.text = getString(R.string.focus_hesitation)
                prompt.text = getString(R.string.prompt_hesitation)
                tip.text = getString(R.string.tip_hesitation)
            }
        }
    }
}