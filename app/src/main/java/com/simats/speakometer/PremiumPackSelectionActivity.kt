package com.simats.speakometer

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.toColorInt

class PremiumPackSelectionActivity : AppCompatActivity() {

    private var selectedPlan: String? = null // Keeps track of which plan is chosen

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_premium_pack_selection)

        val btnBack = findViewById<ImageButton>(R.id.btn_back)
        val cardYearly = findViewById<ConstraintLayout>(R.id.cl_plan_yearly)
        val cardMonthly = findViewById<ConstraintLayout>(R.id.cl_plan_monthly)
        val btnSubscribe = findViewById<AppCompatButton>(R.id.btn_subscribe)

        // Navigation Back
        btnBack.setOnClickListener { navigateBack() }
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() { navigateBack() }
        })

        // Yearly Card Click
        cardYearly.setOnClickListener {
            updateSelection("yearly")
        }

        // Monthly Card Click
        cardMonthly.setOnClickListener {
            updateSelection("monthly")
        }

        // Subscribe Click
        btnSubscribe.setOnClickListener {
            if (selectedPlan != null) {
                val intent = Intent(this, PremiumPaymentActivity::class.java)
                
                // Pass dynamic details based on selection
                val type = if (selectedPlan == "yearly") "Yearly" else "Monthly"
                val price = if (selectedPlan == "yearly") "1199" else "199"
                
                intent.putExtra("IS_FREE_TRIAL", false)
                intent.putExtra("PLAN_TYPE", type)
                intent.putExtra("PLAN_PRICE", price)
                
                startActivity(intent)
                overridePendingTransition(0, 0)
            } else {
                Toast.makeText(this, "Please select a plan first", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateSelection(plan: String) {
        selectedPlan = plan

        val cardYearly = findViewById<ConstraintLayout>(R.id.cl_plan_yearly)
        val cardMonthly = findViewById<ConstraintLayout>(R.id.cl_plan_monthly)
        val ivRadioYearly = findViewById<ImageView>(R.id.iv_radio_yearly)
        val ivRadioMonthly = findViewById<ImageView>(R.id.iv_radio_monthly)
        val tvYearlyPer = findViewById<TextView>(R.id.tv_yearly_per)
        val tvYearlyBilled = findViewById<TextView>(R.id.tv_yearly_billed)
        val tvMonthlyPer = findViewById<TextView>(R.id.tv_monthly_per)

        // Using parseColor to avoid "Unresolved reference" errors
        val colorCyan = "#06B6D4".toColorInt()
        val colorGray = "#9E9E9E".toColorInt()

        if (plan == "yearly") {
            // Highlight Yearly
            cardYearly.setBackgroundResource(R.drawable.bg_plan_card_selected)
            ivRadioYearly.setImageResource(R.drawable.ic_radio_selected_cyan)
            tvYearlyPer.setTextColor(colorCyan)
            tvYearlyBilled.setTextColor(colorCyan)

            // Unhighlight Monthly
            cardMonthly.setBackgroundResource(R.drawable.bg_plan_card_unselected)
            ivRadioMonthly.setImageResource(R.drawable.ic_radio_unselected)
            tvMonthlyPer.setTextColor(colorGray)
        } else {
            // Highlight Monthly
            cardMonthly.setBackgroundResource(R.drawable.bg_plan_card_selected)
            ivRadioMonthly.setImageResource(R.drawable.ic_radio_selected_cyan)
            tvMonthlyPer.setTextColor(colorCyan)

            // Unhighlight Yearly
            cardYearly.setBackgroundResource(R.drawable.bg_plan_card_unselected)
            ivRadioYearly.setImageResource(R.drawable.ic_radio_unselected)
            tvYearlyPer.setTextColor(colorGray)
            tvYearlyBilled.setTextColor(colorGray)
        }
    }

    private fun navigateBack() {
        finish()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            overrideActivityTransition(Activity.OVERRIDE_TRANSITION_OPEN, 0, 0)
        } else {
            @Suppress("DEPRECATION")
            overridePendingTransition(0, 0)
        }
    }
}