package com.example.speakometerfrontend

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton

/**
 * Slide #16: Session Details Activity.
 * Provides a detailed breakdown of a specific recording session.
 */
class SessionDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_session_details)

        // 1. Fix Deprecated onBackPressed: Register modern callback
        val backCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        }
        onBackPressedDispatcher.addCallback(this, backCallback)

        // 2. Initialize Views
        val btnBackArrow = findViewById<ImageView>(R.id.btn_back_arrow)
        val btnBackToHistory = findViewById<AppCompatButton>(R.id.btn_back_to_history)
        val btnViewTips = findViewById<TextView>(R.id.btn_view_tips)
        val tvScore = findViewById<TextView>(R.id.tv_detail_score)
        val pbFiller = findViewById<ProgressBar>(R.id.pb_filler)
        val pbHesitation = findViewById<ProgressBar>(R.id.pb_hesitation)
        val pbPace = findViewById<ProgressBar>(R.id.pb_pace)

        // 3. Apply Data: Resolves "Variable Never Used"
        tvScore.text = getString(R.string.session_score_value)
        pbFiller.progress = 65
        pbHesitation.progress = 30
        pbPace.progress = 15

        // 4. Back Navigation (Top Arrow)
        btnBackArrow.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // 5. Back to History (Bottom Button): Resolves "btnBackToHistory never used"
        btnBackToHistory.setOnClickListener {
            // Navigate back to Slide #15
            val intent = Intent(this, HistoryActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }

        // 6. View Improvement Tips: One single listener block
        btnViewTips.setOnClickListener {
            val intent = Intent(this, ImprovementTipsActivity::class.java)
            startActivity(intent)
        }
    }
}