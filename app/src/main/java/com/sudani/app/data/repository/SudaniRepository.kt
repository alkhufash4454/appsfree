package com.sudani.app.data.repository

import com.sudani.app.data.api.SudaniApiService
import com.sudani.app.data.model.* // استيراد الموديلات بما فيها SubscribeServiceRequest
import com.sudani.app.viewmodel.ServiceOffering // استيراد التعريف لحل Unresolved reference
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*

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

    // بناء الهيدرز الكاملة بناءً على منطق البوت لضمان جلب البيانات الحقيقية
    private fun buildFullHeaders(
        msisdn: String, 
        token: String? = null, 
        userData: OnboardingData? = null, 
        currentPoints: String = "0"
    ): Map<String, String> {
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
            "language" to "en",
            "msisdn" to msisdn,
            "primary-msisdn" to msisdn,
            "host" to "mapp.sudani.sd",
            "sim-preference" to "Primary",
            "lastlogin" to SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US).format(Date())
        )

        userData?.let {
            headers["user-id"] = it.customerId ?: ""
            headers["primary-offer-id"] = it.subscriberId ?: ""
            headers["primary-offer-name"] = it.primaryOfferName ?: "Sudani_agent"
            headers["price-plan"] = it.primaryOfferName ?: "Sudani_agent"
        }

        token?.let { headers["x-auth-selfcare-key"] = it }
        headers["current-loyalty-points"] = currentPoints
        return headers
    }

    suspend fun generateOtp(msisdn: String) = api.generateOtp(buildFullHeaders(msisdn), OtpRequest(msisdn, msisdn))

    suspend fun verifyOtp(msisdn: String, otp: String) = api.verifyOtp(buildFullHeaders(msisdn), VerifyOtpRequest(msisdn, msisdn, otp))

    suspend fun completeOnboarding(msisdn: String, otp: String) = api.completeOnboarding(buildFullHeaders(msisdn), VerifyOtpRequest(msisdn, msisdn, otp))

    suspend fun getDashboard(msisdn: String, token: String, userData: OnboardingData) = 
        api.getDashboard(buildFullHeaders(msisdn, token, userData), mapOf("subscriberId" to (userData.subscriberId ?: "")))

    suspend fun claimPoints(msisdn: String, token: String, userData: OnboardingData, currentPoints: String) = 
        api.claimPoints(buildFullHeaders(msisdn, token, userData, currentPoints), ClaimPointsRequest(currentPoints))

    // حل مشكلة الـ Type mismatch: تحويل الـ Map إلى SubscribeServiceRequest
    suspend fun subscribeToService(
        msisdn: String, 
        token: String, 
        userData: OnboardingData, 
        service: ServiceOffering
    ): retrofit2.Response<SudaniResponse<Any>> {
        val headers = buildFullHeaders(msisdn, token, userData).toMutableMap().apply {
            put("price", service.price)
        }
        
        // بناء الكائن المطلوب بدلاً من الـ Map
        val request = SubscribeServiceRequest(
            offerId = service.offeringId,
            productCategory = service.category,
            productPrice = service.price,
            productName = service.name,
            productId = service.productId,
            typeoftransaction = "subscription"
        )
        
        return api.subscribeService(headers, request)
    }

    suspend fun redeemOffer(
        msisdn: String,
        token: String,
        userData: OnboardingData,
        currentPoints: String,
        offerId: String,
        productId: String,
        points: String,
        dataVolume: String
    ) = api.redeemOffer(
        buildFullHeaders(msisdn, token, userData, currentPoints),
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
