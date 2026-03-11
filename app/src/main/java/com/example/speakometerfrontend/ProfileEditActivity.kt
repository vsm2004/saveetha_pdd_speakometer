package com.example.speakometerfrontend

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton

class ProfileEditActivity : AppCompatActivity() {

    // UI Elements
    private lateinit var etFullName: EditText
    private lateinit var etEmail: EditText
    private lateinit var tvInitial: TextView

    // Result Launcher for Gallery
    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            Toast.makeText(this, "Image selected from Gallery", Toast.LENGTH_SHORT).show()
            tvInitial.text = "" // Clear initial if photo is set
        }
    }

    // Result Launcher for Camera
    private val takePicture = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // FIX: Modern way to get Bitmap without deprecation errors
            val imageBitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                result.data?.extras?.getParcelable("data", Bitmap::class.java)
            } else {
                @Suppress("DEPRECATION")
                result.data?.extras?.getParcelable("data")
            }

            imageBitmap?.let {
                // Now we use the variable to fix the "Unused variable" warning
                Toast.makeText(this, "Photo taken: ${it.width}x${it.height}", Toast.LENGTH_SHORT).show()
                tvInitial.text = ""
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_edit)

        // Initialize Views
        etFullName = findViewById(R.id.et_full_name)
        etEmail = findViewById(R.id.et_email)
        tvInitial = findViewById(R.id.tv_profile_initial)
        val btnBack = findViewById<ImageButton>(R.id.btn_back)
        val btnSave = findViewById<AppCompatButton>(R.id.btn_save_changes)
        val btnCancel = findViewById<AppCompatButton>(R.id.btn_cancel)
        val cameraButton = findViewById<FrameLayout>(R.id.profile_image_container)

        // 1. Back Navigation Logic
        val backAction = {
            val intent = Intent(this, ProfileAccountActivity::class.java)
            startActivity(intent)
            applyFadeTransition()
            finish()
        }

        btnBack.setOnClickListener { backAction() }
        btnCancel.setOnClickListener { backAction() }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() { backAction() }
        })

        // 2. Profile Photo Selection Logic
        cameraButton.setOnClickListener {
            showImagePickerOptions()
        }

        // 3. Save Changes Logic
        btnSave.setOnClickListener {
            saveProfileData()
        }
    }

    private fun showImagePickerOptions() {
        val options = arrayOf("Take Photo", "Choose from Gallery", "Cancel")

        // FIX: Changed to R.style.Theme_AppCompat_DayNight_Dialog_Alert (more reliable)
        val builder = AlertDialog.Builder(this, com.google.android.material.R.style.Theme_Material3_Dark_Dialog_Alert)
        builder.setTitle("Update Profile Picture")
        builder.setItems(options) { dialog, which ->
            when (which) {
                0 -> { // Camera
                    val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    takePicture.launch(takePictureIntent)
                }
                1 -> { // Gallery
                    getContent.launch("image/*")
                }
                2 -> dialog.dismiss()
            }
        }
        builder.show()
    }

    private fun saveProfileData() {
        val name = etFullName.text.toString().trim()
        val email = etEmail.text.toString().trim()

        if (name.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Please fill in all details", Toast.LENGTH_SHORT).show()
            return
        }

        Toast.makeText(this, "Changes saved for $name", Toast.LENGTH_SHORT).show()

        val intent = Intent(this, ProfileAccountActivity::class.java)
        startActivity(intent)
        finish()
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