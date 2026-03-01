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
    @SerializedName("balance") val balance: String?,
    @SerializedName("totalLoyaltyPoints") val totalLoyaltyPoints: String?,
    @SerializedName("activeOffers") val activeOffers: List<ActiveOffer>?
)

data class ActiveOffer(
    @SerializedName("offerName") val offerName: String?,
    @SerializedName("expiryDate") val expiryDate: String?,
    @SerializedName("remainingVolume") val remainingVolume: String?
)

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
    val resources: List<OfferResource>
)

data class OfferResource(
    val key: String,
    val value: String,
    val label: String,
    val unit: String
)
