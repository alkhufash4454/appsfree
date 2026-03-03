package com.sudani.app.data.model

import com.google.gson.annotations.SerializedName

data class SudaniResponse<T>(
    @SerializedName("responseCode") val responseCode: String,
    @SerializedName("responseMessage") val responseMessage: String?,
    @SerializedName("data") val data: T?
)

data class DashboardData(
    @SerializedName("subscriberId") val subscriberId: String?,
    @SerializedName("customerName") val customerName: String?,
    @SerializedName("balance") val balance: Any?, 
    @SerializedName("totalLoyaltyPoints") val totalLoyaltyPoints: String?,
    @SerializedName("freeUnits") val freeUnits: List<FreeUnit>?,
    @SerializedName("activeOffers") val activeOffers: List<ActiveOffer>?
)

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

data class OnboardingData(
    @SerializedName("token") val token: String?,
    @SerializedName("subscriberId") val subscriberId: String?,
    @SerializedName("customerId") val customerId: String?,
    @SerializedName("primaryOfferName") val primaryOfferName: String?,
    @SerializedName("firstName") val firstName: String?,
    @SerializedName("lastName") val lastName: String?
)

data class OtpRequest(val msisdn: String, val primaryMsisdn: String)
data class VerifyOtpRequest(val msisdn: String, val primaryMsisdn: String, val otp: String)
data class ClaimPointsRequest(@SerializedName("Current-loyalty-points") val currentPoints: String)

data class RedeemOfferRequest(
    val offerId: String,
    val productId: String,
    val loyaltyPoints: String,
    @SerializedName("Current-loyalty-points") val currentPoints: String,
    val resources: List<OfferResource>
)

data class OfferResource(val key: String, val value: String, val label: String, val unit: String)

data class SubscribeServiceRequest(
    val offerId: String,
    @SerializedName("product-category") val productCategory: String,
    @SerializedName("product-price") val productPrice: String,
    @SerializedName("product-name") val productName: String,
    @SerializedName("product-id") val productId: String
)
