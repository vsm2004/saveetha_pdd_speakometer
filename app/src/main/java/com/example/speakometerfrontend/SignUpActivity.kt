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
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.graphics.toColorInt

class SignUpActivity : AppCompatActivity() {

    // Move these here so the whole class can use them
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var termsCheckBox: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        // Initialize the class-level variables
        val backButton: ImageButton = findViewById(R.id.btn_back)
        emailEditText = findViewById(R.id.et_email)
        passwordEditText = findViewById(R.id.et_password)
        confirmPasswordEditText = findViewById(R.id.et_confirm_password)
        termsCheckBox = findViewById(R.id.cb_terms)
        val termsTextView: TextView = findViewById(R.id.tv_terms_link)
        val createAccountButton: AppCompatButton = findViewById(R.id.btn_create_account_final)

        backButton.setOnClickListener { finish() }

        setupTermsLink(termsTextView)

        createAccountButton.setOnClickListener {
            if (validateInput()) {
                Toast.makeText(this, "Account Creation Successful!", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, PermissionActivity::class.java)
                startActivity(intent)
                // Proceed to next slide logic
            }
        }
    }

    private fun validateInput(): Boolean {
        // Now we use the variables we already "found" in onCreate
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString()
        val confirmPassword = confirmPasswordEditText.text.toString()
        val termsChecked = termsCheckBox.isChecked

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Enter a valid email", Toast.LENGTH_SHORT).show()
            return false
        }

        // Match your 2026 project requirement: 8 characters (Figma hint said 8, code said 6)
        if (password.length < 8) {
            Toast.makeText(this, "Password must be at least 8 characters.", Toast.LENGTH_SHORT).show()
            return false
        }

        if (password != confirmPassword) {
            Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_SHORT).show()
            return false
        }

        if (!termsChecked) {
            Toast.makeText(this, "Please agree to terms.", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun setupTermsLink(textView: TextView) {
        val fullText = getString(R.string.terms_agreement_text)
        val spannableString = SpannableString(fullText)

        // Fixed "url is never used" by removing it or using _
        val clickableParts = listOf("Terms of Service", "Privacy Policy")

        for (text in clickableParts) {
            val startIndex = fullText.indexOf(text)
            if (startIndex != -1) {
                val clickableSpan = object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        Toast.makeText(this@SignUpActivity, "Opening $text...", Toast.LENGTH_SHORT).show()
                    }
                    override fun updateDrawState(ds: TextPaint) {
                        super.updateDrawState(ds)
                        ds.isUnderlineText = false
                        ds.color = "#06B6D4".toColorInt() // Keep it consistent with your Cyan theme
                    }
                }
                spannableString.setSpan(clickableSpan, startIndex, startIndex + text.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }
        textView.text = spannableString
        textView.movementMethod = LinkMovementMethod.getInstance()
    }
}
