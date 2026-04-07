package com.example.speakometerfrontend.network

import com.google.gson.annotations.SerializedName

// Login Request
data class LoginRequest(
    val email: String,
    val password: String
)

// Signup Request
data class SignupRequest(
    val email: String,
    val password: String,
    val name: String? = null
)

// Generic Response wrapper for User
data class AuthResponse(
    val status: String,
    val message: String,
    val user: UserData? = null,
    @SerializedName("user_id") val userId: Int? = null 
)

// User Data model
data class UserData(
    val id: Int,
    val email: String,
    val name: String?,
    @SerializedName("premium_status") val premiumStatus: Boolean,
    @SerializedName("premium_expiry") val premiumExpiry: String?,
    @SerializedName("last_login") val lastLogin: String?
)
