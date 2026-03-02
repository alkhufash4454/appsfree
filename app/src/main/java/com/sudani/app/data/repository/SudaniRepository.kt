package com.sudani.app.data.repository

import com.sudani.app.data.api.SudaniApiService
import com.sudani.app.data.model.*
import com.sudani.app.viewmodel.ServiceOffering
import java.text.SimpleDateFormat
import java.util.*

class SudaniRepository {
    private val api = RetrofitClient.instance // تأكد من ربطه بـ SudaniApiService

    // بناء الهيدرز الحيوية من البوت لضمان جلب البيانات الحقيقية
    private fun buildFullHeaders(msisdn: String, token: String, userData: OnboardingData, currentPoints: String = "0"): Map<String, String> {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US)
        return mapOf(
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
            "channel" to "sc_app",
            "x-auth-selfcare-key" to token,
            "platform" to "android",
            "language" to "ar",
            "host" to "mapp.sudani.sd",
            "sim-preference" to "Primary"
        )
    }

    // طلب استبدال النقاط (خاص بـ 300MB و 1GB) -
    suspend fun redeemPointsOffer(msisdn: String, token: String, userData: OnboardingData, points: String, offerId: String, productId: String, vol: String) =
        api.redeemOffer(
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

    // جلب الداشبورد (تلقائي بالكامل)
    suspend fun getDashboard(msisdn: String, token: String, userData: OnboardingData) = 
        api.getDashboard(buildFullHeaders(msisdn, token, userData), mapOf("subscriberId" to (userData.subscriberId ?: "")))

    suspend fun claimPoints(msisdn: String, token: String, userData: OnboardingData, currentPoints: String) = 
        api.claimPoints(buildFullHeaders(msisdn, token, userData, currentPoints), ClaimPointsRequest(currentPoints))
}
