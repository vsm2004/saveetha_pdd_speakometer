package com.example.speakometerfrontend

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class PremiumViewAchievementsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_premium_view_achievements)

        val btnAwesome = findViewById<TextView>(R.id.btnAwesome)

        btnAwesome.setOnClickListener {
            showNotificationConsentDialog()
        }
    }

    private fun showNotificationConsentDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Enable Notifications")
        builder.setMessage("Would you like to receive daily updates on celebration streaks, new challenges, and motivational tips?")

        // Inside PremiumViewAchievementsActivity.kt, within the OK button of the dialog:
        builder.setPositiveButton("OK") { _, _ ->
            Toast.makeText(this, "Notifications enabled!", Toast.LENGTH_SHORT).show()

            // Move to Feedback Screen before finishing the session
            val intent = Intent(this, FeedbackActivity::class.java)
            startActivity(intent)
            finish()
        }

        builder.setNegativeButton("Maybe Later") { dialog, _ ->
            dialog.dismiss()
            finish()
        }

        val dialog = builder.create()
        dialog.show()
    }
}