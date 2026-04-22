package com.simats.speakometer

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton

class AppPermissionsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_permissions)

        val btnAllow: AppCompatButton = findViewById(R.id.btn_allow)
        val btnDeny: TextView = findViewById(R.id.btn_deny)

        btnAllow.setOnClickListener {
            // In a real application, you would request the Android Manifest permissions here
            // using ActivityResultContracts.RequestMultiplePermissions() for CAMERA and RECORD_AUDIO
            Toast.makeText(this, "Microphone & Camera Permissions Allowed", Toast.LENGTH_SHORT).show()
            
            // Proceed to the Loading Screen
            navigateToLoading()
        }

        btnDeny.setOnClickListener {
            Toast.makeText(this, "Permissions Denied. App functionality will be limited.", Toast.LENGTH_SHORT).show()
            
            // Proceed anyway, let the app logic handle lack of permissions later
            navigateToLoading()
        }
    }

    private fun navigateToLoading() {
        val intent = Intent(this, LoadingToHomePageActivity::class.java)
        // Clear stack to prevent back-navigation into onboarding/permissions
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
