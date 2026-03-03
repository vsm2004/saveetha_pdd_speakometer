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

        // 2. Setup Action Cards with localized string binding
        setupActionCards()

        // 3. Setup Bottom Navigation Listener
        setupBottomNavigation(bottomNav)

        // 4. Profile Interaction
        findViewById<TextView>(R.id.tv_profile_initial).setOnClickListener {
            // Future: Navigate to Profile Screen
            // val intent = Intent(this, ProfilePageActivity::class.java)
            // startActivity(intent)
        }
    }

    private fun setupActionCards() {
        val cvRecord = findViewById<CardView>(R.id.cv_record)
        val cvUpload = findViewById<CardView>(R.id.cv_upload)

        // NAVIGATION TO RECORDING SCREEN (Slide #11)
        cvRecord.setOnClickListener {
            val intent = Intent(this, RecordingAudioActivity::class.java)
            startActivity(intent)
        }

        // NAVIGATION TO UPLOAD SCREEN (Slide #XX)
        // Replaced the Toast with an Intent to open UploadingAudioActivity
        cvUpload.setOnClickListener {
            val intent = Intent(this, UploadingAudioActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupBottomNavigation(bottomNav: BottomNavigationView) {
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
                R.id.nav_history -> {
                    Toast.makeText(this, "History", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_practice -> {
                    Toast.makeText(this, "Practice", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_profile -> {
                    Toast.makeText(this, "Profile", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }
    }
}