package com.sudani.app.data.repository

import com.sudani.app.data.api.RetrofitClient
import com.sudani.app.data.api.SudaniConfig
import com.sudani.app.data.model.*
import retrofit2.Response

class SudaniRepository {
    private val api = RetrofitClient.instance

    private fun getBaseHeaders(): MutableMap<String, String> {
        return mutableMapOf(
            "Content-Type" to "application/json",
            "is-b2b" to "false",
            "accept-encoding" to "gzip",
            "device-id" to SudaniConfig.DEVICE_ID,
            "tenant" to SudaniConfig.TENANT,
            "subscriber-type" to "Prepaid",
            "channel" to "sc_app",
            "transaction-token" to "abc"
        )
    }

    suspend fun generateOtp(msisdn: String): Response<SudaniResponse<Any>> {
        val headers = getBaseHeaders()
        headers["msisdn"] = msisdn
        return api.generateOtp(headers, OtpRequest(msisdn, msisdn))
    }

    suspend fun completeOnboarding(msisdn: String, otp: String): Response<SudaniResponse<OnboardingData>> {
        val headers = getBaseHeaders()
        headers["msisdn"] = msisdn
        return api.completeOnboarding(headers, VerifyOtpRequest(msisdn, msisdn, otp))
    }

    suspend fun getDashboard(msisdn: String, token: String, userData: OnboardingData): Response<SudaniResponse<DashboardData>> {
        val headers = getBaseHeaders()
        headers["x-auth-token"] = token
        headers["msisdn"] = msisdn
        return api.getDashboard(headers, mapOf("subscriberId" to (userData.subscriberId ?: "")))
    }

    suspend fun claimPoints(msisdn: String, token: String, userData: OnboardingData, points: String): Response<SudaniResponse<Any>> {
        val headers = getBaseHeaders()
        headers["x-auth-token"] = token
        headers["msisdn"] = msisdn
        return api.claimPoints(headers, ClaimPointsRequest(points))
    }

    suspend fun redeemOffer(msisdn: String, token: String, offerId: String, productId: String, pointsNeeded: String, currentPoints: String): Response<SudaniResponse<Any>> {
        val headers = getBaseHeaders()
        headers["x-auth-token"] = token
        headers["msisdn"] = msisdn
        
        val resources = listOf(
            OfferResource("loyaltyPoints", pointsNeeded, "Loyalty Points", "Points"),
            OfferResource("chosenReward", "Referral Gift", "Chosen Reward", "")
        )
        
        val request = RedeemOfferRequest(offerId, productId, pointsNeeded, currentPoints, resources)
        return api.redeemOffer(headers, request)
    }

    suspend fun subscribeService(msisdn: String, token: String, offerId: String, price: String): Response<SudaniResponse<Any>> {
        val headers = getBaseHeaders()
        headers["x-auth-token"] = token
        headers["msisdn"] = msisdn
        return api.subscribeService(headers, SubscribeServiceRequest(offerId, price))
    }
}
