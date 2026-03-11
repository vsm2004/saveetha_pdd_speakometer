package com.example.speakometerfrontend

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.bottomnavigation.BottomNavigationView

class ProfileAccountActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_account)

        // 1. Setup Modern Back Press Dispatcher
        // This fixes the "onBackPressed is no longer called" error
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(this@ProfileAccountActivity, HomePageActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)

                // Apply transition for the back navigation
                applyFadeTransition()
                finish()
            }
        })

        // 2. Setup Bottom Navigation
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        // High-Fidelity 2026 Fixes
        bottomNav.itemIconTintList = null
        bottomNav.isItemActiveIndicatorEnabled = false
        bottomNav.selectedItemId = R.id.nav_profile

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, HomePageActivity::class.java))
                    applyFadeTransition()
                    finish()
                    true
                }
                R.id.nav_practice -> {
                    startActivity(Intent(this, PracticeHubActivity::class.java))
                    applyFadeTransition()
                    finish()
                    true
                }
                R.id.nav_profile -> true
                else -> false
            }
        }

        // 3. Click Listeners for Profile Actions
        findViewById<TextView>(R.id.tv_edit_profile).setOnClickListener {
            Toast.makeText(this, "Opening Profile Editor...", Toast.LENGTH_SHORT).show()
        }

        // Fix for Stats Card Interaction
        findViewById<TextView>(R.id.tv_title_stats)?.let { titleView ->
            (titleView.parent as? ConstraintLayout)?.setOnClickListener {
                Toast.makeText(this, "Loading Detailed Analytics...", Toast.LENGTH_SHORT).show()
            }
        }

        // See Premium Plans Button
        findViewById<TextView>(R.id.btn_see_plans).setOnClickListener {
            Toast.makeText(this, "Redirecting to Premium Subscription...", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Fixes the deprecation for overridePendingTransition.
     * Uses the modern API for Android 14+ and falls back for older versions.
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