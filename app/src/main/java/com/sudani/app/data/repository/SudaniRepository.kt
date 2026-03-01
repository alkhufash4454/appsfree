package com.sudani.app.data.repository

import com.sudani.app.data.api.SudaniApiService
import com.sudani.app.data.model.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SudaniRepository {

    private val BASE_URL = "https://mapp.sudani.sd/prod/"
    private val DEVICE_ID = "ginkgo_xiaomi_ginkgo_Redmi Note 8_Xiaomi_qcom_PKQ1.190616.001"
    private val TENANT = "tec_sudatel"

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    private val api = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(SudaniApiService::class.java)

    private fun getBaseHeaders(msisdn: String? = null): MutableMap<String, String> {
        val headers = mutableMapOf(
            "Content-Type" to "application/json",
            "is-b2b" to "false",
            "accept-encoding" to "gzip",
            "device-id" to DEVICE_ID,
            "tenant" to TENANT,
            "subscriber-type" to "Prepaid",
            "channel" to "sc_app",
            "transaction-token" to "abc",
            "platform" to "android",
            "language" to "en"
        )
        msisdn?.let {
            headers["msisdn"] = it
            headers["primary-msisdn"] = it
        }
        return headers
    }

    suspend fun generateOtp(msisdn: String) = api.generateOtp(
        getBaseHeaders(msisdn),
        OtpRequest(msisdn, msisdn)
    )

    suspend fun verifyOtp(msisdn: String, otp: String) = api.verifyOtp(
        getBaseHeaders(msisdn),
        VerifyOtpRequest(msisdn, msisdn, otp)
    )

    // الدالة الجديدة
    suspend fun completeOnboarding(msisdn: String, otp: String) = api.completeOnboarding(
        getBaseHeaders(msisdn),
        VerifyOtpRequest(msisdn, msisdn, otp)
    )

    suspend fun getDashboard(msisdn: String, token: String, subscriberId: String) = api.getDashboard(
        getBaseHeaders(msisdn).apply {
            put("Authorization", "Bearer $token")
            // إضافة x-auth-selfcare-key كما في البايثون
            put("x-auth-selfcare-key", token) 
        },
        mapOf("subscriberId" to subscriberId)
    )

    suspend fun claimPoints(msisdn: String, token: String, currentPoints: String) = api.claimPoints(
        getBaseHeaders(msisdn).apply {
            put("Authorization", "Bearer $token")
            put("x-auth-selfcare-key", token)
            put("Current-loyalty-points", currentPoints)
        },
        ClaimPointsRequest(currentPoints)
    )

    suspend fun redeemOffer(
        msisdn: String,
        token: String,
        currentPoints: String,
        offerId: String,
        productId: String,
        points: String,
        dataVolume: String
    ) = api.redeemOffer(
        getBaseHeaders(msisdn).apply {
            put("Authorization", "Bearer $token")
            put("x-auth-selfcare-key", token)
            put("Current-loyalty-points", currentPoints)
        },
        RedeemOfferRequest(
            offerId = offerId,
            productId = productId,
            loyaltyPoints = points,
            currentPoints = currentPoints,
            resources = listOf(
                OfferResource("voice", "0", "On Net Mins", "Mins"),
                OfferResource("sms", "0", "SMS", "SMS"),
                OfferResource("data", dataVolume, "MB", "MB")
            )
        )
    )
}
