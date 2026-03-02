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

    var savedAccounts by mutableStateOf<List<OnboardingData>>(emptyList())
    var fullUserData by mutableStateOf<OnboardingData?>(null)
    
    var msisdn by mutableStateOf("")
    var otp by mutableStateOf("")
    var dashboardData by mutableStateOf<DashboardData?>(null)
    var isLoggedIn by mutableStateOf(false)
    var isOtpSent by mutableStateOf(false)
    
    var token: String? = null
    var subscriberId: String? = null

    init {
        loadAccounts()
    }

    // --- إدارة الـ OTP والدخول ---

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
                val verifyRes = repository.verifyOtp(msisdn, otp)
                if (verifyRes.isSuccessful && verifyRes.body()?.responseCode == "200") {
                    val onboardRes = repository.completeOnboarding(msisdn, otp)
                    if (onboardRes.isSuccessful && onboardRes.body()?.responseCode == "200") {
                        val data = onboardRes.body()?.data
                        if (data != null) {
                            token = data.token
                            subscriberId = data.subscriberId
                            fullUserData = data
                            saveAccount(data, msisdn)
                            isLoggedIn = true
                            _uiState.value = UiState.Success("تم تسجيل الدخول بنجاح 🦇")
                            fetchDashboard()
                        }
                    }
                } else {
                    _uiState.value = UiState.Error(verifyRes.body()?.responseMessage ?: "رمز التحقق خطأ")
                }
            } catch (e: Exception) { _uiState.value = UiState.Error("خطأ: ${e.message}") }
        }
    }

    // --- العمليات الحيوية (تلقائية بالكامل من ردود السيرفر) ---

    fun fetchDashboard() {
        val userData = fullUserData ?: return
        if (msisdn.isEmpty() || token == null) return
        
        viewModelScope.launch {
            try {
                // جلب البيانات بالهيدرز الكاملة لضمان عرض الاسم والرصيد الحقيقي
                val response = repository.getDashboard(msisdn, token!!, userData)
                if (response.isSuccessful && response.body()?.responseCode == "200") {
                    dashboardData = response.body()?.data
                }
            } catch (e: Exception) { }
        }
    }

    // تفعيل عروض النقاط (300MB و 1GB) - تعتمد على رد السيرفر حصراً
    fun activateKhufashOffer(type: String) {
        val userData = fullUserData ?: return
        // معالجة النقاط: تحويل 1056.0 إلى 1056 لتجنب رفض السيرفر
        val ptsRaw = dashboardData?.totalLoyaltyPoints ?: "0"
        val pts = ptsRaw.split(".")[0]

        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val response = if (type == "300mb") {
                    repository.redeemPointsOffer(msisdn, token!!, userData, pts, "320196", "2002", "300")
                } else {
                    repository.redeemPointsOffer(msisdn, token!!, userData, pts, "320197", "2023", "1024")
                }

                if (response.isSuccessful && response.body()?.responseCode == "200") {
                    _uiState.value = UiState.Success("تم تفعيل العرض بنجاح 🦇")
                    fetchDashboard() // تحديث البيانات التلقائي
                } else {
                    _uiState.value = UiState.Error(response.body()?.responseMessage ?: "فشل التفعيل")
                }
            } catch (e: Exception) { _uiState.value = UiState.Error("خطأ في الشبكة") }
        }
    }

    // تجميع النقاط اليدوي - يعرض رد السيرفر المباشر
    fun claimPointsManual() {
        val userData = fullUserData ?: return
        val ptsRaw = dashboardData?.totalLoyaltyPoints ?: "0"
        val pts = ptsRaw.split(".")[0]

        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val response = repository.claimPoints(msisdn, token!!, userData, pts)
                val body = response.body()
                if (response.isSuccessful && body?.responseCode == "200") {
                    _uiState.value = UiState.Success("تم تجميع النقاط بنجاح 🦇")
                    fetchDashboard()
                } else {
                    // عرض الرد الحقيقي: "أخذتها مسبقاً" أو غيره
                    _uiState.value = UiState.Error(body?.responseMessage ?: "فشل الطلب")
                }
            } catch (e: Exception) { _uiState.value = UiState.Error("خطأ في الاتصال") }
        }
    }

    // الاشتراك بالرصيد (منفصل تماماً، يحدد الـ API كود 502 عند نقص الرصيد)
    fun subscribeWithBalance(service: ServiceOffering) {
        val userData = fullUserData ?: return
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val res = repository.subscribeToService(msisdn, token!!, userData, service)
                if (res.code() == 502 || res.body()?.responseCode == "520") {
                    _uiState.value = UiState.Error("❌ رصيد نقدي غير كافي (502)")
                } else if (res.isSuccessful && res.body()?.responseCode == "200") {
                    _uiState.value = UiState.Success("✅ تم الاشتراك في ${service.name} بنجاح")
                    fetchDashboard()
                } else {
                    _uiState.value = UiState.Error(res.body()?.responseMessage ?: "فشل العملية")
                }
            } catch (e: Exception) { _uiState.value = UiState.Error("خطأ في السيرفر") }
        }
    }

    // --- إدارة الذاكرة وتعدد الحسابات (حفظ واستعادة الجلسة الكاملة) ---

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
        fullUserData = account // استعادة البيانات الكاملة للهيدرز لضمان "زيرو أصفار"
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
        fullUserData = null
        dashboardData = null
    }
}
