package com.sudani.app.viewmodel

import android.app.Application
import androidx.compose.runtime.*
import androidx.lifecycle.*
import com.sudani.app.data.model.*
import com.sudani.app.data.repository.SudaniRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// ... تعريف الـ UiState والـ ServiceOffering كما سبق

class SudaniViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = SudaniRepository()
    var dashboardData by mutableStateOf<DashboardData?>(null)
    var fullUserData by mutableStateOf<OnboardingData?>(null)
    var msisdn by mutableStateOf("")
    var token: String? = null

    val allServices = listOf(
        ServiceOffering("243586", "Ahla Youm", "Mixed", "2000", "100", "300", "1570"),
        ServiceOffering("244341", "10GB", "Data", "12500", "0", "10240", "2002")
        // أضف البقية هنا من ملف الـ config.py الخاص بك
    )

    fun fetchDashboard() {
        val userData = fullUserData ?: return
        viewModelScope.launch {
            try {
                val response = repository.getDashboard(msisdn, token!!, userData)
                if (response.isSuccessful) dashboardData = response.body()?.data
            } catch (e: Exception) { }
        }
    }

    fun claimPointsManual() {
        val userData = fullUserData ?: return
        val pts = dashboardData?.totalLoyaltyPoints?.split(".")?.get(0) ?: "0"
        viewModelScope.launch {
            repository.claimPoints(msisdn, token!!, userData, pts)
            fetchDashboard()
        }
    }

    fun activateKhufashOffer(type: String) {
        val userData = fullUserData ?: return
        val pts = dashboardData?.totalLoyaltyPoints?.split(".")?.get(0) ?: "0"
        viewModelScope.launch {
            if (type == "300mb") repository.redeemPointsOffer(msisdn, token!!, userData, pts, "320196", "2002", "300")
            else repository.redeemPointsOffer(msisdn, token!!, userData, pts, "320197", "2023", "1024")
            fetchDashboard()
        }
    }
    
    fun subscribeWithBalance(service: ServiceOffering) {
        val userData = fullUserData ?: return
        viewModelScope.launch {
            repository.subscribeToService(msisdn, token!!, userData, service)
            fetchDashboard()
        }
    }
}
