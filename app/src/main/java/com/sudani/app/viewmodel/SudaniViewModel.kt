package com.sudani.app.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sudani.app.data.model.DashboardData
import com.sudani.app.data.model.OnboardingData
import com.sudani.app.data.repository.SudaniRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class UiState {
    object Idle : UiState()
    object Loading : UiState()
    data class Success(val message: String) : UiState()
    data class Error(val message: String) : UiState()
}

// كلاس لتمثيل الخدمة في Kotlin
data class ServiceOffering(
    val offeringId: String,
    val name: String,
    val category: String,
    val price: String,
    val voiceMinutes: String,
    val dataMb: String,
    val productId: String
)

class SudaniViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = SudaniRepository()
    private val sharedPrefs = application.getSharedPreferences("khufash_prefs", android.content.Context.MODE_PRIVATE)
    private val gson = Gson()

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState = _uiState.asStateFlow()

    // بيانات الحسابات
    var savedAccounts by mutableStateOf<List<OnboardingData>>(emptyList())
    var msisdn by mutableStateOf("")
    var otp by mutableStateOf("")
    var dashboardData by mutableStateOf<DashboardData?>(null)
    var isLoggedIn by mutableStateOf(false)
    var isOtpSent by mutableStateOf(false)
    var token: String? = null
    var subscriberId: String? = null

    // --- قائمة الخدمات (SERVICE_OFFERINGS) ---
    val allServices = listOf(
        // Mixed
        ServiceOffering("243586", "Ahla Youm", "Mixed", "2000", "100", "300", "1570"),
        ServiceOffering("237602", "Raih Balak", "Mixed", "1000", "50", "100", "1699"),
        ServiceOffering("238884", "Raih Balak Max", "Mixed", "28000", "1000", "20480", "1762"),
        ServiceOffering("240891", "Raih Balak", "Mixed", "5200", "500", "1024", "1612"),
        ServiceOffering("238883", "Raih Balak", "Mixed", "18500", "0", "5120", "1763"),
        // Voice
        ServiceOffering("231232", "Khalli Anak", "Voice", "3700", "300", "0", "1606"),
        ServiceOffering("243979", "Khalli Anak", "Voice", "1500", "45", "0", "1603"),
        ServiceOffering("243980", "Khalli Anak", "Voice", "14000", "1000", "0", "1607"),
        // Data
        ServiceOffering("244340", "5GB", "Data", "8500", "0", "5120", "2001"),
        ServiceOffering("244341", "10GB", "Data", "12500", "0", "10240", "2002"),
        ServiceOffering("244441", "5GB", "Data", "14000", "0", "5120", "2003"),
        ServiceOffering("244448", "500MB", "Data", "1700", "0", "500", "2004"),
        ServiceOffering("239806", "200MB", "Data", "1700", "0", "200", "2005"),
        ServiceOffering("244449", "1.5GB", "Data", "2500", "0", "1536", "2006"),
        ServiceOffering("239706", "1GB", "Data", "10304", "0", "1024", "2007")
    )

    init {
        loadAccounts()
    }

    // --- إدارة الحسابات والـ OTP ---

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
                val verifyResponse = repository.verifyOtp(msisdn, otp)
                if (verifyResponse.isSuccessful && verifyResponse.body()?.responseCode == "200") {
                    val onboardResponse = repository.completeOnboarding(msisdn, otp)
                    if (onboardResponse.isSuccessful && onboardResponse.body()?.responseCode == "200") {
                        val data = onboardResponse.body()?.data
                        if (data != null) {
                            token = data.token
                            subscriberId = data.subscriberId
                            saveAccount(data, msisdn)
                            isLoggedIn = true
                            _uiState.value = UiState.Success("تم تسجيل الدخول بنجاح")
                            fetchDashboard()
                        }
                    } else {
                        _uiState.value = UiState.Error("فشل في جلب بيانات التوكن")
                    }
                } else {
                    _uiState.value = UiState.Error(verifyResponse.body()?.responseMessage ?: "رمز التحقق خطأ")
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error("خطأ: ${e.message}")
            }
        }
    }

    // --- العمليات على الداشبورد والخدمات ---

    fun fetchDashboard() {
        if (msisdn.isEmpty() || token == null || subscriberId == null) return
        viewModelScope.launch {
            try {
                val response = repository.getDashboard(msisdn, token!!, subscriberId!!)
                if (response.isSuccessful && response.body()?.responseCode == "200") {
                    dashboardData = response.body()?.data
                }
            } catch (e: Exception) { /* Log error */ }
        }
    }

    fun subscribeToService(offering: ServiceOffering) {
        if (token == null) return
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                // ملاحظة: تحتاج لإضافة دالة subscribeService في الـ Repository
                // استناداً لبيانات البوت: payload = { offerId, product-category, product-price, product-name, product-id }
                _uiState.value = UiState.Success("جاري تفعيل باقة ${offering.name}...")
                // هنا يتم استدعاء الـ API الخاص بالاشتراك
            } catch (e: Exception) {
                _uiState.value = UiState.Error("فشل الاشتراك: ${e.message}")
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
                    _uiState.value = UiState.Error(response.body()?.responseMessage ?: "فشل التجميع")
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error("خطأ في الاتصال")
            }
        }
    }

    // --- إدارة الذاكرة وتعدد الحسابات ---

    private fun saveAccount(data: OnboardingData, phone: String) {
        val currentList = savedAccounts.toMutableList()
        currentList.removeAll { it.customerId == phone }
        if (currentList.size < 10) {
            currentList.add(data.copy(customerId = phone))
        }
        savedAccounts = currentList
        sharedPrefs.edit().putString("accounts_list", gson.toJson(savedAccounts)).apply()
    }

    private fun loadAccounts() {
        val json = sharedPrefs.getString("accounts_list", null)
        if (json != null) {
            val type = object : TypeToken<List<OnboardingData>>() {}.type
            savedAccounts = gson.fromJson(json, type)
        }
    }

    fun switchAccount(account: OnboardingData) {
        msisdn = account.customerId ?: ""
        token = account.token
        subscriberId = account.subscriberId
        isLoggedIn = true
        fetchDashboard()
    }

    fun logout() {
        val currentList = savedAccounts.toMutableList()
        currentList.removeAll { it.customerId == msisdn }
        savedAccounts = currentList
        sharedPrefs.edit().putString("accounts_list", gson.toJson(savedAccounts)).apply()
        isLoggedIn = false
        msisdn = ""
        token = null
        dashboardData = null
    }
}
