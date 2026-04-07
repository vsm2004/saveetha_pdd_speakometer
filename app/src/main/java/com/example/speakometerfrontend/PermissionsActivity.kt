package com.example.speakometerfrontend

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton

class PermissionsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permissions)

        // 1. Setup Modern Back Press Logic
        // This replaces the deprecated onBackPressed()
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Block the back button to ensure a choice is made.
                Toast.makeText(
                    this@PermissionsActivity,
                    getString(R.string.back_block_msg),
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

        val allowButton: AppCompatButton = findViewById(R.id.btn_allow)
        val denyButton: TextView = findViewById(R.id.btn_deny)

        allowButton.setOnClickListener {
            Toast.makeText(
                this,
                getString(R.string.permissions_granted_msg),
                Toast.LENGTH_SHORT
            ).show()
            // NAVIGATE TO FINAL SETUP SCREEN
            navigateToFinalSetup()
        }

        denyButton.setOnClickListener {
            Toast.makeText(
                this,
                getString(R.string.permissions_denied_msg),
                Toast.LENGTH_SHORT
            ).show()
            // NAVIGATE TO FINAL SETUP SCREEN
            navigateToFinalSetup()
        }
    }

    /**
     * Navigates the user to the FinalSetupActivity.
     */
    private fun navigateToFinalSetup() {
        // Create an Intent to start your FinalSetupActivity
        val intent = Intent(this, FinalSetupActivity::class.java)
        startActivity(intent)
        // Finish the PermissionsActivity so the user can't go back to it.
        finish()
    }
}