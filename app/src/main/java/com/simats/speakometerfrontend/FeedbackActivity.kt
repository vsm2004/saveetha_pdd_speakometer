package com.simats.speakometerfrontend

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton

class FeedbackActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feedback)

        val ivBackArrow = findViewById<ImageView>(R.id.ivBackArrow)
        val btnSubmit = findViewById<AppCompatButton>(R.id.btnSubmitFeedback)
        val btnCancel = findViewById<TextView>(R.id.btnCancel)
        val etInput = findViewById<EditText>(R.id.etFeedbackInput)

        // 1. Submit Logic
        btnSubmit.setOnClickListener {
            val feedbackText = etInput.text.toString().trim()

            if (feedbackText.isNotEmpty()) {
                // In a real app, you'd send this string to your server/Firebase
                Toast.makeText(this, "Thank you! Your feedback helps us improve.", Toast.LENGTH_LONG).show()
                navigateToHome()
            } else {
                Toast.makeText(this, "Please enter some feedback first", Toast.LENGTH_SHORT).show()
            }
        }

        // 2. Cancel/Back Logic (Returns to Home without saving)
        btnCancel.setOnClickListener { navigateToHome() }
        ivBackArrow.setOnClickListener { navigateToHome() }
    }

    private fun navigateToHome() {
        val intent = Intent(this, HomePageActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}