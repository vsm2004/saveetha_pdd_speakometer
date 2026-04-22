package com.simats.speakometer

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.activity.result.contract.ActivityResultContracts
import android.net.Uri
import android.widget.ImageView
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.simats.speakometer.network.ProfileUpdateRequest
import com.simats.speakometer.network.VoiceApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileEditActivity : AppCompatActivity() {

    private lateinit var etFullName: EditText
    private lateinit var etEmail: EditText
    private lateinit var tvInitial: TextView
    private lateinit var ivProfilePhoto: ImageView
    private lateinit var tvAccentPref: TextView
    private var selectedPhotoUri: Uri? = null
    private var selectedAccent: String = "Indian"

    private val accentOptions = arrayOf("🇮🇳 Indian English", "🇺🇸 American English", "🇬🇧 British English")
    private val accentKeys = arrayOf("Indian", "American", "British")

    private val pickMedia = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            selectedPhotoUri = uri
            try {
                contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } catch (e: SecurityException) {
                // Ignore if provider doesn't support persistent grants
            }
            ivProfilePhoto.setImageURI(uri)
            ivProfilePhoto.visibility = View.VISIBLE
            tvInitial.visibility = View.GONE
            enableSaveButton()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_edit)

        // Initialize Views
        etFullName = findViewById(R.id.et_full_name)
        etEmail = findViewById(R.id.et_email)
        tvInitial = findViewById(R.id.tv_profile_initial)
        ivProfilePhoto = findViewById(R.id.iv_profile_photo)
        tvAccentPref = findViewById(R.id.et_accent_pref)
        val btnBack = findViewById<ImageButton>(R.id.btn_back)
        val btnSave = findViewById<AppCompatButton>(R.id.btn_save_changes)
        val btnCancel = findViewById<AppCompatButton>(R.id.btn_cancel)
        val cameraButton = findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.profile_image_container)

        // Load existing user data from SharedPreferences
        val prefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val savedName = prefs.getString("USER_NAME", "") ?: ""
        val savedEmail = prefs.getString("USER_EMAIL", "") ?: ""
        selectedAccent = prefs.getString("ACCENT_PREF", "Indian") ?: "Indian"

        val originalName = savedName
        val originalEmail = savedEmail
        val originalAccent = selectedAccent

        if (savedName.isNotEmpty()) {
            etFullName.setText(savedName)
            tvInitial.text = savedName.first().uppercase()
        }
        if (savedEmail.isNotEmpty()) {
            etEmail.setText(savedEmail)
        }

        // Show the current accent label
        updateAccentLabel()

        val savedPhotoStr = prefs.getString("USER_PHOTO_URI", "") ?: ""
        if (savedPhotoStr.isNotEmpty()) {
            try {
                val uri = Uri.parse(savedPhotoStr)
                ivProfilePhoto.setImageURI(uri)
                ivProfilePhoto.visibility = View.VISIBLE
                tvInitial.visibility = View.GONE
            } catch (e: Exception) {
                // Ignore broken Uris
            }
        }

        // 1. Back Navigation Logic
        val backAction = {
            applyFadeTransition()
            finish()
        }
        btnBack.setOnClickListener { backAction() }
        btnCancel.setOnClickListener { backAction() }
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() { backAction() }
        })

        // 2. Profile Photo tap
        cameraButton.setOnClickListener { pickMedia.launch("image/*") }

        // 3. Accent Preference tap — show picker dialog
        tvAccentPref.setOnClickListener {
            val currentIndex = accentKeys.indexOf(selectedAccent).coerceAtLeast(0)
            AlertDialog.Builder(this)
                .setTitle("Select Accent Preference")
                .setSingleChoiceItems(accentOptions, currentIndex) { dialog, which ->
                    selectedAccent = accentKeys[which]
                    updateAccentLabel()
                    dialog.dismiss()
                    // Enable save if accent changed
                    val currentName = etFullName.text.toString().trim()
                    if (currentName.isNotEmpty()) enableSaveButton()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        // 4. Save Changes Logic
        btnSave.setOnClickListener { saveProfileData(originalName, originalEmail, originalAccent) }

        // 5. Highlight Save Button when text changes AND is different from original
        val textWatcher = object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val currentName = etFullName.text.toString().trim()
                val currentEmail = etEmail.text.toString().trim()
                if (currentName.isNotEmpty() && (currentName != originalName || currentEmail != originalEmail || selectedAccent != originalAccent)) {
                    enableSaveButton()
                } else {
                    btnSave.alpha = 0.5f
                    btnSave.isEnabled = false
                }
            }
            override fun afterTextChanged(s: android.text.Editable?) {}
        }

        etFullName.addTextChangedListener(textWatcher)

        // Initial state is disabled (no changes yet)
        btnSave.alpha = 0.5f
        btnSave.isEnabled = false
    }

    private fun enableSaveButton() {
        val btnSave = findViewById<AppCompatButton>(R.id.btn_save_changes)
        btnSave.alpha = 1.0f
        btnSave.isEnabled = true
    }

    private fun updateAccentLabel() {
        val index = accentKeys.indexOf(selectedAccent).coerceAtLeast(0)
        tvAccentPref.text = accentOptions[index]
        tvAccentPref.setTextColor(getColor(android.R.color.white))
    }

    private fun saveProfileData(originalName: String, originalEmail: String, originalAccent: String) {
        val name = etFullName.text.toString().trim()
        val email = etEmail.text.toString().trim()

        if (name.isEmpty()) {
            Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show()
            return
        }

        // 1. Persist to Database via API
        val prefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userId = prefs.getInt("USER_ID", -1)

        if (userId != -1) {
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val request = ProfileUpdateRequest(
                        userId = userId,
                        name = if (name != originalName) name else null,
                        email = if (email != originalEmail) email else null
                    )
                    
                    val response = VoiceApiClient.analysisService.updateProfile(request)
                    
                    withContext(Dispatchers.Main) {
                        if (response.isSuccessful) {
                            // 2. Only if API success, update SharedPreferences
                            val editor = prefs.edit()
                                .putString("USER_NAME", name)
                                .putString("USER_EMAIL", email)
                                .putString("ACCENT_PREF", selectedAccent)

                            selectedPhotoUri?.let {
                                editor.putString("USER_PHOTO_URI", it.toString())
                            }
                            editor.apply()

                            Toast.makeText(this@ProfileEditActivity, "Profile updated!", Toast.LENGTH_SHORT).show()
                            tvInitial.text = name.first().uppercase()
                            
                            // Go back
                            applyFadeTransition()
                            finish()
                        } else {
                            Toast.makeText(this@ProfileEditActivity, "Failed to update profile on server", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@ProfileEditActivity, "Network error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } else {
            // Fallback for local-only if no userId (shouldn't happen)
            val editor = prefs.edit()
                .putString("USER_NAME", name)
                .putString("USER_EMAIL", email)
                .apply()
            finish()
        }
    }

    private fun applyFadeTransition() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            overrideActivityTransition(Activity.OVERRIDE_TRANSITION_OPEN, 0, 0)
        } else {
            @Suppress("DEPRECATION")
            overridePendingTransition(0, 0)
        }
    }
}
