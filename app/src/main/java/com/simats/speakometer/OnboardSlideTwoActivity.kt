package com.simats.speakometer

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton

class OnboardSlideTwoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Link this Kotlin file to your activity_onboard_two.xml layout
        setContentView(R.layout.activity_onboard_two)

        // --- Find the interactive views from the layout ---
        val nextButton: AppCompatButton = findViewById(R.id.btn_next)
        val skipButton: TextView = findViewById(R.id.tv_skip)

        // --- Set up click listeners ---

        // 1. 'Next' button listener
        nextButton.setOnClickListener {
            // Navigate to the third and final onboarding screen
            val intent = Intent(this, OnboardSlideThreeActivity::class.java)
            startActivity(intent)
        }

        // 2. 'Skip' button listener
        skipButton.setOnClickListener {
            // The user wants to skip the rest of the onboarding process.
            Toast.makeText(this, "Skipping to Home", Toast.LENGTH_SHORT).show()
            navigateToHome()
        }
    }

    /**
     * Navigates the user directly to the main HomePageActivity
     * and clears the back stack to prevent them from returning to onboarding.
     */
    private fun navigateToHome() {
        val intent = Intent(this, HomePageActivity::class.java)
        // These flags ensure that the entire task stack (including Onboard 1) is cleared.
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish() // Close this onboarding activity
    }

    // It is not necessary to override the back button here. By default,
    // pressing back will correctly navigate the user to OnboardSlideOneActivity,
    // which is the expected behavior.
}
