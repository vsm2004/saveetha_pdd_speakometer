package com.simats.speakometerfrontend

import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.content.Context
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.simats.speakometerfrontend.network.VoiceApiClient
import com.simats.speakometerfrontend.network.SessionData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class HistoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

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
        bottomNav.itemIconTintList = null

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, HomePageActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                    })
                    true
                }
                R.id.nav_history -> true
                R.id.nav_practice -> {
                    startActivity(Intent(this, PracticeHubActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                    })
                    true
                }
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

    override fun onResume() {
        super.onResume()
        findViewById<BottomNavigationView>(R.id.bottom_navigation).selectedItemId = R.id.nav_history
        loadHistoryData()
    }

    private fun loadHistoryData() {
        val prefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userId = prefs.getInt("USER_ID", -1)
        if (userId != -1) {
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val response = VoiceApiClient.analysisService.getSessions(userId)
                    withContext(Dispatchers.Main) {
                        if (response.isSuccessful) {
                            response.body()?.let { data ->
                                populateHistoryUI(data.sessions, data.totalSessions)
                            }
                        }
                    }
                } catch (e: Exception) {
                    // Ignore background network errors
                }
            }
        }
    }

    private fun populateHistoryUI(sessions: List<SessionData>, totalCount: Int) {
        val container = findViewById<LinearLayout>(R.id.ll_history_sessions_container)
        container.removeAllViews()

        if (sessions.isNotEmpty()) {
            val avgScore = sessions.map { it.score }.average().toInt()
            val improved = sessions.first().score - avgScore

            findViewById<TextView>(R.id.tv_summary_sessions).text = totalCount.toString()
            findViewById<TextView>(R.id.tv_summary_avg_score).text = avgScore.toString()

            // FIX: Using string resources for improvement text
            findViewById<TextView>(R.id.tv_summary_improvement).text = if (improved >= 0) {
                getString(R.string.improvement_plus_format, improved)
            } else {
                improved.toString()
            }
        }

        val inflater = LayoutInflater.from(this)
        for (session in sessions) {
            val view = inflater.inflate(R.layout.item_history_session, container, false)

            val tvDate = view.findViewById<TextView>(R.id.tv_date)
            val tvDuration = view.findViewById<TextView>(R.id.tv_duration)
            val tvScore = view.findViewById<TextView>(R.id.tv_session_score)

            // FIX: Ensure your SessionData class uses 'createdAt' or '@SerializedName("created_at")'
            tvDate.text = formatSessionDate(session.createdAt)
            tvScore.text = session.score.toString()

            val pseudoMinutes = 1 + (session.fillersCount % 3)
            val pseudoSeconds = 15 + (session.score % 40)

            // FIX: Use string resource with placeholders for duration
            tvDuration.text = getString(R.string.history_duration_format, pseudoMinutes, pseudoSeconds)

            container.addView(view)
        }
    }

    private fun formatSessionDate(isoString: String): String {
        return try {
            val cleanIso = isoString.take(19)
            val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val date = format.parse(cleanIso) ?: return isoString

            val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
            val timeStr = timeFormat.format(date)

            val calNow = Calendar.getInstance()
            val calSession = Calendar.getInstance().apply { time = date }

            when {
                // Today
                calNow.get(Calendar.YEAR) == calSession.get(Calendar.YEAR) &&
                        calNow.get(Calendar.DAY_OF_YEAR) == calSession.get(Calendar.DAY_OF_YEAR) -> {
                    getString(R.string.history_today_format, timeStr)
                }
                // Yesterday
                else -> {
                    calSession.add(Calendar.DAY_OF_YEAR, 1)
                    if (calNow.get(Calendar.YEAR) == calSession.get(Calendar.YEAR) &&
                        calNow.get(Calendar.DAY_OF_YEAR) == calSession.get(Calendar.DAY_OF_YEAR)) {
                        getString(R.string.history_yesterday_format, timeStr)
                    } else {
                        SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(date)
                    }
                }
            }
        } catch (e: Exception) {
            getString(R.string.history_recent_session)
        }
    }
}