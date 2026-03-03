package com.sudani.app.data.repository

import com.sudani.app.data.api.RetrofitClient
import com.sudani.app.data.model.*
import java.text.SimpleDateFormat
import java.util.*

class SudaniRepository {
    private val api = RetrofitClient.instance

    suspend fun generateOtp(msisdn: String) = api.generateOtp(mapOf("msisdn" to msisdn), OtpRequest(msisdn, msisdn))
    suspend fun completeOnboarding(msisdn: String, otp: String) = api.completeOnboarding(mapOf("msisdn" to msisdn), VerifyOtpRequest(msisdn, msisdn, otp))
    
    suspend fun claimPoints(msisdn: String, token: String, userData: OnboardingData, points: String) = 
        api.claimPoints(mapOf("x-auth-token" to token), ClaimPointsRequest(points))

    // تأكد من تسمية الباراميتر points ليتوافق مع الـ Worker
    suspend fun getDashboard(msisdn: String, token: String, userData: OnboardingData) = 
        api.getDashboard(mapOf("x-auth-token" to token), mapOf("subscriberId" to (userData.subscriberId ?: "")))
}
