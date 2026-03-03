package com.sudani.app.viewmodel

import android.app.Application
import androidx.compose.runtime.*
import androidx.lifecycle.*
import com.sudani.app.data.model.*
import com.sudani.app.data.repository.SudaniRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SudaniViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = SudaniRepository()
    
    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState = _uiState.asStateFlow()

    // المتغيرات المطلوبة في MainActivity والواجهات
    var isLoggedIn by mutableStateOf(false)
    var isOtpSent by mutableStateOf(false)
    var msisdn by mutableStateOf("")
    var otp by mutableStateOf("")
    var token: String? = null
    var fullUserData by mutableStateOf<OnboardingData?>(null)
    var dashboardData by mutableStateOf<DashboardData?>(null)
    var savedAccounts by mutableStateOf<List<OnboardingData>>(emptyList())

    val allServices = listOf(
        ServiceOffering("243586", "Ahla Youm", "Mixed", "2000", "100", "300", "1570"),
        ServiceOffering("244341", "10GB", "Data", "12500", "0", "10240", "2002")
    )

    fun sendOtp() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val res = repository.generateOtp(msisdn)
            if (res.isSuccessful) isOtpSent = true
            _uiState.value = UiState.Idle
        }
    }

    fun verifyOtp() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val res = repository.completeOnboarding(msisdn, otp)
            if (res.isSuccessful) {
                fullUserData = res.body()?.data
                token = fullUserData?.token
                isLoggedIn = true
            }
            _uiState.value = UiState.Idle
        }
    }

    fun switchAccount(account: OnboardingData) {
        fullUserData = account
        msisdn = account.customerId ?: ""
        token = account.token
        isLoggedIn = true
    }

    fun logout() { isLoggedIn = false }
}
