package com.example.speakometerfrontend // Make sure this matches your package name

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // This links the Kotlin file to your XML layout file.
        setContentView(R.layout.activity_login_signup)

        // Find the views from your XML layout by their IDs
        val emailEditText: EditText = findViewById(R.id.et_email)
        val passwordEditText: EditText = findViewById(R.id.et_password)
        val usernameEditText: EditText = findViewById(R.id.et_username)
        val submitButton: Button = findViewById(R.id.btn_submit)
        val signInLink: TextView = findViewById(R.id.tv_signin_link)

        // Set a click listener for the "Submit" button
        submitButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            val username = usernameEditText.text.toString()

            // For now, we'll just show a Toast message with the input.
            // Later, you will add your signup/login logic here.
            if (email.isNotEmpty() && password.isNotEmpty() && username.isNotEmpty()) {
                val message = "Email: $email\nUsername: $username"
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()

                // TODO: Add your user registration/authentication logic here (e.g., with Firebase)

            } else {
                Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show()
            }
        }
        
        // Set a click listener for the "Sign In" link
        signInLink.setOnClickListener {
            // Create an Intent to start your SigningInActivity
            val intent = Intent(this, SigningInActivity::class.java)

            // Execute the intent to open the new activity
            startActivity(intent)
        }

    }
}
