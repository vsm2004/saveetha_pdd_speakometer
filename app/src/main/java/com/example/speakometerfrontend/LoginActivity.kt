package com.example.speakometerfrontend

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.graphics.toColorInt // Essential KTX import

class LoginActivity : AppCompatActivity() {

    // Move views to class level to resolve "Unused Variable" errors
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize variables
        val backButton: ImageButton = findViewById(R.id.btn_back_login)
        emailEditText = findViewById(R.id.et_login_email)
        passwordEditText = findViewById(R.id.et_login_password)
        val forgotPasswordText: TextView = findViewById(R.id.tv_forgot_password)
        val loginButton: AppCompatButton = findViewById(R.id.btn_login_final)
        val signUpFooter: TextView = findViewById(R.id.tv_signup_footer)

        backButton.setOnClickListener { finish() }

        forgotPasswordText.setOnClickListener {
            Toast.makeText(this, "Forgot Password Clicked", Toast.LENGTH_SHORT).show()
        }

        loginButton.setOnClickListener {
            if (validateInput()) {
                Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show()
                // Navigation logic for simulation goes here
            }
        }

        setupSignUpFooter(signUpFooter)
    }

    private fun validateInput(): Boolean {
        // Use the class-level variables initialized in onCreate
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString()

        if (email.isEmpty()) {
            Toast.makeText(this, "Email address is required.", Toast.LENGTH_SHORT).show()
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email address.", Toast.LENGTH_SHORT).show()
            return false
        }
        if (password.isEmpty()) {
            Toast.makeText(this, "Password cannot be empty.", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun setupSignUpFooter(textView: TextView) {
        val fullText = getString(R.string.signup_footer)
        val clickableText = "Sign up"
        val startIndex = fullText.indexOf(clickableText)

        if (startIndex == -1) return

        val spannableString = SpannableString(fullText)
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                val intent = Intent(this@LoginActivity, SignUpActivity::class.java)
                startActivity(intent)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                // Use KTX extension for Cyan theme color
                ds.color = "#06B6D4".toColorInt()
                ds.isUnderlineText = false
            }
        }

        spannableString.setSpan(
            clickableSpan,
            startIndex,
            startIndex + clickableText.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        textView.text = spannableString
        textView.movementMethod = LinkMovementMethod.getInstance()
    }
}