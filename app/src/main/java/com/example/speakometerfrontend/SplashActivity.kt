package com.example.speakometerfrontend

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // Must be called before super.onCreate()
        installSplashScreen()

        super.onCreate(savedInstanceState)
        // We don't need to set a content view here because the splash screen
        // will use your app's theme and icon automatically.

        // This handler will still navigate to the LoginActivity after a delay.
        // It gives the user a moment to see the splash screen icon.
        Handler(Looper.getMainLooper()).postDelayed({
            // Create an Intent to start your Login Activity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)

            // Finish the SplashActivity so the user can't go back to it
            finish()
        }, 1500) // A shorter delay of 1.5 seconds is usually enough
    }
}