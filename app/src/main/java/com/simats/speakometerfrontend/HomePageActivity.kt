package com.simats.speakometerfrontend

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.lifecycle.lifecycleScope
import com.simats.speakometerfrontend.network.VoiceApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.core.content.edit

class HomePageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepage)

        // 1. Initialize Bottom Navigation and Fix Icon Fidelity
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.itemIconTintList = null // Allows original multi-color PNGs to show
        bottomNav.selectedItemId = R.id.nav_home

        // 2. Setup Action Cards (Recording and Upload)
        setupActionCards()

        // 3. Setup Bottom Navigation Listener
        setupBottomNavigation(bottomNav)

        // 4. Profile Interaction
        findViewById<TextView>(R.id.tv_profile_initial).setOnClickListener {
            checkNetworkAndNavigate(Intent(this, ProfileAccountActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        updateGreetingAndProfile()
        syncUserData() // NEW: Background sync check
        // Ensure Home is highlighted
        findViewById<BottomNavigationView>(R.id.bottom_navigation).selectedItemId = R.id.nav_home
    }

    private fun syncUserData() {
        val prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val userId = prefs.getInt("USER_ID", -1)
        
        if (userId != -1) {
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val response = VoiceApiClient.analysisService.getProfile(userId)
                    if (response.isSuccessful) {
                        val user = response.body()?.user
                        withContext(Dispatchers.Main) {
                            user?.let {
                                prefs.edit {
                                    putString("USER_NAME", it.name ?: "User")
                                    putString("USER_EMAIL", it.email)
                                    putBoolean("IS_PREMIUM", it.premiumStatus)
                                    putString("PREMIUM_EXPIRY", it.premiumExpiry)
                                }
                                // Update UI if needed (greeting depends on name)
                                updateGreetingAndProfile()
                            }
                        }
                    }
                } catch (e: Exception) {
                    // Fail silently in background sync
                }
            }
        }
    }

    private fun updateGreetingAndProfile() {
        val prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val userName = prefs.getString("USER_NAME", "User") ?: "User"
        
        // 1. Update Profile Initial (Synchronize with ProfileEdit changes)
        val tvProfileInitial = findViewById<TextView>(R.id.tv_profile_initial)
        if (userName.isNotEmpty()) {
            tvProfileInitial.text = userName.first().uppercase()
        }

        // 2. Dynamic Greeting based on Time
        val tvGreeting = findViewById<TextView>(R.id.tv_greeting)
        val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        
        val greetingPrefix = when {
            hour in 5..11 -> "Good morning"
            hour in 12..16 -> "Good afternoon"
            hour in 17..20 -> "Good evening"
            else -> "Good night"
        }

        val emoji = if (hour in 21..23 || hour in 0..4) " 🌃" else " 👋"
        
        // Extract first name for a friendlier feel
        val firstName = userName.split(" ").firstOrNull() ?: userName
        
        // Final result: "Good afternoon, Manu Sai! 👋"
        tvGreeting.text = "$greetingPrefix, $firstName!$emoji"
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
        bottomNav.selectedItemId = R.id.nav_home
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
                R.id.nav_history -> {
                    val intent = Intent(this@HomePageActivity, HistoryActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                    checkNetworkAndNavigate(intent)
                    true
                }
                R.id.nav_practice -> {
                    val intent = Intent(this@HomePageActivity, PracticeHubActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                    checkNetworkAndNavigate(intent)
                    true
                }
                R.id.nav_profile -> {
                    val intent = Intent(this@HomePageActivity, ProfileAccountActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                    checkNetworkAndNavigate(intent)
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