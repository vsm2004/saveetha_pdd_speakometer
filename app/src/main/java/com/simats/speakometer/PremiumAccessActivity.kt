package com.simats.speakometer

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class PremiumAccessActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_premium_access)

        // 1. Initialize Views
        val tvValuePlan = findViewById<TextView>(R.id.tv_value_plan)
        val tvValueAmount = findViewById<TextView>(R.id.tv_value_amount)
        val tvValueTrial = findViewById<TextView>(R.id.tv_value_trial)
        val tvValueBilling = findViewById<TextView>(R.id.tv_value_billing)
        val btnExplore = findViewById<AppCompatButton>(R.id.btnExploreFeatures)

        // 2. Retrieve Data from Intent
        val isFreeTrial = intent.getBooleanExtra("IS_FREE_TRIAL", false)
        val planType = intent.getStringExtra("PLAN_TYPE") ?: "Yearly"
        val planPrice = intent.getStringExtra("PLAN_PRICE") ?: "1199"

        // 3. Setup Date Formatter and Calendar
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

        // 4. Handle Dynamic UI Logic
        if (isFreeTrial) {
            // RULE: Show the mandatory trial toast message
            Toast.makeText(
                this,
                "You'll see billing after 7 days and you can cancel it anytime before then.",
                Toast.LENGTH_LONG
            ).show()

            // Calculate Trial End Date (7 days from now)
            calendar.add(Calendar.DAY_OF_YEAR, 7)
            val trialEndDate = dateFormat.format(calendar.time)

            tvValuePlan.text = planType
            tvValueAmount.text = getString(R.string.payment_zero_due) // Shows ₹0
            tvValueTrial.text = trialEndDate
            tvValueBilling.text = trialEndDate // Billing starts when trial ends
        } else {
            // Case: Regular Immediate Purchase
            val billingDate = dateFormat.format(calendar.time) // Billed today

            tvValuePlan.text = planType
            // Using your price format resource: getString(id, currency, amount)
            tvValueAmount.text = getString(R.string.payment_price_format, "₹", planPrice)
            tvValueTrial.text = getString(R.string.status_activated)
            tvValueBilling.text = billingDate
        }

        // 5. Navigation
        btnExplore.setOnClickListener {
            val intent = Intent(this, PremiumAnalyticsActivity::class.java)
            startActivity(intent)
            // We don't necessarily need finish() here if you want them
            // to be able to press back to see their plan details.
        }
    }
}