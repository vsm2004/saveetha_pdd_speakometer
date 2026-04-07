package com.example.speakometerfrontend.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {

    @POST("api/login")
    suspend fun loginUser(@Body request: LoginRequest): Response<AuthResponse>

    @POST("api/signup")
    suspend fun signupUser(@Body request: SignupRequest): Response<AuthResponse>

}
