package com.sudani.app.data.model

import com.google.gson.annotations.SerializedName

// الحالة العامة للواجهة
sealed class UiState {
    object Idle : UiState()
    object Loading : UiState()
    data class Success(val message: String) : UiState()
    data class Error(val message: String) : UiState()
}

// تعريف الخدمة (الباقات)
data class ServiceOffering(
    val offeringId: String,
    val name: String,
    val category: String,
    val price: String,
    val voiceMinutes: String,
    val dataMb: String,
    val productId: String
)

data class SudaniResponse<T>(
    @SerializedName("responseCode") val responseCode: String,
    @SerializedName("responseMessage") val responseMessage: String?,
    @SerializedName("data") val data: T?
)

data class DashboardData(
    @SerializedName("customerName") val customerName: String?,
    @SerializedName("balance") val balance: Any?, 
    @SerializedName("totalLoyaltyPoints") val totalLoyaltyPoints: String?,
    @SerializedName("freeUnits") val freeUnits: List<FreeUnit>?,
    @SerializedName("subscriberId") val subscriberId: String?
)

data class FreeUnit(
    @SerializedName("unitName") val unitName: String?,
    @SerializedName("currentAmount") val currentAmount: String?,
    @SerializedName("totalAmount") val totalAmount: String?,
    @SerializedName("measureUnit") val measureUnit: String?
)

data class OnboardingData(
    @SerializedName("token") val token: String?,
    @SerializedName("subscriberId") val subscriberId: String?,
    @SerializedName("customerId") val customerId: String?,
    @SerializedName("primaryOfferName") val primaryOfferName: String?
)

data class OtpRequest(val msisdn: String, val primaryMsisdn: String)
data class VerifyOtpRequest(val msisdn: String, val primaryMsisdn: String, val otp: String)
data class ClaimPointsRequest(@SerializedName("Current-loyalty-points") val currentPoints: String)
data class RedeemOfferRequest(
    val offerId: String, val productId: String, val loyaltyPoints: String,
    @SerializedName("Current-loyalty-points") val currentPoints: String,
    val resources: List<OfferResource>
)
data class OfferResource(val key: String, val value: String, val label: String, val unit: String)
data class SubscribeServiceRequest(val offerId: String, @SerializedName("product-price") val price: String)
