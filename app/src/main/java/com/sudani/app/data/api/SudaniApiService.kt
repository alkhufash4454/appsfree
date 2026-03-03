package com.sudani.app.data.api

import com.sudani.app.data.model.*
import retrofit2.Response
import retrofit2.http.*

/**
 * الواجهة البرمجية لتطبيق الخفاش 🦇
 * تم ضبطها لتكون مرآة لمحرك البوت الأصلي لضمان استجابة السيرفر الصحيحة
 */
interface SudaniApiService {
    
    // 1. طلب رمز التحقق (OTP)
    @POST("sc-onboarding/api/customer/generate-otp")
    suspend fun generateOtp(
        @HeaderMap headers: Map<String, String>,
        @Body request: OtpRequest
    ): Response<SudaniResponse<Any>>

    // 2. التحقق من رمز الـ OTP
    @POST("sc-onboarding/api/customer/verify-otp")
    suspend fun verifyOtp(
        @HeaderMap headers: Map<String, String>,
        @Body request: VerifyOtpRequest
    ): Response<SudaniResponse<Any>>

    // 3. إكمال عملية التسجيل والحصول على التوكن (Onboarding)
    @POST("sc-onboarding/api/customer/onboarding")
    suspend fun completeOnboarding(
        @HeaderMap headers: Map<String, String>,
        @Body request: VerifyOtpRequest
    ): Response<SudaniResponse<OnboardingData>>

    // 4. جلب بيانات الداشبورد
    @POST("sc-dashboard/api/get-dashboard")
    suspend fun getDashboard(
        @HeaderMap headers: Map<String, String>,
        @Body payload: Map<String, String>
    ): Response<SudaniResponse<DashboardData>>

    // 5. تجميع نقاط الولاء اليومية
    @POST("gamification-service/api/reward/claim")
    suspend fun claimPoints(
        @HeaderMap headers: Map<String, String>,
        @Body request: ClaimPointsRequest
    ): Response<SudaniResponse<Any>>

    // 6. استبدال النقاط بعروض الداتا
    @POST("offer-service/api/loyalty/redeem-offer-v2")
    suspend fun redeemOffer(
        @HeaderMap headers: Map<String, String>,
        @Body request: RedeemOfferRequest
    ): Response<SudaniResponse<Any>>

    // 7. تفعيل الخدمات والباقات بالرصيد النقدي
    @POST("offer-service/api/catalogue/subscribe")
    suspend fun subscribeService(
        @HeaderMap headers: Map<String, String>,
        @Body request: SubscribeServiceRequest
    ): Response<SudaniResponse<Any>>
}
