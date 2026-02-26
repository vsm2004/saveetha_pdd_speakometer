package com.example.speakometerfrontend

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton

class OnboardSlideOneActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboard_one)

        // --- View Initialization ---
        val nextButton: AppCompatButton = findViewById(R.id.btn_next)
        val skipButton: TextView = findViewById(R.id.tv_skip)

        // --- Handle Back Button (AndroidX Way) ---
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Blocks back gesture to prevent returning to loading screen
                Toast.makeText(this@OnboardSlideOneActivity, "Please use 'Next' or 'Skip'", Toast.LENGTH_SHORT).show()
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)

        // --- Set up click listeners ---

        nextButton.setOnClickListener {
            // Once we create Slide Two, we will link it here
            Toast.makeText(this, "Navigating to Onboarding Slide 2", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, OnboardSlideTwoActivity::class.java)
            startActivity(intent)

            // val intent = Intent(this, OnboardSlideTwoActivity::class.java)
            // startActivity(intent)
        }

        skipButton.setOnClickListener {
            Toast.makeText(this, "Skipping to Home", Toast.LENGTH_SHORT).show()
            navigateToHome()
        }
    }

    private fun navigateToHome() {
        val intent = Intent(this, HomePageActivity::class.java)
        // Clears the stack so user enters the app fresh
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()

    }
}