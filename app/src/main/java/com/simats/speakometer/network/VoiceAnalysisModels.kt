package com.simats.speakometer.network

import com.google.gson.annotations.SerializedName

data class AnalyzeResponse(
    val status: String,
    val message: String?,
    val transcription: String?,
    val wpm: Double?,
    @SerializedName("tone_analysis") val toneAnalysis: String?,
    @SerializedName("accent_score") val accentScore: Double?,
    @SerializedName("filler_count") val fillerCount: Int?,
    @SerializedName("waveform_url") val waveformUrl: String?,
    @SerializedName("confidence_score") val confidenceScore: Int?
)

data class SessionData(
    val id: Int,
    val score: Int,
    @SerializedName("fillers_count") val fillersCount: Int,
    @SerializedName("stretching_level") val stretchingLevel: String,
    val confidence: Int,
    @SerializedName("created_at") val createdAt: String
)

data class SessionsResponse(
    val status: String,
    @SerializedName("total_sessions") val totalSessions: Int,
    val sessions: List<SessionData>
)

data class SaveSessionRequest(
    @SerializedName("user_id") val userId: Int,
    val score: Int,
    @SerializedName("fillers_count") val fillersCount: Int,
    @SerializedName("stretching_level") val stretchingLevel: String,
    val confidence: Int
)

data class SaveSessionResponse(
    val status: String,
    val message: String,
    @SerializedName("session_id") val sessionId: Int?
)

data class ProfileUpdateRequest(
    @SerializedName("user_id") val userId: Int,
    val name: String? = null,
    val email: String? = null
)

data class GenericResponse(
    val status: String,
    val message: String
)


data class ProfileSyncResponse(
    val status: String,
    val user: UserData
)