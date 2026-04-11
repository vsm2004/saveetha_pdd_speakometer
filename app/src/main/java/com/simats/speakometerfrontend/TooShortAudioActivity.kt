package com.simats.speakometerfrontend

import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton

/**
 * Slide #13: Error Screen for short recordings (< 5 seconds).
 */
class TooShortAudioActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_too_short)

        val btnRetry = findViewById<AppCompatButton>(R.id.btn_retry)

        // 1. Logic for the 'Try Again' button
        btnRetry.setOnClickListener {
            val intent = Intent(this, RecordingAudioActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }

        // 2. The Modern Way to handle the back button (Fixes your errors)
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Simply close this screen and return to the previous one
                finish()
            }
        })
    }
}