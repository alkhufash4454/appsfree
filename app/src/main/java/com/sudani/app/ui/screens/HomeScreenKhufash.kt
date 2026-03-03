package com.sudani.app.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sudani.app.data.model.FreeUnit
import com.sudani.app.ui.theme.*
import com.sudani.app.viewmodel.SudaniViewModel

@Composable
fun HomeScreenKhufash(
    viewModel: SudaniViewModel = viewModel(),
    onSwitchClick: () -> Unit
) {
    val scrollState = rememberScrollState()
    val data = viewModel.dashboardData
    val msisdn = viewModel.msisdn

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(KhufashBackground)
            .verticalScroll(scrollState)
            .padding(bottom = 24.dp)
    ) {
        // 1. الهيدر العلوي الديناميكي (يعرض الاسم والرصيد الحقيقي من السيرفر)
        HeaderSection(
            name = data?.customerName ?: "مستخدم سوداني",
            phone = msisdn,
            balance = data?.balance?.toString() ?: "0.00",
            onSwitchClick = onSwitchClick,
            onRefresh = { viewModel.fetchDashboard() }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 2. كارت الاستهلاك المتبقي (تطبيق فكرة الدائرة الحمراء للإنترنت)
        RemainingUsageCard(freeUnits = data?.freeUnits)

        Spacer(modifier = Modifier.height(16.dp))

        // 3. كارت برنامج الولاء (يعرض النقاط الحقيقية المجمعة)
        LoyaltyPointsCard(
            points = data?.totalLoyaltyPoints?.split(".")?.get(0) ?: "0", // تحويل النقاط لعدد صحيح
            onClaimClick = { viewModel.claimPointsManual() }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 4. قسم العمليات السريعة (تفعيل العروض بالنقاط حصراً)
        Text(
            text = "عمليات سريعة 🦇",
            color = TextWhite,
            modifier = Modifier.padding(horizontal = 20.dp),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // زر 300 ميجا (70 نقطة)
            QuickActionBtn(
                title = "300 ميجا",
                subtitle = "70 نقطة",
                icon = Icons.Default.Public,
                modifier = Modifier.weight(1f),
                onClick = { viewModel.activateKhufashOffer("300mb") }
            )

            // زر 1 قيقا (100 نقطة)
            QuickActionBtn(
                title = "1 جيجا",
                subtitle = "100 نقطة",
                icon = Icons.Default.Public,
                modifier = Modifier.weight(1f),
                onClick = { viewModel.activateKhufashOffer("1gb") }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // زر تجميع النقاط اليدوي (يعتمد على رد السيرفر المباشر)
        Button(
            onClick = { viewModel.claimPointsManual() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(Icons.Default.Star, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text("تجميع نقاط الخفاش الآن", fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}

@Composable
fun RemainingUsageCard(freeUnits: List<FreeUnit>?) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = KhufashSurface),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text("الاستهلاك المتبقي", color = TextWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(24.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // تصفية الوحدات لعرض الإنترنت والمكالمات تلقائياً
                UsageCircleIndicator(title = "إنترنت", freeUnits = freeUnits, isInternet = true)
                UsageCircleIndicator(title = "مكالمات", freeUnits = freeUnits, isInternet = false)
                UsageCircleIndicator(title = "رسائل", freeUnits = freeUnits, isInternet = false)
            }
        }
    }
}

@Composable
fun UsageCircleIndicator(title: String, freeUnits: List<FreeUnit>?, isInternet: Boolean) {
    // منطق البحث عن وحدة البيانات المناسبة من رد السيرفر
    val unit = if (isInternet) {
        freeUnits?.find { 
            it.unitName?.contains("إنترنت") == true || 
            it.measureUnit?.contains("MB") == true || 
            it.measureUnit?.contains("GB") == true 
        }
    } else {
        freeUnits?.find { it.unitName?.contains("مكالمات") == true || it.measureUnit?.contains("Min") == true }
    }

    val cur = unit?.currentAmount?.toFloatOrNull() ?: 0f
    val tot = unit?.totalAmount?.toFloatOrNull() ?: 1f
    val percentage = if (tot > 0) (cur / tot) else 0f
    
    // تنسيق عرض الحجم (قيقا أو ميجا) بناءً على القيم الحقيقية
    val displayValue = when {
        cur >= 1024 -> "${String.format("%.2f", cur / 1024)} GB"
        cur > 0 -> "${cur.toInt()} MB"
        else -> "0 MB"
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(title, color = TextWhite, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(12.dp))
        Box(modifier = Modifier.size(80.dp), contentAlignment = Alignment.Center) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(color = TextGray.copy(alpha = 0.2f), style = Stroke(width = 6.dp.toPx()))
                drawArc(
                    color = if (isInternet) Color.Red else KhufashPrimary, // اللون الأحمر للإنترنت
                    startAngle = -90f,
                    sweepAngle = 360 * percentage,
                    useCenter = false,
                    style = Stroke(6.dp.toPx(), cap = StrokeCap.Round)
                )
            }
            Text(displayValue, color = TextWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}

// الأقسام المتبقية (HeaderSection, LoyaltyPointsCard, QuickActionBtn) تظل كما هي مع ربطها بالـ ViewModel الجديد.
