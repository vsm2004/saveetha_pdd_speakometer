package com.example.speakometerfrontend

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomePageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepage)

        // 1. Initialize Bottom Navigation and Fix Icon Fidelity
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.itemIconTintList = null // Allows original multi-color PNGs to show

        // 2. Setup Action Cards (Recording and Upload)
        setupActionCards()

        // 3. Setup Bottom Navigation Listener
        setupBottomNavigation(bottomNav)

        // 4. Profile Interaction
        findViewById<TextView>(R.id.tv_profile_initial).setOnClickListener {
            //checkNetworkAndNavigate(Intent(this, ProfilePageActivity::class.java))
        }
    }

    private fun setupActionCards() {
        val cvRecord = findViewById<CardView>(R.id.cv_record)
        val cvUpload = findViewById<CardView>(R.id.cv_upload)

        // NAVIGATION TO RECORDING SCREEN
        cvRecord.setOnClickListener {
            val intent = Intent(this, RecordingAudioActivity::class.java)
            checkNetworkAndNavigate(intent)
        }

        // NAVIGATION TO UPLOAD SCREEN
        cvUpload.setOnClickListener {
            val intent = Intent(this, UploadingAudioActivity::class.java)
            checkNetworkAndNavigate(intent)
        }
    }

    private fun setupBottomNavigation(bottomNav: BottomNavigationView) {
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
                R.id.nav_history -> {
                    checkNetworkAndNavigate(null, "History")
                    true
                }
                R.id.nav_practice -> {
                    // Navigate to PracticeHubActivity with network check
                    val intent = Intent(this, PracticeHubActivity::class.java)
                    checkNetworkAndNavigate(intent)
                    true
                }
                R.id.nav_profile -> {
                    checkNetworkAndNavigate(null, "Profile")
                    true
                }
                else -> false
            }
        }
    }

    /**
     * Helper function to centralize Network Checking logic.
     * If online: starts the provided intent.
     * If offline: redirects to OfflineReminderActivity.
     */
    private fun checkNetworkAndNavigate(targetIntent: Intent?, debugToast: String? = null) {
        if (NetworkUtils.isInternetAvailable(this)) {
            if (targetIntent != null) {
                startActivity(targetIntent)
            } else if (debugToast != null) {
                Toast.makeText(this, debugToast, Toast.LENGTH_SHORT).show()
            }
        } else {
            // REDIRECT TO OFFLINE SCREEN
            val offlineIntent = Intent(this, OfflineReminderActivity::class.java)
            startActivity(offlineIntent)
        }
    }
}