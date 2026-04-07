package com.example.speakometerfrontend.network

data class UpgradeRequest(
    val user_id: Int,
    val plan_type: String,
    val is_free_trial: Boolean
)

data class UpgradeResponse(
    val status: String,
    val message: String,
    val expiry_date: String?
)
