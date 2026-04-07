package com.example.speakometerfrontend.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface PremiumApiService {
    @POST("api/upgrade-premium")
    suspend fun upgradePremium(@Body request: UpgradeRequest): Response<UpgradeResponse>
}
