package com.example.speakometerfrontend

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton

class PermissionsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permissions)

        val allowButton: AppCompatButton = findViewById(R.id.btn_allow)
        val denyButton: TextView = findViewById(R.id.btn_deny)

        allowButton.setOnClickListener {
            Toast.makeText(this, "Permissions Granted. Setting up your account...", Toast.LENGTH_SHORT).show()
            // NAVIGATE TO FINAL SETUP SCREEN
            navigateToFinalSetup()
        }

        denyButton.setOnClickListener {
            Toast.makeText(this, "Permissions Denied. Setting up your account...", Toast.LENGTH_SHORT).show()
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

    override fun onBackPressed() {
        // Block the back button to ensure a choice is made.
        Toast.makeText(this, "Please choose an option to continue", Toast.LENGTH_SHORT).show()
    }
}
