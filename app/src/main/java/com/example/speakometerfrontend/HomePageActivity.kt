package com.example.speakometerfrontend // Make sure this matches your package name

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import de.hdodenhof.circleimageview.CircleImageView

class HomePageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // This links the Kotlin file to your activity_homepage.xml layout
        setContentView(R.layout.activity_homepage)

        // --- Find all the interactive views from your layout ---

        // Top section
        val userNameTextView: TextView = findViewById(R.id.tv_user_name)
        val profileImageView: CircleImageView = findViewById(R.id.profile_image)

        // Main content
        val analysisButton: AppCompatButton = findViewById(R.id.btn_analysis)

        // Custom Bottom Navigation Buttons
        val homeButton: ImageButton = findViewById(R.id.btn_home)
        val historyButton: ImageButton = findViewById(R.id.btn_history)
        val practiceButton: ImageButton = findViewById(R.id.btn_practice)
        val profileButton: ImageButton = findViewById(R.id.btn_profile)

        // --- Set up click listeners for all buttons ---

        // Analysis Button
        analysisButton.setOnClickListener {
            // TODO: Implement the action for starting analysis
            Toast.makeText(this, "Start Analysis Clicked!", Toast.LENGTH_SHORT).show()
        }

        // Profile image in the header
        profileImageView.setOnClickListener {
            // You can make this open the profile screen as well
            Toast.makeText(this, "Profile Image Clicked!", Toast.LENGTH_SHORT).show()
            // For example, you could navigate to a new ProfileActivity
            // startActivity(Intent(this, ProfileActivity::class.java))
        }

        // --- Bottom Navigation Button Clicks ---

        homeButton.setOnClickListener {
            // Since we are already on the home page, we can just give feedback
            // or reload the content if necessary.
            Toast.makeText(this, "Home Clicked", Toast.LENGTH_SHORT).show()
            // Here you might want to highlight the button
            updateButtonSelection(it.id)
        }

        historyButton.setOnClickListener {
            // TODO: Replace with navigation to a HistoryActivity or HistoryFragment
            Toast.makeText(this, "History Clicked", Toast.LENGTH_SHORT).show()
            updateButtonSelection(it.id)
            // Example: startActivity(Intent(this, HistoryActivity::class.java))
        }

        practiceButton.setOnClickListener {
            // TODO: Replace with navigation to a PracticeActivity or PracticeFragment
            Toast.makeText(this, "Practice Clicked", Toast.LENGTH_SHORT).show()
            updateButtonSelection(it.id)
        }

        profileButton.setOnClickListener {
            // TODO: Replace with navigation to a ProfileActivity or ProfileFragment
            Toast.makeText(this, "Profile Clicked", Toast.LENGTH_SHORT).show()
            updateButtonSelection(it.id)
        }

        // Set the initial selected state for the home button
        updateButtonSelection(R.id.btn_home)
    }

    /**
     * A helper function to visually update which button is selected.
     * This is a basic example; you can make it more advanced.
     */
    private fun updateButtonSelection(selectedButtonId: Int) {
        // You could change the background or icon tint here to show selection.
        // For example, setting the background of the selected button.
        // This is a simple placeholder to show the concept.
        findViewById<ImageButton>(R.id.btn_home).isSelected = (selectedButtonId == R.id.btn_home)
        findViewById<ImageButton>(R.id.btn_history).isSelected = (selectedButtonId == R.id.btn_history)
        findViewById<ImageButton>(R.id.btn_practice).isSelected = (selectedButtonId == R.id.btn_practice)
        findViewById<ImageButton>(R.id.btn_profile).isSelected = (selectedButtonId == R.id.btn_profile)

        // To make this visually work, you would need to create a selector drawable for the button backgrounds
        // e.g., android:background="@drawable/bottom_nav_button_selector"
    }
}
