package com.sudani.app.data.model

import com.google.gson.annotations.SerializedName

// الموديل العام للردود
data class SudaniResponse<T>(
    @SerializedName("responseCode") val responseCode: String,
    @SerializedName("responseMessage") val responseMessage: String?,
    @SerializedName("data") val data: T?
)

// بيانات الداشبورد المحدثة
data class DashboardData(
    @SerializedName("subscriberId") val subscriberId: String?,
    @SerializedName("customerName") val customerName: String?,
    // تعديل الرصيد ليكون كائناً كما في رد السيرفر الحقيقي
    @SerializedName("balance") val balance: Any?, 
    @SerializedName("totalLoyaltyPoints") val totalLoyaltyPoints: String?,
    @SerializedName("activeOffers") val activeOffers: List<ActiveOffer>?,
    @SerializedName("freeUnits") val freeUnits: List<FreeUnit>?,
    @SerializedName("totalUsage") val totalUsage: String?,
    @SerializedName("lastTopUpDate") val lastTopUpDate: String?,
    @SerializedName("lastTopUpAmount") val lastTopUpAmount: String?
)

// بيانات الوحدات المجانية (الباقات النشطة بالتفصيل)
data class FreeUnit(
    @SerializedName("unitName") val unitName: String?,
    @SerializedName("currentAmount") val currentAmount: String?,
    @SerializedName("totalAmount") val totalAmount: String?,
    @SerializedName("measureUnit") val measureUnit: String?
)

data class ActiveOffer(
    @SerializedName("offerName") val offerName: String?,
    @SerializedName("expiryDate") val expiryDate: String?,
    @SerializedName("remainingVolume") val remainingVolume: String?
)

// بيانات الدخول الكاملة
data class OnboardingData(
    @SerializedName("token") val token: String?,
    @SerializedName("subscriberId") val subscriberId: String?,
    @SerializedName("customerId") val customerId: String?,
    @SerializedName("primaryOfferName") val primaryOfferName: String?,
    @SerializedName("firstName") val firstName: String?,
    @SerializedName("lastName") val lastName: String?,
    @SerializedName("creationTime") val creationTime: String?,
    @SerializedName("subscriberType") val subscriberType: String?,
    @SerializedName("pin1") val pin1: String?,
    @SerializedName("puk1") val puk1: String?
)

// موديلات الطلبات (Requests)
data class OtpRequest(
    val msisdn: String,
    val primaryMsisdn: String,
    val email: String = "",
    val method: String = "SMS",
    val useCase: String = "ONBOARDING",
    val platform: String = "android",
    val language: String = "en"
)

data class VerifyOtpRequest(
    val msisdn: String,
    val primaryMsisdn: String,
    val otp: String,
    val method: String = "SMS",
    val useCase: String = "ONBOARDING",
    val channel: String = "sc_app",
    val transactionToken: String = "abc",
    val platform: String = "android",
    val language: String = "en"
)

data class ClaimPointsRequest(
    @SerializedName("Current-loyalty-points") val currentPoints: String,
    val milestone: String = "NO",
    val milestoneIdentifier: String = "1"
)

data class RedeemOfferRequest(
    val age: String = "1",
    val offerId: String,
    val productId: String,
    val loyaltyPoints: String,
    @SerializedName("Current-loyalty-points") val currentPoints: String,
    val chosenReward: String = "Referral Gift",
    val resources: List<OfferResource>,
    val rewardTypes: String = "On Net Mins,SMS,MB"
)

data class OfferResource(
    val key: String,
    val value: String,
    val label: String,
    val unit: String
)

// الموديل الجديد للاشتراك في الخدمات بالرصيد
data class SubscribeServiceRequest(
    val offerId: String,
    @SerializedName("product-category") val productCategory: String,
    @SerializedName("product-price") val productPrice: String,
    @SerializedName("product-name") val productName: String,
    @SerializedName("product-id") val productId: String,
    val typeoftransaction: String = "subscription"
)
