package com.sudani.app.data.api

import com.sudani.app.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface SudaniApiService {
    
    // طلب رمز التحقق (OTP)
    @POST("sc-onboarding/api/customer/generate-otp")
    suspend fun generateOtp(
        @HeaderMap headers: Map<String, String>,
        @Body request: OtpRequest
    ): Response<SudaniResponse<Any>>

    // التحقق من رمز الـ OTP
    @POST("sc-onboarding/api/customer/verify-otp")
    suspend fun verifyOtp(
        @HeaderMap headers: Map<String, String>,
        @Body request: VerifyOtpRequest
    ): Response<SudaniResponse<Any>>

    // إكمال عملية التسجيل للحصول على التوكن وبيانات المشترك الكاملة
    @POST("sc-onboarding/api/customer/onboarding")
    suspend fun completeOnboarding(
        @HeaderMap headers: Map<String, String>,
        @Body request: VerifyOtpRequest
    ): Response<SudaniResponse<OnboardingData>>

    // جلب بيانات لوحة التحكم (Dashboard) باستخدام الهيدرز الكاملة لمنع ظهور الأصفار
    @POST("sc-dashboard/api/get-dashboard")
    suspend fun getDashboard(
        @HeaderMap headers: Map<String, String>,
        @Body payload: Map<String, String>
    ): Response<SudaniResponse<DashboardData>>

    // تجميع نقاط الولاء اليومية (نظام الخفاش)
    @POST("gamification-service/api/reward/claim")
    suspend fun claimPoints(
        @HeaderMap headers: Map<String, String>,
        @Body request: ClaimPointsRequest
    ): Response<SudaniResponse<Any>>

    // استبدال النقاط بعروض (مثل 300 ميجا و 1 جيجا)
    @POST("offer-service/api/loyalty/redeem-offer-v2")
    suspend fun redeemOffer(
        @HeaderMap headers: Map<String, String>,
        @Body request: RedeemOfferRequest
    ): Response<SudaniResponse<Any>>

    // تفعيل الخدمات والباقات بالرصيد النقدي
    @POST("offer-service/api/catalogue/subscribe")
    suspend fun subscribeService(
        @HeaderMap headers: Map<String, String>,
        @Body request: SubscribeServiceRequest
    ): Response<SudaniResponse<Any>>
}
