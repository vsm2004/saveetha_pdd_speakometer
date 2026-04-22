package com.simats.speakometer

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton

class OfflineReminderActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_offline_remainder)

        val btnGotIt = findViewById<AppCompatButton>(R.id.btnGotIt)

        btnGotIt.setOnClickListener {
            // Re-check the internet signal using our Utility
            if (NetworkUtils.isInternetAvailable(this)) {
                // User is back online! Return to Home and clear this screen
                val intent = Intent(this, HomePageActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            } else {
                // Still offline - show a warning and stay on this screen
                Toast.makeText(
                    this,
                    "Still no internet connection. Please check your Wi-Fi or Data.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}