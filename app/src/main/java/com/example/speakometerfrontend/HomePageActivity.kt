package com.example.speakometerfrontend

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.android.material.bottomnavigation.BottomNavigationView

/**
 * HomePageActivity: Primary dashboard for the Speak-o-Meter application (Slide #10).
 * Handles localized data binding for action cards and preserves high-fidelity
 * icon rendering in the Bottom Navigation.
 */
class HomePageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepage)

        // 1. Initialize Bottom Navigation and Fix Icon Fidelity
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        /*
           CRITICAL FIX: Set itemIconTintList to null.
           This bypasses the system's default monochromatic overlay,
           allowing the 'Speak-o-Meter' icons to render their native
           multi-color gradients and transparency.
        */
        bottomNav.itemIconTintList = null

        // 2. Localized Data Binding for Action Cards
        // This ensures tv_record_title and tv_upload_title show distinct text
        setupActionCards()

        // 3. Setup Navigation Logic
        setupBottomNavigation(bottomNav)

        // 4. Setup Profile Interaction
        findViewById<TextView>(R.id.tv_profile_initial).setOnClickListener {
            // Navigate to Profile Screen (Slide #34)
            val intent = Intent(this, ProfilePageActivity::class.java)
            startActivity(intent)
        }
    }

    /**
     * Assigns specific string resources and click listeners to the Record and Upload cards.
     */
    private fun setupActionCards() {
        val cvRecord = findViewById<CardView>(R.id.cv_record)
        val cvUpload = findViewById<CardView>(R.id.cv_upload)

        // Record Card Interaction
        cvRecord.setOnClickListener {
            Toast.makeText(this, getString(R.string.record_audio) + " clicked", Toast.LENGTH_SHORT).show()
            // Future logic: Trigger Audio Recorder
        }

        // Upload Card Interaction
        cvUpload.setOnClickListener {
            Toast.makeText(this, getString(R.string.upload_audio) + " clicked", Toast.LENGTH_SHORT).show()
            // Future logic: Trigger File Picker
        }
    }

    /**
     * Handles BottomNavigationView item selection logic.
     */
    private fun setupBottomNavigation(bottomNav: BottomNavigationView) {
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    // Already on Home
                    true
                }
                R.id.nav_history -> {
                    Toast.makeText(this, "Opening History", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_practice -> {
                    Toast.makeText(this, "Opening Practice Mode", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_profile -> {
                    // Navigate to Profile Screen
                    startActivity(Intent(this, ProfilePageActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }
}