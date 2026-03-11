package com.example.speakometerfrontend

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout

class PracticeHubActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_practice_hub)

        // Check if user is premium (In a real app, this comes from a Database/SharedPrefs)
        // For this flow, we assume true if they just completed the payment
        val isPremiumUser = true

        if (isPremiumUser) {
            unlockPremiumUnits()
        }
    }

    private fun unlockPremiumUnits() {
        // 1. Find the locked layouts (we need to add IDs to them in XML or find them by child)
        val rootLayout = findViewById<LinearLayout>(R.id.scroll_practice).getChildAt(0) as LinearLayout

        // Let's assume you add IDs: cl_art_and_story and cl_presentation_skills
        // If not, we access them by index (Art is the 4th card, Pres is 5th)
        val clArt = rootLayout.getChildAt(3) as ConstraintLayout
        val clPres = rootLayout.getChildAt(4) as ConstraintLayout
        val upgradeCard = rootLayout.getChildAt(5) as ConstraintLayout

        // 2. Change Art & Storytelling to "Unlocked"
        val ivArtIcon = clArt.findViewById<ImageView>(R.id.iv_icon_art)
        ivArtIcon.setBackgroundResource(R.drawable.bg_practice_circle_purple) // Change grey to purple
        ivArtIcon.setImageResource(R.drawable.ic_practice_play) // Change lock to play icon

        // 3. Change Presentation Skills to "Unlocked"
        val ivPresIcon = clPres.findViewById<ImageView>(R.id.iv_icon_pres)
        ivPresIcon.setBackgroundResource(R.drawable.bg_practice_circle_purple)
        ivPresIcon.setImageResource(R.drawable.ic_practice_play)

        // 4. Hide the "Upgrade Now" card since they are already premium
        upgradeCard.visibility = View.GONE
    }
}