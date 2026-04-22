package com.simats.speakometer

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton

class FreeTrialActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_premium_freetrial)

        val btnBack = findViewById<ImageButton>(R.id.btn_back)
        val btnStartTrial = findViewById<AppCompatButton>(R.id.btn_start_trial)

        // 1. Back Navigation Logic
        val navigateBack = {
            finish()
            applyFadeTransition()
        }

        btnBack.setOnClickListener { navigateBack() }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navigateBack()
            }
        })

        // 2. Start Trial Button Logic
        // This is where we pass the dynamic data to the PremiumPaymentActivity
        btnStartTrial.setOnClickListener {
            val intent = Intent(this, PremiumPaymentActivity::class.java)

            // We pass 'true' for IS_FREE_TRIAL so the payment screen shows ₹0
            intent.putExtra("IS_FREE_TRIAL", true)

            // We set the default plan for the trial (usually Yearly as it's the best value)
            intent.putExtra("PLAN_TYPE", "Yearly")
            intent.putExtra("PLAN_PRICE", "1199")

            startActivity(intent)
            applyFadeTransition()
        }
    }

    /**
     * Ensures smooth modern transitions across Android versions
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