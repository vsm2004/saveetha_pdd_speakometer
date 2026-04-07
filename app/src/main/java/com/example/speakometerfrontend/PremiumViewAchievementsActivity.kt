package com.example.speakometerfrontend

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.content.Context
import androidx.lifecycle.lifecycleScope
import com.example.speakometerfrontend.network.VoiceApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PremiumViewAchievementsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_premium_view_achievements)

        val btnAwesome = findViewById<TextView>(R.id.btnAwesome)

        btnAwesome.setOnClickListener {
            showNotificationConsentDialog()
        }

        loadAchievementsData()
    }

    private fun loadAchievementsData() {
        val prefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val completedPractices = prefs.all.keys.count { it.startsWith("COMPLETED_PRACTICE_") }
        
        val tvAchievementTitle = findViewById<TextView>(R.id.tvAchievementTitle)
        val tvBadgeNamePill = findViewById<TextView>(R.id.tvBadgeNamePill)
        val tvAchievementDesc = findViewById<TextView>(R.id.tvAchievementDesc)
        
        when {
            completedPractices == 0 -> {
                tvAchievementTitle.text = "Getting Started"
                tvBadgeNamePill.text = "Bronze Speaker Ribbon"
                tvAchievementDesc.text = "Complete your first practice module to unlock higher tiers!"
            }
            completedPractices in 1..2 -> {
                tvAchievementTitle.text = "Practice Novice"
                tvBadgeNamePill.text = "Silver Speaker Ribbon"
                tvAchievementDesc.text = "You've successfully completed $completedPractices practice modules. Keep it up!"
            }
            else -> {
                tvAchievementTitle.text = "Outstanding Dedication"
                tvBadgeNamePill.text = "Gold Speaker Ribbon"
                tvAchievementDesc.text = "You are a pro! You've conquered $completedPractices practice modules!"
            }
        }
        
        // Define default bottom stats in case network fails
        val tvStat1 = findViewById<TextView>(R.id.tv_ach_stat_1)
        val tvStat2 = findViewById<TextView>(R.id.tv_ach_stat_2)
        val tvStat3 = findViewById<TextView>(R.id.tv_ach_stat_3)
        
        tvStat1.text = "$completedPractices\nModules"
        tvStat2.text = "0\nAvg Score"
        tvStat3.text = "0\nSessions"
        
        val userId = prefs.getInt("USER_ID", -1)
        if (userId != -1) {
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val response = VoiceApiClient.analysisService.getSessions(userId)
                    withContext(Dispatchers.Main) {
                        if (response.isSuccessful) {
                            val data = response.body()
                            if (data != null && data.sessions.isNotEmpty()) {
                                val avg = data.sessions.map { it.score }.average().toInt()
                                val total = data.totalSessions
                                
                                tvStat2.text = "$avg\nAvg Score"
                                tvStat3.text = "$total\nSessions"
                            }
                        }
                    }
                } catch (e: Exception) {
                    // Keep defaults if failed
                }
            }
        }
    }

    private fun showNotificationConsentDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Enable Notifications")
        builder.setMessage("Would you like to receive daily updates on celebration streaks, new challenges, and motivational tips?")

        // Inside PremiumViewAchievementsActivity.kt, within the OK button of the dialog:
        builder.setPositiveButton("OK") { _, _ ->
            Toast.makeText(this, "Notifications enabled!", Toast.LENGTH_SHORT).show()

            // Move to Feedback Screen before finishing the session
            val intent = Intent(this, FeedbackActivity::class.java)
            startActivity(intent)
            finish()
        }

        builder.setNegativeButton("Maybe Later") { dialog, _ ->
            dialog.dismiss()
            finish()
        }

        val dialog = builder.create()
        dialog.show()
    }
}