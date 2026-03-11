package com.example.speakometerfrontend

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import java.text.SimpleDateFormat
import java.util.*

class WelcomePremiumActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome_to_premium)

        // 1. Initialize Views
        val tvValuePlan = findViewById<TextView>(R.id.tv_value_plan)
        val tvValueAmount = findViewById<TextView>(R.id.tv_value_amount)
        val tvValueTrial = findViewById<TextView>(R.id.tv_value_trial)
        val tvValueBilling = findViewById<TextView>(R.id.tv_value_billing)
        val btnExplore = findViewById<AppCompatButton>(R.id.btn_explore)

        // 2. Retrieve Data passed from PremiumPaymentActivity
        val isFreeTrial = intent.getBooleanExtra("IS_FREE_TRIAL", false)
        val planType = intent.getStringExtra("PLAN_TYPE") ?: "Yearly"
        val planPrice = intent.getStringExtra("PLAN_PRICE") ?: "1199"

        // 3. Date Calculation Logic (Current Date)
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

        if (isFreeTrial) {
            // RULE: If user used a Free Trial, show the specific cancellation warning Toast
            Toast.makeText(this, "You can cancel anytime in the next 7 days. If not, you will be billed after the trial period.", Toast.LENGTH_LONG).show()

            // Calculate Trial End Date (Today + 7 Days)
            calendar.add(Calendar.DAY_OF_YEAR, 7)
            val trialEndDate = dateFormat.format(calendar.time)

            tvValuePlan.text = planType
            tvValueAmount.text = getString(R.string.payment_zero_due) // Should show ₹0
            tvValueTrial.text = trialEndDate
            tvValueBilling.text = trialEndDate // Billing happens the day trial ends
        } else {
            // RULE: If user paid directly (No Trial)
            val billingDate = dateFormat.format(calendar.time) // Billing is today

            tvValuePlan.text = planType
            tvValueAmount.text = getString(R.string.payment_price_format, "₹", planPrice)
            tvValueTrial.text = getString(R.string.status_activated)
            tvValueBilling.text = billingDate
        }

        // 4. Explore Button Logic
        btnExplore.setOnClickListener {
            // Navigate to Home Page and clear the activity stack so they can't "Go Back" to payment
            val intent = Intent(this, HomePageActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}