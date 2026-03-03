package com.sudani.app.data.repository

import com.sudani.app.data.api.RetrofitClient
import com.sudani.app.data.model.*
import com.sudani.app.viewmodel.ServiceOffering
import java.text.SimpleDateFormat
import java.util.*

class SudaniRepository {
    private val api = RetrofitClient.instance

    private fun buildFullHeaders(msisdn: String, token: String, userData: OnboardingData, currentPoints: String = "0", price: String = "0"): Map<String, String> {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US)
        return mapOf(
            "is-b2b" to "false",
            "device-id" to "ginkgo_xiaomi_ginkgo_Redmi Note 8_Xiaomi_qcom_PKQ1.190616.001",
            "primary-msisdn" to msisdn,
            "tenant" to "tec_sudatel",
            "lastlogin" to sdf.format(Date()),
            "user-id" to (userData.customerId ?: ""),
            "msisdn" to msisdn,
            "primary-offer-id" to (userData.subscriberId ?: ""),
            "current-loyalty-points" to currentPoints,
            "price-plan" to (userData.primaryOfferName ?: "Sudani_agent"),
            "price" to price,
            "x-auth-selfcare-key" to token,
            "channel" to "sc_app",
            "platform" to "android"
        )
    }

    suspend fun generateOtp(msisdn: String) = api.generateOtp(mapOf("msisdn" to msisdn), OtpRequest(msisdn, msisdn))
    suspend fun verifyOtp(msisdn: String, otp: String) = api.verifyOtp(mapOf("msisdn" to msisdn), VerifyOtpRequest(msisdn, msisdn, otp))
    suspend fun completeOnboarding(msisdn: String, otp: String) = api.completeOnboarding(mapOf("msisdn" to msisdn), VerifyOtpRequest(msisdn, msisdn, otp))
    suspend fun getDashboard(msisdn: String, token: String, userData: OnboardingData) = api.getDashboard(buildFullHeaders(msisdn, token, userData), mapOf("subscriberId" to (userData.subscriberId ?: "")))
    suspend fun claimPoints(msisdn: String, token: String, userData: OnboardingData, points: String) = api.claimPoints(buildFullHeaders(msisdn, token, userData, currentPoints = points), ClaimPointsRequest(points))
    
    suspend fun redeemPointsOffer(msisdn: String, token: String, userData: OnboardingData, points: String, offerId: String, productId: String, vol: String) = 
        api.redeemOffer(buildFullHeaders(msisdn, token, userData, currentPoints = points), RedeemOfferRequest(offerId, productId, if(offerId=="320196") "70" else "100", points, listOf(OfferResource("data", vol, "MB", "MB"))))

    suspend fun subscribeToService(msisdn: String, token: String, userData: OnboardingData, service: ServiceOffering) = 
        api.subscribeService(buildFullHeaders(msisdn, token, userData, price = service.price), SubscribeServiceRequest(service.offeringId, service.category, service.price, service.name, service.productId))
}
