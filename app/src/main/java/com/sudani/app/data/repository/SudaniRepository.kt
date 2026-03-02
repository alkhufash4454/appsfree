package com.sudani.app.data.repository

import com.sudani.app.data.api.SudaniApiService
import com.sudani.app.data.model.*
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

    // دالة بناء الهيدرز الكاملة لمحاكاة طلبات "الخفاش"
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

        // إضافة بيانات المستخدم الديناميكية لمنع ظهور الأصفار
        userData?.let {
            headers["user-id"] = it.customerId ?: ""
            headers["primary-offer-id"] = it.subscriberId ?: ""
            headers["primary-offer-name"] = it.primaryOfferName ?: "Sudani_agent"
            headers["price-plan"] = it.primaryOfferName ?: "Sudani_agent"
        }

        // إضافة التوكن في الحقل الصحيح كما في البايثون
        token?.let {
            headers["x-auth-selfcare-key"] = it
        }

        headers["current-loyalty-points"] = currentPoints
        return headers
    }

    suspend fun generateOtp(msisdn: String) = api.generateOtp(
        buildFullHeaders(msisdn),
        OtpRequest(msisdn, msisdn)
    )

    suspend fun verifyOtp(msisdn: String, otp: String) = api.verifyOtp(
        buildFullHeaders(msisdn),
        VerifyOtpRequest(msisdn, msisdn, otp)
    )

    suspend fun completeOnboarding(msisdn: String, otp: String) = api.completeOnboarding(
        buildFullHeaders(msisdn),
        VerifyOtpRequest(msisdn, msisdn, otp)
    )

    // جلب الداشبورد بالهيدرز الكاملة
    suspend fun getDashboard(msisdn: String, token: String, userData: OnboardingData) = api.getDashboard(
        buildFullHeaders(msisdn, token, userData),
        mapOf("subscriberId" to (userData.subscriberId ?: ""))
    )

    suspend fun claimPoints(msisdn: String, token: String, userData: OnboardingData, currentPoints: String) = api.claimPoints(
        buildFullHeaders(msisdn, token, userData, currentPoints),
        ClaimPointsRequest(currentPoints)
    )

    // دالة الاشتراك في الخدمات الجديدة بالرصيد
    suspend fun subscribeToService(
        msisdn: String, 
        token: String, 
        userData: OnboardingData, 
        service: ServiceOffering
    ) = api.subscribeService(
        buildFullHeaders(msisdn, token, userData).toMutableMap().apply {
            put("price", service.price)
        },
        mapOf(
            "offerId" to service.offeringId,
            "product-category" to service.category,
            "product-price" to service.price,
            "product-name" to service.name,
            "product-id" to service.productId,
            "typeoftransaction" to "subscription"
        )
    )

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
