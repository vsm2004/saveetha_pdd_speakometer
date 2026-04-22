package com.simats.speakometer

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
import androidx.core.graphics.toColorInt
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.simats.speakometer.network.ApiClient
import com.simats.speakometer.network.LoginRequest
import org.json.JSONObject
import androidx.core.content.edit // Important for KTX SharedPreferences

class LoginActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

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
                performLogin()
            }
        }

        setupSignUpFooter(signUpFooter)
    }

    private fun performLogin() {
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString()

        val loginButton: AppCompatButton = findViewById(R.id.btn_login_final)
        loginButton.isEnabled = false
        loginButton.text = getString(R.string.login_text)

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val request = LoginRequest(email, password)
                val response = ApiClient.authService.loginUser(request)

                withContext(Dispatchers.Main) {
                    loginButton.isEnabled = true
                    loginButton.text = getString(R.string.login_text)

                    // FIXED: Added the missing check for response success
                    if (response.isSuccessful) {
                        val authResponse = response.body()
                        val user = authResponse?.user

                        // Save login state — Always sync from server to ensure professional consistency
                        val prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)
                        
                        // Trust the server for all data on a fresh login
                        val serverName = user?.name ?: "User"
                        val serverPremium = user?.premiumStatus ?: false
                        val serverExpiry = user?.premiumExpiry
                        val userId = user?.id ?: -1

                        prefs.edit {
                            putBoolean("IS_LOGGED_IN", true)
                            putInt("USER_ID", userId)
                            putString("USER_EMAIL", email)
                            putString("USER_NAME", serverName)
                            putBoolean("IS_PREMIUM", serverPremium)
                            putString("PREMIUM_EXPIRY", serverExpiry)
                        }

                        Toast.makeText(this@LoginActivity, "Welcome back, ${user?.name ?: "User"}!", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@LoginActivity, PermissionsActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        val errorBody = response.errorBody()?.string()
                        val errorMsg = try {
                            JSONObject(errorBody ?: "").getString("detail")
                        } catch (e: Exception) {
                            "Login failed: ${response.code()}"
                        }
                        Toast.makeText(this@LoginActivity, errorMsg, Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    loginButton.isEnabled = true
                    loginButton.text = getString(R.string.login_text)
                    Toast.makeText(this@LoginActivity, "Network error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun validateInput(): Boolean {
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
        // Ensure this string exists in strings.xml
        val fullText = try { getString(R.string.signup_footer) } catch (e: Exception) { "Don't have an account? Sign up" }
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