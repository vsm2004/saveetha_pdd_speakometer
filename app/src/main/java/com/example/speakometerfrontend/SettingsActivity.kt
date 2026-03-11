package com.example.speakometerfrontend

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.edit

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // 1. Initialize UI Elements
        val btnBack = findViewById<ImageButton>(R.id.btn_back)
        val switchNotif = findViewById<SwitchCompat>(R.id.switch_notifications)
        val switchDark = findViewById<SwitchCompat>(R.id.switch_dark_mode)
        val clLogout = findViewById<ConstraintLayout>(R.id.cl_logout)
        val clPrivacy = findViewById<ConstraintLayout>(R.id.cl_privacy)

        // 2. Back Navigation
        val backAction = {
            val intent = Intent(this, ProfileAccountActivity::class.java)
            startActivity(intent)
            applyFadeTransition()
            finish()
        }

        btnBack.setOnClickListener { backAction() }
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() { backAction() }
        })

        // 3. Notification Toggle Logic
        // In a real app, this would modify a NotificationChannel or SharedPreferences
        switchNotif.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Toast.makeText(this, "Notifications Enabled", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Notifications Blocked", Toast.LENGTH_SHORT).show()
            }
        }

        // 4. Dark Mode / White Mode Logic
        // This checks current state to prevent infinite loops during recreation
        val isCurrentNight = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES
        switchDark.isChecked = isCurrentNight

        switchDark.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                Toast.makeText(this, "Dark Mode Enabled", Toast.LENGTH_SHORT).show()
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                Toast.makeText(this, "White Mode Enabled", Toast.LENGTH_SHORT).show()
            }
        }

        // 5. Privacy Policy Click
        clPrivacy.setOnClickListener {
            Toast.makeText(this, "Opening Privacy Policy...", Toast.LENGTH_SHORT).show()
        }

        // 6. Log Out Logic
        clLogout.setOnClickListener {
            // Clear User Session (Simulated)
            val sharedPrefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
            sharedPrefs.edit { clear() }

            Toast.makeText(this, "Logged Out Successfully", Toast.LENGTH_SHORT).show()

            // Navigate back to Login/Signup Activity
            // Ensure this class name matches your actual Login Activity name
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun applyFadeTransition() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            overrideActivityTransition(Activity.OVERRIDE_TRANSITION_OPEN, 0, 0)
        } else {
            @Suppress("DEPRECATION")
            overridePendingTransition(0, 0)
        }
    }
}
