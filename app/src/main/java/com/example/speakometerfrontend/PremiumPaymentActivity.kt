package com.example.speakometerfrontend

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton

class PremiumPaymentActivity : AppCompatActivity() {

    private lateinit var tvTotalDue: TextView
    private lateinit var tvAfterLabel: TextView
    private lateinit var tvAfterPrice: TextView
    private lateinit var etCardNumber: EditText
    private lateinit var etExpiry: EditText
    private lateinit var etCvc: EditText
    private lateinit var btnSubmit: AppCompatButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_premium_payment)

        // Initialize Views
        val btnBack: ImageButton = findViewById(R.id.btn_back)
        tvTotalDue = findViewById(R.id.tv_total_due)
        tvAfterLabel = findViewById(R.id.tv_after_label)
        tvAfterPrice = findViewById(R.id.tv_after_price)
        etCardNumber = findViewById(R.id.et_card_number)
        etExpiry = findViewById(R.id.et_expiry)
        etCvc = findViewById(R.id.et_cvc)
        btnSubmit = findViewById(R.id.btn_submit)

        btnBack.setOnClickListener { finish() }

        // Logic to automatically add spaces to card number (Visual Polish)
        setupCardFormatter()

        // Retrieve the data passed from the previous screens
        val isFreeTrial = intent.getBooleanExtra("IS_FREE_TRIAL", false)
        val planType = intent.getStringExtra("PLAN_TYPE") ?: "Yearly"
        val planPrice = intent.getStringExtra("PLAN_PRICE") ?: "1199"

        setupPricingDetails(isFreeTrial, planType, planPrice)

        btnSubmit.setOnClickListener {
            validateAndProcessPayment(isFreeTrial, planType, planPrice)
        }
    }

    private fun validateAndProcessPayment(isFreeTrial: Boolean, planType: String, planPrice: String) {
        // Remove spaces for logic check
        val cardNumber = etCardNumber.text.toString().trim().replace(" ", "")
        val expiry = etExpiry.text.toString().trim()
        val cvc = etCvc.text.toString().trim()

        // 1. Basic UI Validation
        if (cardNumber.length < 16) {
            etCardNumber.error = "Invalid Card Number"
            return
        }

        if (!expiry.contains("/") || expiry.length < 5) {
            etExpiry.error = "Use MM/YY"
            return
        }

        if (cvc.length < 3) {
            etCvc.error = "Invalid CVC"
            return
        }

        // 2. Success Path: Show "Actual" Gateway Loading
        Toast.makeText(this, "Connecting to secure gateway...", Toast.LENGTH_SHORT).show()

        // Disable UI during processing
        btnSubmit.isEnabled = false
        btnSubmit.alpha = 0.5f

        // 3. Simulate a 2.5-second bank processing delay
        Handler(Looper.getMainLooper()).postDelayed({

            // Use the "Mock Gateway Engine" to decide the result
            val bankResponse = mockBankResponse(cardNumber, cvc)

            // Inside PremiumPaymentActivity.kt (within the Handler postDelayed)
            if (bankResponse == "SUCCESS") {
                // Change WelcomePremiumActivity to PremiumAccessActivity
                val welcomeIntent = Intent(this, PremiumAccessActivity::class.java)
                welcomeIntent.putExtra("IS_FREE_TRIAL", isFreeTrial)
                welcomeIntent.putExtra("PLAN_TYPE", planType)
                welcomeIntent.putExtra("PLAN_PRICE", planPrice)

                startActivity(welcomeIntent)
                finish()
            } else {
                // Handle Bank Failures (Funds, Wrong Details, etc)
                btnSubmit.isEnabled = true
                btnSubmit.alpha = 1.0f
                Toast.makeText(this, "Payment Failed: $bankResponse", Toast.LENGTH_LONG).show()
            }

        }, 2500)
    }

    /**
     * LOCAL GATEWAY ENGINE
     * Mimics real bank logic without needing an API key
     */
    private fun mockBankResponse(cardNumber: String, cvc: String): String {
        return when {
            // Failure Rule 1: Incorrect CVC simulation
            cvc == "000" -> "Security code (CVC) check failed."

            // Failure Rule 2: Insufficient Funds simulation (ends in 0000)
            cardNumber.endsWith("0000") -> "Insufficient funds in bank account."

            // Failure Rule 3: Expired Card simulation (starts with 5)
            cardNumber.startsWith("5") -> "This card has expired."

            // Success Rule: Standard Visa (4) or Mastercard (2)
            cardNumber.startsWith("4") || cardNumber.startsWith("2") -> "SUCCESS"

            // Default
            else -> "Transaction declined by issuing bank."
        }
    }

    private fun setupPricingDetails(isFreeTrial: Boolean, planType: String, planPrice: String) {
        val currencySymbol = "₹"
        val suffix = if (planType.equals("yearly", ignoreCase = true)) "/year" else "/month"

        if (isFreeTrial) {
            tvTotalDue.text = getString(R.string.payment_zero_due)
            tvAfterLabel.text = getString(R.string.payment_after_trial)
            tvAfterPrice.text = getString(R.string.payment_price_suffix_format, currencySymbol, planPrice, suffix)
        } else {
            tvTotalDue.text = getString(R.string.payment_price_format, currencySymbol, planPrice)
            tvAfterLabel.text = getString(R.string.payment_plan_label)
            tvAfterPrice.text = planType
        }
    }

    private fun setupCardFormatter() {
        etCardNumber.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (!s.isNullOrEmpty() && (s.length % 5 == 0)) {
                    val c = s[s.length - 1]
                    if (' ' == c) {
                        s.delete(s.length - 1, s.length)
                    }
                    if (Character.isDigit(c) && TextUtils.split(s.toString(), " ").size <= 3) {
                        s.insert(s.length - 1, " ")
                    }
                }
            }
        })
    }
}