package com.example.speakometerfrontend.network

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Part
import retrofit2.http.Body

interface VoiceApiService {

    // Expects the Python `voice_engine.py` /analyze endpoint
    @Multipart
    @POST("api/analyze")
    suspend fun analyzeSpeech(
        @Part file: MultipartBody.Part
    ): Response<AnalyzeResponse>

    // Fetch user's session history for profile stats
    @GET("api/sessions/{userId}")
    suspend fun getSessions(@Path("userId") userId: Int): Response<SessionsResponse>

    // Save user's session history to update stats automatically
    @POST("api/save-session")
    suspend fun saveSession(@Body request: SaveSessionRequest): Response<SaveSessionResponse>

    // Persistent Profile Updates (Issue 3)
    @retrofit2.http.PUT("api/profile")
    suspend fun updateProfile(@Body request: ProfileUpdateRequest): Response<GenericResponse>

}
