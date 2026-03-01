package com.sudani.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Language // بديل لأيقونة الإنترنت
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sudani.app.ui.theme.*

// كلاس بسيط لتخزين بيانات الباقات
data class KhufashService(
    val name: String,
    val validity: String,
    val dataMb: String,
    val voiceMins: String,
    val price: String
)

@Composable
fun ServicesScreenKhufash() {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("باقات مميزة", "مكالمات", "إنترنت")

    // البيانات الحقيقية من البوت بتاعك
    val mixedServices = listOf(
        KhufashService("أهلاً يوم", "صالح لمدة يوم", "300 MB", "100 دقيقة", "2,000.00"),
        KhufashService("ريح بالك", "صالح لمدة يوم", "100 MB", "50 دقيقة", "1,000.00"),
        KhufashService("ريح بالك", "صالح لمدة يوم", "1024 MB", "500 دقيقة", "5,200.00"),
        KhufashService("ريح بالك ماكس", "صالح لمدة شهر", "20 GB", "1000 دقيقة", "28,000.00")
    )

    val voiceServices = listOf(
        KhufashService("خلي عنك", "صالح لمدة يوم", "0 MB", "45 دقيقة", "1,500.00"),
        KhufashService("خلي عنك", "صالح لمدة يومين", "0 MB", "300 دقيقة", "3,700.00"),
        KhufashService("خلي عنك ماكس", "صالح لمدة شهر", "0 MB", "1000 دقيقة", "14,000.00")
    )

    val dataServices = listOf(
        KhufashService("500 ميقا", "صالح لمدة يوم", "500 MB", "0 دقيقة", "1,700.00"),
        KhufashService("1 جيجا", "صالح لمدة يوم", "1024 MB", "0 دقيقة", "10,304.00"),
        KhufashService("5 جيجا", "صالح لمدة أسبوع", "5120 MB", "0 دقيقة", "8,500.00"),
        KhufashService("10 جيجا", "صالح لمدة شهر", "10 GB", "0 دقيقة", "12,500.00")
    )

    val currentList = when (selectedTabIndex) {
        0 -> mixedServices
        1 -> voiceServices
        2 -> dataServices
        else -> mixedServices
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(KhufashBackground)
    ) {
        // شريط العنوان العلوي
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(24.dp)) // عشان الكلمة تتوسط
            Text("الخدمات", color = TextWhite, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Icon(Icons.Default.Search, contentDescription = "بحث", tint = TextWhite)
        }

        // شريط التبويبات (Tabs)
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

        // قائمة الكروت
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(currentList) { service ->
                KhufashServiceCard(service)
            }
        }
    }
}

@Composable
fun KhufashServiceCard(service: KhufashService) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = KhufashSurface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            // الجزء العلوي من الكارت (التفاصيل)
            Column(modifier = Modifier.padding(20.dp)) {
                Text(service.validity, color = KhufashPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Text(service.name, color = TextWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                
                Spacer(modifier = Modifier.height(20.dp))
                
                Row(horizontalArrangement = Arrangement.spacedBy(32.dp)) {
                    // عرض الإنترنت لو موجود
                    if (service.dataMb != "0 MB") {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.Language, contentDescription = null, tint = TextGray, modifier = Modifier.size(28.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(service.dataMb, color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                Text("إنترنت", color = TextGray, fontSize = 12.sp)
                            }
                        }
                    }
                    
                    // عرض المكالمات لو موجودة
                    if (service.voiceMins != "0 دقيقة") {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Call, contentDescription = null, tint = TextGray, modifier = Modifier.size(28.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(service.voiceMins.replace(" دقيقة", ""), color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                Text("دقيقة محلية", color = TextGray, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // الجزء السفلي من الكارت (السعر وزر الاشتراك)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(KhufashPrimary)
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(service.price, color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 22.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("SDG", color = TextWhite, fontSize = 14.sp, modifier = Modifier.padding(bottom = 3.dp))
                        }
                        Text("شامل الضريبة", color = TextWhite.copy(alpha = 0.8f), fontSize = 11.sp)
                    }

                    Button(
                        onClick = { /* تنفيذ الاشتراك لاحقاً */ },
                        colors = ButtonDefaults.buttonColors(containerColor = TextWhite),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text("اشتراك", color = KhufashPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}
