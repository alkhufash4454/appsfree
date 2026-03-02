package com.sudani.app.data.model

import com.google.gson.annotations.SerializedName

/**
 * الموديل العام للردود من سيرفر سوداني
 * [span_1](start_span)يغلف كافة البيانات المستلمة مع كود الحالة والرسالة[span_1](end_span)
 */
data class SudaniResponse<T>(
    @SerializedName("responseCode") val responseCode: String,
    @SerializedName("responseMessage") val responseMessage: String?,
    @SerializedName("data") val data: T?
)

/**
 * بيانات لوحة التحكم (Dashboard)
 * [span_2](start_span)تم تحديثها لتشمل الوحدات المجانية (Free Units) لحساب حجم الإنترنت المتبقي[span_2](end_span)
 */
data class DashboardData(
    @SerializedName("subscriberId") val subscriberId: String?,
    @SerializedName("customerName") val customerName: String?,
    [span_3](start_span)// تم ضبطه كـ Any لأن السيرفر يرسله أحياناً كـ String وأحياناً كـ Object[span_3](end_span)
    @SerializedName("balance") val balance: Any?, 
    @SerializedName("totalLoyaltyPoints") val totalLoyaltyPoints: String?,
    @SerializedName("activeOffers") val activeOffers: List<ActiveOffer>?,
    @SerializedName("freeUnits") val freeUnits: List<FreeUnit>?,
    @SerializedName("totalUsage") val totalUsage: String?,
    @SerializedName("lastTopUpDate") val lastTopUpDate: String?,
    @SerializedName("lastTopUpAmount") val lastTopUpAmount: String?
)

/**
 * تفاصيل الباقات النشطة (إنترنت، دقائق، رسائل)
 * [span_4](start_span)[span_5](start_span)تستخدم لرسم "الدائرة الحمراء" وحساب النسب المئوية[span_4](end_span)[span_5](end_span)
 */
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

/**
 * بيانات الجلسة والمشترك الكاملة
 * [span_6](start_span)تخزن في الذاكرة (SharedPreferences) لضمان بقاء 10 أرقام متصلة[span_6](end_span)
 */
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

// --- موديلات الطلبات (Requests) لضمان تطابق الهيدرز مع البوت ---

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

/**
 * طلب تجميع النقاط
 * [span_7](start_span)يستخدم الهيدر الخاص بـ Current-loyalty-points لضمان قبول السيرفر[span_7](end_span)
 */
data class ClaimPointsRequest(
    @SerializedName("Current-loyalty-points") val currentPoints: String,
    val milestone: String = "NO",
    val milestoneIdentifier: String = "1"
)

/**
 * طلب استبدال النقاط (300MB و 1GB)
 * [span_8](start_span)يتطلب مصفوفة موارد (resources) دقيقة جداً لنجاح العملية[span_8](end_span)
 */
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

/**
 * موديل الاشتراك بالرصيد النقدي
 * [span_9](start_span)[span_10](start_span)الحقول هنا مصممة لتعكس رد كود 502 في حال نقص الرصيد[span_9](end_span)[span_10](end_span)
 */
data class SubscribeServiceRequest(
    val offerId: String,
    @SerializedName("product-category") val productCategory: String,
    @SerializedName("product-price") val productPrice: String,
    @SerializedName("product-name") val productName: String,
    @SerializedName("product-id") val productId: String,
    val typeoftransaction: String = "subscription"
)
