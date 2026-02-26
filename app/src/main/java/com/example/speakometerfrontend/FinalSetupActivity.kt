package com.example.speakometerfrontend

import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class FinalSetupActivity : AppCompatActivity() {

    // Constant for the 3-second simulation delay
    private val setupDelay: Long = 3000

    private lateinit var dot1: View
    private lateinit var dot2: View
    private lateinit var dot3: View
    private lateinit var dots: List<View>

    private var dotAnimator: ValueAnimator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_final_setup)

        // Initialize dot views
        dot1 = findViewById(R.id.dot1_f)
        dot2 = findViewById(R.id.dot2_f)
        dot3 = findViewById(R.id.dot3_f)
        dots = listOf(dot1, dot2, dot3)

        // Start the 1-2-3 dot glow animation
        startDotAnimation()

        // FIX: The function is now called inside the Handler block
        Handler(Looper.getMainLooper()).postDelayed({
            navigateToOnboarding()
        }, setupDelay)
    }

    private fun startDotAnimation() {
        dotAnimator = ValueAnimator.ofInt(0, 2).apply {
            duration = 1200
            repeatCount = ValueAnimator.INFINITE
            interpolator = LinearInterpolator()

            addUpdateListener { animation ->
                val glowingIndex = animation.animatedValue as Int
                dots.forEachIndexed { index, dot ->
                    if (index == glowingIndex) {
                        dot.background = ContextCompat.getDrawable(
                            this@FinalSetupActivity,
                            R.drawable.cyan_dot_glow
                        )
                    } else {
                        dot.background = ContextCompat.getDrawable(
                            this@FinalSetupActivity,
                            R.drawable.cyan_dot_plain
                        )
                    }
                }
            }
        }
        dotAnimator?.start()
    }

    /**
     * Navigates to the first onboarding screen and clears the stack.
     */
    private fun navigateToOnboarding() {
        val intent = Intent(this, OnboardSlideOneActivity::class.java)
        // Ensure user can't back-button into the setup or login screens
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Clean up animator to prevent memory leaks
        dotAnimator?.cancel()
    }
}