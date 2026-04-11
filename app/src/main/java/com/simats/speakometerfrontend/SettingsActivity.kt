package com.simats.speakometerfrontend

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

        // 2. Back Navigation Logic
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
        switchNotif.setOnCheckedChangeListener { _, isChecked ->
            val messageId = if (isChecked) R.string.notif_enabled else R.string.notif_blocked
            Toast.makeText(this, getString(messageId), Toast.LENGTH_SHORT).show()
        }

        // 4. Dark Mode / White Mode Logic
        val isCurrentNight = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES
        switchDark.isChecked = isCurrentNight

        switchDark.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                Toast.makeText(this, getString(R.string.dark_mode_enabled), Toast.LENGTH_SHORT).show()
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                Toast.makeText(this, getString(R.string.white_mode_enabled), Toast.LENGTH_SHORT).show()
            }
        }

        // 5. Privacy Policy Click
        clPrivacy.setOnClickListener {
            Toast.makeText(this, getString(R.string.opening_privacy), Toast.LENGTH_SHORT).show()
        }

        // 6. Log Out Logic
        clLogout.setOnClickListener {
            val sharedPrefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
            sharedPrefs.edit {
                putBoolean("IS_LOGGED_IN", false)
                remove("USER_ID")
                remove("USER_NAME")
                remove("USER_EMAIL")
                remove("IS_PREMIUM")
                remove("PREMIUM_PLAN")
                remove("PREMIUM_EXPIRY")
            }

            Toast.makeText(this, getString(R.string.logout_success), Toast.LENGTH_SHORT).show()

            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    /**
     * Fixes the "Unresolved reference: applyFadeTransition" error.
     * Handles activity transitions for modern Android versions (14+) and older ones.
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