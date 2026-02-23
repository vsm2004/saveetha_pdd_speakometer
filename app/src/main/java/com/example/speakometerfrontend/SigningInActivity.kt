package com.example.speakometerfrontend // Make sure this matches your package name

import android.os.Bundle
import android.content.Intent
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class SigningInActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // This links the Kotlin file to your activity_signin.xml layout.
        setContentView(R.layout.activity_signin)

        // --- Find all the views from your XML layout by their IDs ---

        // Email and Password fields
        val emailEditText: EditText = findViewById(R.id.et_login_email)
        val passwordEditText: EditText = findViewById(R.id.et_login_password)

        // Buttons
        val submitButton: MaterialButton = findViewById(R.id.btn_submit)
        val googleButton: MaterialButton = findViewById(R.id.btn_google)
        val facebookButton: MaterialButton = findViewById(R.id.btn_facebook)


        // --- Set up click listeners for all the buttons ---

        // 1. Email/Password Submit Button Listener
        // In SigningInActivity.kt

        submitButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {

                // TODO: Add your Firebase email/password authentication logic here.
                // For now, we will simulate a successful login.
                val isLoginSuccessful = true // Replace with actual login result

                if (isLoginSuccessful) {
                    // Login was successful, start the loading activity!
                    val intent = Intent(this, LoadingToHomePageActivity::class.java)
                    startActivity(intent)

                    // Finish the SigningInActivity so the user can't come back to it
                    finish()
                } else {
                    // Handle failed login
                    Toast.makeText(this, "Login Failed. Please try again.", Toast.LENGTH_SHORT).show()
                }

            } else {
                Toast.makeText(this, "Please enter both email and password.", Toast.LENGTH_SHORT).show()
            }
        }


        // 2. Google Sign-In Button Listener
        googleButton.setOnClickListener {
            // This is a placeholder. You will replace this with Google Sign-In SDK logic.
            Toast.makeText(this, "Google Sign-In clicked!", Toast.LENGTH_SHORT).show()

            // TODO: Start the Google Sign-In flow here.
        }

        // 3. Facebook Sign-In Button Listener
        facebookButton.setOnClickListener {
            // This is a placeholder. You will replace this with Facebook SDK logic.
            Toast.makeText(this, "Facebook Sign-In clicked!", Toast.LENGTH_SHORT).show()

            // TODO: Start the Facebook Login flow here.
        }

        // Note: I removed the "Create Account" link listener because it's not in this XML layout.
        // The user journey is now one-way from the sign-up page to the sign-in page.
        // To go back, the user will press the system's back button.
    }
}
