package com.simats.speakometerfrontend

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat // FIXED: Added missing import
import com.google.android.material.bottomnavigation.BottomNavigationView

class PracticeHubActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_practice_hub)

        // 1. Setup Modern Back Press Logic
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(this@PracticeHubActivity, HomePageActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                startActivity(intent)
                finish()
            }
        })

        // 2. Initial Setup
        setupBottomNavigation()
    }

    override fun onResume() {
        super.onResume()
        // FIXED: Only one checkPremiumStatus call needed
        checkPremiumStatus()
        refreshUI()
        findViewById<BottomNavigationView>(R.id.bottom_navigation).selectedItemId = R.id.nav_practice
    }

    private fun refreshUI() {
        val prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val suggestedTopic = prefs.getString("SUGGESTED_PRACTICE", "")

        val topics = listOf(
            Triple("Filler Words", R.id.iv_icon_filler, R.id.tv_desc_filler),
            Triple("Hesitation", R.id.iv_icon_confidence, R.id.tv_desc_confidence),
            Triple("Pace Variation", R.id.iv_icon_pace, R.id.tv_desc_pace),
            Triple("Art & Storytelling", R.id.iv_icon_art, R.id.tv_desc_art),
            Triple("Presentation Skills", R.id.iv_icon_pres, R.id.tv_desc_pres)
        )

        for ((topic, iconId, descId) in topics) {
            val isCompleted = prefs.getBoolean("COMPLETED_PRACTICE_$topic", false)
            val iconView = findViewById<ImageView>(iconId)
            val descView = findViewById<TextView>(descId)

            if (isCompleted && iconView != null) {
                iconView.setImageResource(R.drawable.ic_practice_check)
                iconView.setBackgroundResource(R.drawable.bg_practice_circle_green)
            }

            if (topic == suggestedTopic && descView != null) {
                val originalText = descView.text.toString()
                // Check using the string resource value to be safe across translations
                val suggestedPrefix = getString(R.string.suggested_topic_format).split("%")[0]

                if (!originalText.startsWith(suggestedPrefix)) {
                    // Use getString with the originalText as the placeholder argument
                    descView.text = getString(R.string.suggested_topic_format, originalText)
                    descView.setTextColor(ContextCompat.getColor(this, R.color.cyan_main))
                }
            }
        }
    }

    // FIXED: Combined the two duplicate checkPremiumStatus functions into one
    private fun checkPremiumStatus() {
        val prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val isPremium = prefs.getBoolean("IS_PREMIUM", false)

        // 1. Setup Free Units (Always Active)
        findViewById<ConstraintLayout>(R.id.cl_filler_words)?.setOnClickListener {
            startExercise("Filler Words")
        }
        findViewById<ConstraintLayout>(R.id.cl_confidence)?.setOnClickListener {
            startExercise("Hesitation")
        }
        findViewById<ConstraintLayout>(R.id.cl_pace_control)?.setOnClickListener {
            startExercise("Pace Variation")
        }

        // 2. Setup Premium Units
        val clArt = findViewById<ConstraintLayout>(R.id.cl_art_storytelling)
        val clPres = findViewById<ConstraintLayout>(R.id.cl_presentation_skills)

        if (isPremium) {
            // FIXED: Passed the required parameters
            if (clArt != null && clPres != null) {
                unlockPremiumUnits(clArt, clPres)
            }
        } else {
            val lockListener = View.OnClickListener {
                startActivity(Intent(this, PremiumScreenActivity::class.java))
            }
            clArt?.setOnClickListener(lockListener)
            clPres?.setOnClickListener(lockListener)
            findViewById<TextView>(R.id.tv_upgrade_now)?.setOnClickListener(lockListener)
        }
    }

    private fun setupBottomNavigation() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.itemIconTintList = null
        bottomNav.selectedItemId = R.id.nav_practice

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, HomePageActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                    })
                    true
                }
                R.id.nav_history -> {
                    startActivity(Intent(this, HistoryActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                    })
                    true
                }
                R.id.nav_practice -> true
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileAccountActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                    })
                    true
                }
                else -> false
            }
        }
    }

    private fun startExercise(type: String) {
        val intent = Intent(this, PracticeExerciseActivity::class.java)
        intent.putExtra("MISTAKE_TYPE", type)
        startActivity(intent)
    }

    private fun unlockPremiumUnits(clArt: ConstraintLayout, clPres: ConstraintLayout) {
        val upgradeCard = findViewById<ConstraintLayout>(R.id.cl_upgrade_card)

        clArt.setOnClickListener { startExercise("Art & Storytelling") }
        clPres.setOnClickListener { startExercise("Presentation Skills") }

        val ivArtIcon = clArt.findViewById<ImageView>(R.id.iv_icon_art)
        ivArtIcon?.setBackgroundResource(R.drawable.bg_practice_circle_purple)
        ivArtIcon?.setImageResource(R.drawable.ic_practice_play)

        val ivPresIcon = clPres.findViewById<ImageView>(R.id.iv_icon_pres)
        ivPresIcon?.setBackgroundResource(R.drawable.bg_practice_circle_purple)
        ivPresIcon?.setImageResource(R.drawable.ic_practice_play)

        upgradeCard?.visibility = View.GONE
    }
}