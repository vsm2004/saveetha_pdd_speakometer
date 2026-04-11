package com.simats.speakometerfrontend


import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.core.graphics.toColorInt
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.simats.speakometerfrontend.network.VoiceApiClient
import androidx.core.content.edit
import java.text.SimpleDateFormat
import java.util.Locale

class ProfileAccountActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_account)

        // 1. Setup Modern Back Press Dispatcher
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(this@ProfileAccountActivity, HomePageActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)
                applyFadeTransition()
                finish()
            }
        })

        // 2. Setup Bottom Navigation
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.itemIconTintList = null
        bottomNav.isItemActiveIndicatorEnabled = false
        bottomNav.selectedItemId = R.id.nav_profile

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, HomePageActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                    })
                    applyFadeTransition()
                    true
                }
                R.id.nav_history -> {
                    startActivity(Intent(this, HistoryActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                    })
                    applyFadeTransition()
                    true
                }
                R.id.nav_practice -> {
                    startActivity(Intent(this, PracticeHubActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                    })
                    applyFadeTransition()
                    true
                }
                R.id.nav_profile -> true
                else -> false
            }
        }

        // 3. Click Listeners
        findViewById<TextView>(R.id.tv_edit_profile).setOnClickListener {
            startActivity(Intent(this, ProfileEditActivity::class.java))
        }

        findViewById<ConstraintLayout>(R.id.btn_menu_settings).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        findViewById<ConstraintLayout>(R.id.btn_logout).setOnClickListener {
            showLogoutDialog()
        }
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.logout_title))
            .setMessage(getString(R.string.logout_confirm_msg))
            .setPositiveButton(getString(R.string.logout_btn)) { _, _ ->
                getSharedPreferences("UserPrefs", Context.MODE_PRIVATE).edit {
                    putBoolean("IS_LOGGED_IN", false)
                    remove("USER_ID")
                    remove("USER_NAME")
                    remove("USER_EMAIL")
                    remove("IS_PREMIUM")
                    remove("PREMIUM_PLAN")
                    remove("PREMIUM_EXPIRY")
                }
                val intent = Intent(this, LoginActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                startActivity(intent)
                finish()
            }
            .setNegativeButton(getString(android.R.string.cancel), null)
            .show()
    }

    override fun onResume() {
        super.onResume()
        findViewById<BottomNavigationView>(R.id.bottom_navigation).selectedItemId = R.id.nav_profile

        val prefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userName = prefs.getString("USER_NAME", "") ?: ""
        val userEmail = prefs.getString("USER_EMAIL", null)

        findViewById<TextView>(R.id.tv_display_name).text = userName
        if (userName.isNotEmpty()) {
            findViewById<TextView>(R.id.tv_avatar_initial).text = userName.first().uppercase()
        }
        if (userEmail != null) {
            findViewById<TextView>(R.id.tv_display_email).text = userEmail
        }

        fetchUserStats(prefs.getInt("USER_ID", -1))
        setupPremiumUI(prefs)
    }

    private fun fetchUserStats(userId: Int) {
        if (userId == -1) return

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = VoiceApiClient.analysisService.getSessions(userId)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val data = response.body()
                        if (data != null && data.sessions.isNotEmpty()) {
                            val total = data.totalSessions
                            val avg = data.sessions.map { it.score }.average().toInt()
                            val improved = data.sessions.first().score - avg

                            findViewById<TextView>(R.id.tv_stats_sessions_val).text = total.toString()
                            findViewById<TextView>(R.id.tv_stats_score_val).text = avg.toString()
                            findViewById<TextView>(R.id.tv_stats_improved_val).text =
                                if (improved >= 0) "+$improved" else improved.toString()
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    resetStats()
                }
            }
        }
    }

    private fun resetStats() {
        findViewById<TextView>(R.id.tv_stats_sessions_val).text = "0"
        findViewById<TextView>(R.id.tv_stats_score_val).text = "0"
        findViewById<TextView>(R.id.tv_stats_improved_val).text = "0"
    }

    private fun setupPremiumUI(prefs: android.content.SharedPreferences) {
        val isPremium = prefs.getBoolean("IS_PREMIUM", false)
        val planType = prefs.getString("PREMIUM_PLAN", "Free") ?: "Free"

        findViewById<ConstraintLayout>(R.id.btn_menu_stats).setOnClickListener {
            val target = if (isPremium) PremiumAnalyticsActivity::class.java else PremiumScreenActivity::class.java
            startActivity(Intent(this, target))
        }

        findViewById<ConstraintLayout>(R.id.btn_menu_achievements).setOnClickListener {
            val target = if (isPremium) PremiumViewAchievementsActivity::class.java else PremiumScreenActivity::class.java
            startActivity(Intent(this, target))
        }

        val tvPlanTitle = findViewById<TextView>(R.id.tv_plan_title)
        val tvPlanDesc = findViewById<TextView>(R.id.tv_plan_desc)
        val btnSeePlans = findViewById<TextView>(R.id.btn_see_plans)

        if (isPremium) {
            tvPlanTitle.text = getString(R.string.premium_plan_format, planType)
            val formattedDate = formatExpiryDate(prefs.getString("PREMIUM_EXPIRY", null))
            tvPlanDesc.text = formattedDate?.let { getString(R.string.billing_format, it) } ?: getString(R.string.billing_active)

            btnSeePlans.text = getString(R.string.manage_subscription)
            btnSeePlans.setTextColor("#4ADE80".toColorInt())
            btnSeePlans.setOnClickListener { startActivity(Intent(this, PremiumExpiryActivity::class.java)) }
        } else {
            tvPlanTitle.text = getString(R.string.plan_free_title)
            tvPlanDesc.text = getString(R.string.plan_free_desc)
            btnSeePlans.text = getString(R.string.see_premium_plans)
            btnSeePlans.setTextColor("#EC4899".toColorInt())
            btnSeePlans.setOnClickListener { startActivity(Intent(this, PremiumScreenActivity::class.java)) }
        }
    }

    private fun formatExpiryDate(rawDate: String?): String? {
        if (rawDate == null) return null
        return try {
            val pattern = if (rawDate.contains("T")) "yyyy-MM-dd'T'HH:mm:ss" else "yyyy-MM-dd HH:mm:ss"
            val date = SimpleDateFormat(pattern, Locale.getDefault()).parse(rawDate)
            date?.let { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(it) }
        } catch (e: Exception) { null }
    }

    private fun applyFadeTransition() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            overrideActivityTransition(OVERRIDE_TRANSITION_OPEN, 0, 0)
        } else {
            @Suppress("DEPRECATION")
            overridePendingTransition(0, 0)
        }
    }
}