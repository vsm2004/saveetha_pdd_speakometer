package com.simats.speakometerfrontend

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton

class PremiumScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_premium_screen)

        // 1. Initialize UI Elements
        val btnBack = findViewById<ImageButton>(R.id.btn_back)
        val btnSeePlans = findViewById<AppCompatButton>(R.id.btn_see_plans)
        val btnMaybeLater = findViewById<AppCompatButton>(R.id.btn_maybe_later)

        val navigateBack = {
            applyFadeTransition()
            finish()
        }

        // Handle Back Arrow Click
        btnBack.setOnClickListener { navigateBack() }

        // Handle "Maybe Later" Click
        btnMaybeLater.setOnClickListener { navigateBack() }

        // Handle System Back Gesture/Button (Fixes Deprecation)
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navigateBack()
            }
        })

        // 3. See Plans Logic
        btnSeePlans.setOnClickListener {
            val intent = Intent(this, PremiumPackSelectionActivity::class.java)
            startActivity(intent)
        }
    }

    /**
     * Fixes the deprecation for overridePendingTransition.
     * Uses the modern API for Android 14+ (UPSIDE_DOWN_CAKE) and falls back for older versions.
     */
    private fun applyFadeTransition() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            overrideActivityTransition(
                Activity.OVERRIDE_TRANSITION_OPEN,
                0, 0
            )
        } else {
            @Suppress("DEPRECATION")
            overridePendingTransition(0, 0)
        }
    }
}