package com.simats.speakometerfrontend


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
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.simats.speakometerfrontend.network.ApiClient
import com.simats.speakometerfrontend.network.SignupRequest
import org.json.JSONObject

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
        // Fixed mismatched IDs from the XML layout:
        // et_password maps to the second field (which has the "Username" label in XML)
        // et_confirm_password maps to the third field (which has the "Password" label in XML)
        emailEditText = findViewById(R.id.et_email)
        
        // Use the proper Password field for length validation
        passwordEditText = findViewById(R.id.et_confirm_password)
        
        // We do not actually have a confirmation field in the UI. 
        // The layout only has: Email (et_email), Username (et_password), and Password (et_confirm_password).
        // Let's bind confirmPasswordEditText to exactly the same as password, and we'll adjust the logic below.
        confirmPasswordEditText = findViewById(R.id.et_confirm_password)
        
        termsCheckBox = findViewById(R.id.cb_terms)
        val termsTextView: TextView = findViewById(R.id.tv_terms_link)
        val createAccountButton: AppCompatButton = findViewById(R.id.btn_create_account_final)

        backButton.setOnClickListener { finish() }

        setupTermsLink(termsTextView)

        createAccountButton.setOnClickListener {
            if (validateInput()) {
                performSignUp()
            }
        }
    }

    private fun performSignUp() {
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString()

        val createAccountButton: AppCompatButton = findViewById(R.id.btn_create_account_final)
        createAccountButton.isEnabled = false
        createAccountButton.text = "Creating Account..."

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val request = SignupRequest(email = email, password = password)
                val response = ApiClient.authService.signupUser(request)

                withContext(Dispatchers.Main) {
                    createAccountButton.isEnabled = true
                    createAccountButton.text = "Create Account"

                    if (response.isSuccessful) {
                        // Save login state and user info
                        val username = findViewById<EditText>(R.id.et_password).text.toString().trim()
                        getSharedPreferences("UserPrefs", MODE_PRIVATE).edit()
                            .putBoolean("IS_LOGGED_IN", true)
                            .putString("USER_EMAIL", email)
                            .putString("USER_NAME", username.ifEmpty { email.substringBefore("@") })
                            .apply()
                        Toast.makeText(this@SignUpActivity, "Account Creation Successful!", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@SignUpActivity, PermissionsActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        val errorBody = response.errorBody()?.string()
                        val errorMsg = try {
                            JSONObject(errorBody ?: "").getString("detail")
                        } catch (e: Exception) {
                            "Signup failed: ${response.code()}"
                        }
                        Toast.makeText(this@SignUpActivity, errorMsg, Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    createAccountButton.isEnabled = true
                    createAccountButton.text = "Create Account"
                    Toast.makeText(this@SignUpActivity, "Network error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun validateInput(): Boolean {
        // Now we use the variables we already "found" in onCreate
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString()
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
