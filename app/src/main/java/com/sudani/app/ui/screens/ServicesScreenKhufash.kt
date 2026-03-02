package com.sudani.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Public 
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sudani.app.ui.theme.*
import com.sudani.app.viewmodel.ServiceOffering
import com.sudani.app.viewmodel.SudaniViewModel

@Composable
fun ServicesScreenKhufash(viewModel: SudaniViewModel = viewModel()) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("باقات مميزة", "مكالمات", "إنترنت")

    // تصفية الخدمات تلقائياً من القائمة الـ 15 المحدثة في الـ ViewModel
    val currentList = when (selectedTabIndex) {
        0 -> viewModel.allServices.filter { it.category == "Mixed" }
        1 -> viewModel.allServices.filter { it.category == "Voice" }
        2 -> viewModel.allServices.filter { it.category == "Data" }
        else -> viewModel.allServices
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(KhufashBackground)
    ) {
        // شريط العنوان العلوي (The Bat Style)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 48.dp, bottom = 16.dp, start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(24.dp))
            Text("خدمات الخفاش 🦇", color = TextWhite, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Icon(Icons.Default.Search, contentDescription = "بحث", tint = TextWhite)
        }

        // شريط التبويبات المتفاعل مع ثيم الخفاش
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = KhufashBackground,
            contentColor = KhufashPrimary,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                    color = KhufashPrimary
                )
            },
            divider = { Divider(color = KhufashSurface) }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = {
                        Text(
                            text = title,
                            color = if (selectedTabIndex == index) KhufashPrimary else TextGray,
                            fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }
        }

        // قائمة الكروت الديناميكية التي تعتمد على استجابة السيرفر
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(currentList) { service ->
                KhufashServiceCard(service) {
                    // استدعاء منطق الاشتراك بالرصيد الذي يعالج كود 502 تلقائياً
                    viewModel.subscribeWithBalance(service)
                }
            }
        }
    }
}

@Composable
fun KhufashServiceCard(service: ServiceOffering, onSubscribe: () -> Unit) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = KhufashSurface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            Column(modifier = Modifier.padding(20.dp)) {
                // تصنيف الباقة بناءً على منطق البوت
                Text(
                    text = when(service.category) {
                        "Mixed" -> "🎁 عرض مميز"
                        "Voice" -> "📞 باقة مكالمات"
                        else -> "📶 باقة إنترنت"
                    }, 
                    color = KhufashPrimary, 
                    fontSize = 12.sp, 
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(service.name, color = TextWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                
                Spacer(modifier = Modifier.height(20.dp))
                
                Row(horizontalArrangement = Arrangement.spacedBy(32.dp)) {
                    // بيانات الإنترنت المتبقية (تلقائي)
                    if (service.dataMb != "0") {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Public, null, tint = TextGray, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                val displayMb = if (service.dataMb.toInt() >= 1024) "${service.dataMb.toInt()/1024} GB" else "${service.dataMb} MB"
                                Text(displayMb, color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text("إنترنت", color = TextGray, fontSize = 11.sp)
                            }
                        }
                    }
                    
                    // بيانات المكالمات (تلقائي)
                    if (service.voiceMinutes != "0") {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Call, null, tint = TextGray, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("${service.voiceMinutes} د", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text("مكالمات", color = TextGray, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }

            // شريط السعر والاشتراك (محرك الـ API)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(KhufashPrimary)
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.Bottom) {
                            // عرض السعر الحقيقي المطلوب من السيرفر
                            Text(service.price, color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("SDG", color = TextWhite, fontSize = 12.sp, modifier = Modifier.padding(bottom = 2.dp))
                        }
                    }

                    Button(
                        onClick = onSubscribe,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text("تفعيل", color = KhufashPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}
