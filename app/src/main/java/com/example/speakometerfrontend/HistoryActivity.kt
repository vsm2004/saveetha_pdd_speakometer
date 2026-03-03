package com.example.speakometerfrontend

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

/**
 * Slide #15: History Activity.
 * Displays past recording summaries and individual session cards.
 */
class HistoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        // Modern Back Press Logic
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(this@HistoryActivity, HomePageActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        // Fix: Disable tinting and set History as the active tab
        bottomNav.itemIconTintList = null
        bottomNav.selectedItemId = R.id.nav_history

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    val intent = Intent(this, HomePageActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    true
                }
                R.id.nav_history -> true
                R.id.nav_practice -> {
                    Toast.makeText(this, "Opening Practice Mode", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }
    }
}