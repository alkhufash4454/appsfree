package com.sudani.app.data.repository

import com.sudani.app.data.api.RetrofitClient
import com.sudani.app.data.model.*
import com.sudani.app.viewmodel.ServiceOffering
import java.text.SimpleDateFormat
import java.util.*

class SudaniRepository {
    // ربط المحرك بالواجهة البرمجية (ApiService)
    private val api = RetrofitClient.instance

    // بناء الهيدرز "الجذرية" من البوت لضمان استجابة السيرفر بالبيانات الحقيقية
    private fun buildFullHeaders(
        msisdn: String, 
        token: String, 
        userData: OnboardingData, 
        currentPoints: String = "0",
        price: String = "0"
    ): Map<String, String> {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US)
        return mapOf(
            "Content-Type" to "application/json",
            "is-b2b" to "false",
            "accept-encoding" to "gzip",
            "device-id" to "ginkgo_xiaomi_ginkgo_Redmi Note 8_Xiaomi_qcom_PKQ1.190616.001",
            "primary-msisdn" to msisdn,
            "tenant" to "tec_sudatel",
            "subscriber-type" to "Prepaid",
            "lastlogin" to sdf.format(Date()),
            "user-id" to (userData.customerId ?: ""),
            "transaction-token" to "abc",
            "sim-category" to "B2C",
            "msisdn" to msisdn,
            "primary-offer-id" to (userData.subscriberId ?: ""),
            "current-loyalty-points" to currentPoints,
            "price-plan" to (userData.primaryOfferName ?: "Sudani_agent"),
            "primary-offer-name" to (userData.primaryOfferName ?: "Sudani_agent"),
            "price" to price,
            "channel" to "sc_app",
            "x-auth-selfcare-key" to token,
            "platform" to "android",
            "language" to "ar",
            "host" to "mapp.sudani.sd",
            "sim-preference" to "Primary"
        )
    }

    // 1. طلب رمز التحقق (OTP)
    suspend fun generateOtp(msisdn: String) = 
        api.generateOtp(mapOf("msisdn" to msisdn), OtpRequest(msisdn, msisdn))

    // 2. التحقق من الرمز وإكمال التسجيل (Auth)
    suspend fun verifyOtp(msisdn: String, otp: String) = 
        api.verifyOtp(mapOf("msisdn" to msisdn), VerifyOtpRequest(msisdn, msisdn, otp))

    suspend fun completeOnboarding(msisdn: String, otp: String) = 
        api.completeOnboarding(mapOf("msisdn" to msisdn), VerifyOtpRequest(msisdn, msisdn, otp))

    // 3. جلب بيانات الداشبورد (الاسم، الرصيد، النقاط، الباقات)
    suspend fun getDashboard(msisdn: String, token: String, userData: OnboardingData) = 
        api.getDashboard(
            buildFullHeaders(msisdn, token, userData), 
            mapOf("subscriberId" to (userData.subscriberId ?: ""))
        )

    // 4. تجميع نقاط الخفاش (Claim)
    suspend fun claimPoints(msisdn: String, token: String, userData: OnboardingData, points: String) = 
        api.claimPoints(
            buildFullHeaders(msisdn, token, userData, currentPoints = points), 
            ClaimPointsRequest(points)
        )

    // 5. استبدال النقاط (خاص بعروض 300MB و 1GB)
    suspend fun redeemPointsOffer(
        msisdn: String, 
        token: String, 
        userData: OnboardingData, 
        points: String, 
        offerId: String, 
        productId: String, 
        vol: String
    ) = api.redeemOffer(
        buildFullHeaders(msisdn, token, userData, currentPoints = points),
        RedeemOfferRequest(
            offerId = offerId, 
            productId = productId, 
            loyaltyPoints = if(offerId == "320196") "70" else "100", 
            currentPoints = points, 
            resources = listOf(OfferResource("data", vol, "MB", "MB")),
            rewardTypes = "On Net Mins,SMS,MB"
        )
    )

    // 6. الاشتراك في الخدمات بالرصيد (الذي يحدد الـ API كود 502)
    suspend fun subscribeToService(
        msisdn: String, 
        token: String, 
        userData: OnboardingData, 
        service: ServiceOffering
    ) = api.subscribeService(
        buildFullHeaders(msisdn, token, userData, price = service.price),
        SubscribeServiceRequest(
            offerId = service.offeringId,
            productCategory = service.category,
            productPrice = service.price,
            productName = service.name,
            productId = service.productId
        )
    )
}
