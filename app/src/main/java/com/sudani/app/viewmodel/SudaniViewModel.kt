package com.sudani.app.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sudani.app.data.model.DashboardData
import com.sudani.app.data.repository.SudaniRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class UiState {
    object Idle : UiState()
    object Loading : UiState()
    data class Success(val message: String) : UiState()
    data class Error(val message: String) : UiState()
}

class SudaniViewModel : ViewModel() {
    private val repository = SudaniRepository()

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState = _uiState.asStateFlow()

    var msisdn by mutableStateOf("")
    var otp by mutableStateOf("")
    var dashboardData by mutableStateOf<DashboardData?>(null)
    var isLoggedIn by mutableStateOf(false)
    var isOtpSent by mutableStateOf(false)

    private var token: String? = null
    private var subscriberId: String? = null

    fun sendOtp() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val response = repository.generateOtp(msisdn)
                if (response.isSuccessful && response.body()?.responseCode == "200") {
                    isOtpSent = true
                    _uiState.value = UiState.Success("تم إرسال رمز التحقق بنجاح")
                } else {
                    _uiState.value = UiState.Error(response.body()?.responseMessage ?: "فشل إرسال الرمز")
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error("خطأ في الاتصال: ${e.message}")
            }
        }
    }

    fun verifyOtp() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val response = repository.verifyOtp(msisdn, otp)
                if (response.isSuccessful && response.body()?.responseCode == "200") {
                    val data = response.body()?.data
                    dashboardData = data
                    subscriberId = data?.subscriberId
                    // In a real app, the token comes from headers or a specific field
                    // For this mock/re-implementation, we'll assume it's successful
                    isLoggedIn = true
                    _uiState.value = UiState.Success("تم تسجيل الدخول بنجاح")
                    fetchDashboard()
                } else {
                    _uiState.value = UiState.Error(response.body()?.responseMessage ?: "رمز التحقق غير صحيح")
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error("خطأ في الاتصال: ${e.message}")
            }
        }
    }

    fun fetchDashboard() {
        if (msisdn.isEmpty() || subscriberId == null) return
        viewModelScope.launch {
            try {
                val response = repository.getDashboard(msisdn, token ?: "", subscriberId!!)
                if (response.isSuccessful && response.body()?.responseCode == "200") {
                    dashboardData = response.body()?.data
                }
            } catch (e: Exception) {
                // Silently fail or log
            }
        }
    }

    fun claimPoints() {
        val currentPoints = dashboardData?.totalLoyaltyPoints ?: "0"
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val response = repository.claimPoints(msisdn, token ?: "", currentPoints)
                if (response.isSuccessful && response.body()?.responseCode == "200") {
                    _uiState.value = UiState.Success("تم تجميع النقاط بنجاح")
                    fetchDashboard()
                } else {
                    _uiState.value = UiState.Error(response.body()?.responseMessage ?: "فشل تجميع النقاط")
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error("خطأ في الاتصال: ${e.message}")
            }
        }
    }

    fun activateOffer(offerType: String) {
        val (offerId, productId, points, volume) = when (offerType) {
            "300MB" -> listOf("320196", "2002", "70", "300")
            "1GB" -> listOf("320197", "2023", "100", "1024")
            else -> return
        }

        val currentPoints = dashboardData?.totalLoyaltyPoints ?: "0"
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val response = repository.redeemOffer(
                    msisdn, token ?: "", currentPoints, offerId, productId, points, volume
                )
                if (response.isSuccessful && response.body()?.responseCode == "200") {
                    _uiState.value = UiState.Success("تم تفعيل العرض بنجاح")
                    fetchDashboard()
                } else {
                    _uiState.value = UiState.Error(response.body()?.responseMessage ?: "فشل تفعيل العرض")
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error("خطأ في الاتصال: ${e.message}")
            }
        }
    }
}
