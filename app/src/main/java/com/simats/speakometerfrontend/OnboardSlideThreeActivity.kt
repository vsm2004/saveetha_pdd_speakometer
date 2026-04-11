package com.simats.speakometerfrontend

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton

class OnboardSlideThreeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboard_three)

        // Find the "Get Started" button
        val getStartedButton: AppCompatButton = findViewById(R.id.btn_get_started)

        getStartedButton.setOnClickListener {
            // Launch the App Permissions Screen before Home
            val intent = Intent(this, AppPermissionsActivity::class.java)
            // Clear task so user can't back-button into onboarding
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}
