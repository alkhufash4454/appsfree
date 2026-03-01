package com.sudani.app.data.api

import com.sudani.app.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface SudaniApiService {
    
    @POST("sc-onboarding/api/customer/generate-otp")
    suspend fun generateOtp(
        @HeaderMap headers: Map<String, String>,
        @Body request: OtpRequest
    ): Response<SudaniResponse<Any>>

    @POST("sc-onboarding/api/customer/verify-otp")
    suspend fun verifyOtp(
        @HeaderMap headers: Map<String, String>,
        @Body request: VerifyOtpRequest
    ): Response<SudaniResponse<Any>> // تعديل هنا

    // المسار الجديد اللي كان ناقص
    @POST("sc-onboarding/api/customer/onboarding")
    suspend fun completeOnboarding(
        @HeaderMap headers: Map<String, String>,
        @Body request: VerifyOtpRequest
    ): Response<SudaniResponse<OnboardingData>>

    @POST("sc-dashboard/api/get-dashboard")
    suspend fun getDashboard(
        @HeaderMap headers: Map<String, String>,
        @Body payload: Map<String, String>
    ): Response<SudaniResponse<DashboardData>>

    @POST("gamification-service/api/reward/claim")
    suspend fun claimPoints(
        @HeaderMap headers: Map<String, String>,
        @Body request: ClaimPointsRequest
    ): Response<SudaniResponse<Any>>

    @POST("offer-service/api/loyalty/redeem-offer-v2")
    suspend fun redeemOffer(
        @HeaderMap headers: Map<String, String>,
        @Body request: RedeemOfferRequest
    ): Response<SudaniResponse<Any>>
}
