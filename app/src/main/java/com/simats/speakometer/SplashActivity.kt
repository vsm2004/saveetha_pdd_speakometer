package com.simats.speakometer

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    // Total duration for the splash screen
    private val SPLASH_DELAY: Long = 3000 // 3 seconds

    // Animation timing
    private val ANIMATION_INTERVAL: Long = 400 // Time for each dot to glow
    private lateinit var dot1: View
    private lateinit var dot2: View
    private lateinit var dot3: View
    private lateinit var dots: List<View>

    private val animationHandler = Handler(Looper.getMainLooper())
    private var dotIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.introduction_page_of_speakometer)

        // Find the dot views by their new IDs
        dot1 = findViewById(R.id.dot1)
        dot2 = findViewById(R.id.dot2)
        dot3 = findViewById(R.id.dot3)
        dots = listOf(dot1, dot2, dot3)

        // Start the dot animation sequence
        startDotAnimation()

        // Schedule the navigation to the next screen after the total delay
        animationHandler.postDelayed({
            navigateToNextScreen()
        }, SPLASH_DELAY)
    }

    private val dotAnimationRunnable = object : Runnable {
        override fun run() {
            // Reset all dots to plain
            dots.forEach { it.background = ContextCompat.getDrawable(this@SplashActivity, R.drawable.cyan_dot_plain) }

            // Set the current dot to glow
            if (dotIndex < dots.size) {
                dots[dotIndex].background = ContextCompat.getDrawable(this@SplashActivity, R.drawable.cyan_dot_glow)
            }

            // Move to the next dot for the next cycle
            dotIndex = (dotIndex + 1) % dots.size

            // Schedule the next animation frame
            animationHandler.postDelayed(this, ANIMATION_INTERVAL)
        }
    }

    private fun startDotAnimation() {
        // Start the animation immediately
        animationHandler.post(dotAnimationRunnable)
    }

    private fun navigateToNextScreen() {
        // Check if user is already logged in
        val prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val isLoggedIn = prefs.getBoolean("IS_LOGGED_IN", false)

        val intent = if (isLoggedIn) {
            // Skip login, go directly to home
            Intent(this, HomePageActivity::class.java)
        } else {
            // First time or logged out, show login
            Intent(this, LoginActivity::class.java)
        }
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Important: Remove callbacks to prevent memory leaks if the user
        // leaves the screen before the delay is over.
        animationHandler.removeCallbacksAndMessages(null)
    }
}
