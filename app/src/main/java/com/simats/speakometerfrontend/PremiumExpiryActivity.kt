package com.simats.speakometerfrontend

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton

class PremiumExpiryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_premium_expiry_msg)

        val btnRenew = findViewById<AppCompatButton>(R.id.btnRenew)
        val btnMaybeLater = findViewById<TextView>(R.id.btnMaybeLater)

        // Renew takes user back to the pricing selection screen
        btnRenew.setOnClickListener {
            val intent = Intent(this, PremiumPackSelectionActivity::class.java)
            startActivity(intent)
        }

        // Maybe Later takes user to the main Home Page
        btnMaybeLater.setOnClickListener {
            navigateToHome()
        }

        // Setup Bottom Navigation Clicks (Mocks)
        setupBottomNav()
    }

    private fun setupBottomNav() {
        val bottomNav = findViewById<android.widget.LinearLayout>(R.id.bottomNavContainer)

        // Home Nav Item (Index 0)
        bottomNav.getChildAt(0).setOnClickListener { navigateToHome() }

        // Practice Nav Item (Index 2)
        bottomNav.getChildAt(2).setOnClickListener {
            val intent = Intent(this, PracticeHubActivity::class.java)
            startActivity(intent)
        }
    }

    private fun navigateToHome() {
        val intent = Intent(this, HomePageActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}