package com.sudani.app.viewmodel

import android.app.Application
import androidx.compose.runtime.*
import androidx.lifecycle.*
import com.sudani.app.data.model.*
import com.sudani.app.data.repository.SudaniRepository
import com.sudani.app.data.api.SudaniConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SudaniViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = SudaniRepository()
    
    // إدارة حالة الواجهة لرسائل النجاح والفشل
    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState = _uiState.asStateFlow()

    // متغيرات الحالة المطلوبة في MainActivity والـ Screens
    var isLoggedIn by mutableStateOf(false)
    var isOtpSent by mutableStateOf(false)
    var msisdn by mutableStateOf("")
    var otp by mutableStateOf("")
    var token: String? = null
    var fullUserData by mutableStateOf<OnboardingData?>(null)
    var dashboardData by mutableStateOf<DashboardData?>(null)
    var savedAccounts by mutableStateOf<List<OnboardingData>>(emptyList())

    // قائمة الخدمات الـ 15 المتطابقة مع البوت
    val allServices = listOf(
        ServiceOffering("243586", "Ahla Youm", "Mixed", "2000", "100", "300", "1570"),
        ServiceOffering("237602", "Raih Balak", "Mixed", "1000", "50", "100", "1699"),
        ServiceOffering("238884", "Raih Balak Max", "Mixed", "28000", "1000", "20480", "1762"),
        ServiceOffering("240891", "Raih Balak", "Mixed", "5200", "500", "1024", "1612"),
        ServiceOffering("238883", "Raih Balak", "Mixed", "18500", "0", "5120", "1763"),
        ServiceOffering("231232", "Khalli Anak", "Voice", "3700", "300", "0", "1606"),
        ServiceOffering("243979", "Khalli Anak", "Voice", "1500", "45", "0", "1603"),
        ServiceOffering("243980", "Khalli Anak", "Voice", "14000", "1000", "0", "1607"),
        ServiceOffering("244340", "5GB", "Data", "8500", "0", "5120", "2001"),
        ServiceOffering("244341", "10GB", "Data", "12500", "0", "10240", "2002"),
        ServiceOffering("244441", "5GB", "Data", "14000", "0", "5120", "2003"),
        ServiceOffering("244448", "500MB", "Data", "1700", "0", "500", "2004"),
        ServiceOffering("239806", "200MB", "Data", "1700", "0", "200", "2005"),
        ServiceOffering("244449", "1.5GB", "Data", "2500", "0", "1536", "2006"),
        ServiceOffering("239706", "1GB", "Data", "10304", "0", "1024", "2007")
    )

    // إرسال OTP -
    fun sendOtp() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val res = repository.generateOtp(msisdn)
                if (res.isSuccessful && res.body()?.responseCode == "200") {
                    isOtpSent = true
                    _uiState.value = UiState.Success("تم إرسال الرمز بنجاح 🦇")
                } else {
                    _uiState.value = UiState.Error(res.body()?.responseMessage ?: "فشل إرسال الرمز")
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error("خطأ في الاتصال بالشبكة")
            }
        }
    }

    // التحقق من OTP وإكمال التسجيل -
    fun verifyOtp() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val res = repository.completeOnboarding(msisdn, otp)
                if (res.isSuccessful && res.body()?.responseCode == "200") {
                    fullUserData = res.body()?.data
                    token = fullUserData?.token
                    isLoggedIn = true
                    fetchDashboard() // تحديث البيانات فوراً بعد الدخول
                    _uiState.value = UiState.Success("تم تسجيل دخول الخفاش بنجاح 🦇")
                } else {
                    _uiState.value = UiState.Error(res.body()?.responseMessage ?: "رمز التحقق خطأ")
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error("خطأ في عملية التحقق")
            }
        }
    }

    // جلب بيانات لوحة التحكم (الاسم، الرصيد، النقاط) -
    fun fetchDashboard() {
        val currentToken = token ?: return
        val userData = fullUserData ?: return
        viewModelScope.launch {
            try {
                val res = repository.getDashboard(msisdn, currentToken, userData)
                if (res.isSuccessful && res.body()?.responseCode == "200") {
                    dashboardData = res.body()?.data
                }
            } catch (e: Exception) {}
        }
    }

    // تجميع النقاط يدوياً -
    fun claimPointsManual() {
        val currentToken = token ?: return
        val userData = fullUserData ?: return
        // معالجة النقاط الحالية كعدد صحيح لضمان قبول السيرفر
        val points = dashboardData?.totalLoyaltyPoints?.split(".")?.get(0) ?: "0"
        
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val res = repository.claimPoints(msisdn, currentToken, userData, points)
                if (res.isSuccessful && res.body()?.responseCode == "200") {
                    _uiState.value = UiState.Success("تم تجميع نقاط الخفاش 🦇")
                    fetchDashboard()
                } else {
                    _uiState.value = UiState.Error(res.body()?.responseMessage ?: "تنبيه: أخذتها مسبقاً")
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error("خطأ في الشبكة")
            }
        }
    }

    // تفعيل عروض النقاط (300MB / 1GB) -
    fun activateKhufashOffer(type: String) {
        val currentToken = token ?: return
        val userData = fullUserData ?: return
        val currentPoints = dashboardData?.totalLoyaltyPoints?.split(".")?.get(0) ?: "0"
        
        val (offerId, productId, pointsNeeded, vol) = when(type) {
            "300mb" -> Triple(SudaniConfig.OFFER_300MB_ID, SudaniConfig.OFFER_300MB_PRODUCT_ID, "70") to "300"
            "1gb" -> Triple(SudaniConfig.OFFER_1GB_ID, SudaniConfig.OFFER_1GB_PRODUCT_ID, "100") to "1024"
            else -> return
        }

        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                // استدعاء دالة الـ Redeem المتوافقة مع البوت
                val res = repository.redeemPointsOffer(msisdn, currentToken, userData, currentPoints, offerId.first, offerId.second, offerId.third)
                if (res.isSuccessful && res.body()?.responseCode == "200") {
                    _uiState.value = UiState.Success("تم تفعيل العرض بنجاح 🦇")
                    fetchDashboard()
                } else {
                    _uiState.value = UiState.Error(res.body()?.responseMessage ?: "نقاطك لا تكفي")
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error("خطأ في الشبكة")
            }
        }
    }

    // تفعيل الخدمات بالرصيد النقدي -
    fun subscribeWithBalance(service: ServiceOffering) {
        val currentToken = token ?: return
        val userData = fullUserData ?: return
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                // معالجة كود 502 للرصيد غير الكافي
                val res = repository.subscribeToService(msisdn, currentToken, userData, service)
                if (res.isSuccessful && res.body()?.responseCode == "200") {
                    _uiState.value = UiState.Success("تم تفعيل ${service.name} بنجاح 🦇")
                    fetchDashboard()
                } else {
                    _uiState.value = UiState.Error("فشل: رصيد نقدي غير كافٍ (502)")
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error("خطأ في الاتصال بالسيرفر")
            }
        }
    }

    fun switchAccount(account: OnboardingData) {
        fullUserData = account
        msisdn = account.customerId ?: ""
        token = account.token
        isLoggedIn = true
        fetchDashboard()
    }

    fun logout() {
        isLoggedIn = false
        isOtpSent = false
        dashboardData = null
    }
}
