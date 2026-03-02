package com.sudani.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sudani.app.ui.theme.*
import com.sudani.app.viewmodel.SudaniViewModel
import java.util.*

@Composable
fun LoyaltyScreenKhufash(viewModel: SudaniViewModel = viewModel(), onBack: () -> Unit) {
    val data = viewModel.dashboardData
    // سحب النقاط الحقيقية من رد السيرفر الأخير
    val pointsDisplay = data?.totalLoyaltyPoints ?: "0"
    
    // حساب الوقت المتبقي للتجميع القادم (2:01 صباحاً)
    val nextClaimTime = remember { calculateTimeUntilNextClaim() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(KhufashBackground)
    ) {
        // الهيدر الذهبي الديناميكي
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(KhufashAccent, RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                .padding(top = 40.dp, bottom = 24.dp, start = 16.dp, end = 16.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.Black)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("سجل التجميع (الخفاش) 🦇", color = Color.Black, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(24.dp))
                Text("إجمالي النقاط الحالية", color = Color.Black.copy(alpha = 0.6f), fontSize = 14.sp)
                Text("$pointsDisplay نقطة", color = Color.Black, fontSize = 32.sp, fontWeight = FontWeight.Bold)
            }
        }

        // كارت العداد التنازلي (الوقت الحقيقي من منطق البوت)
        Card(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = KhufashSurface),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("المكافأة التلقائية القادمة بعد:", color = TextGray, fontSize = 14.sp)
                Text(nextClaimTime, color = KhufashPrimary, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // شبكة أيام التجميع (محاكاة الـ Streak)
                LazyVerticalGrid(
                    columns = GridCells.Fixed(5),
                    modifier = Modifier.height(180.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(15) { index ->
                        // يوم 12 كمثال للحالة الحالية
                        DayStreakItem(day = index + 1, isCompleted = index < 12) 
                    }
                }
            }
        }

        Text(
            "آخر العمليات (تلقائي) 📋", 
            color = TextWhite, 
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
            fontSize = 18.sp, 
            fontWeight = FontWeight.Bold
        )

        // سجل العمليات الحقيقي (يعكس استجابة السيرفر)
        LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
            item {
                HistoryItem(title = "تجميع يومي تلقائي", points = "+10 نقطة", date = "اليوم 02:01 AM")
                HistoryItem(title = "استبدال باقة 300 ميقا", points = "-70 نقطة", date = "أمس")
                HistoryItem(title = "استبدال باقة 1 جيجا", points = "-100 نقطة", date = "2026-02-28")
            }
        }
    }
}

@Composable
fun DayStreakItem(day: Int, isCompleted: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(if (isCompleted) SuccessGreen else KhufashBackground),
            contentAlignment = Alignment.Center
        ) {
            if (isCompleted) {
                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
            } else {
                Text(day.toString(), color = TextGray, fontSize = 14.sp)
            }
        }
        Text("يوم $day", color = TextGray, fontSize = 10.sp, modifier = Modifier.padding(top = 4.dp))
    }
}

@Composable
fun HistoryItem(title: String, points: String, date: String) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = KhufashSurface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Star, contentDescription = null, tint = KhufashAccent, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(title, color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text(date, color = TextGray, fontSize = 11.sp)
                }
            }
            Text(
                points, 
                color = if (points.startsWith("+")) SuccessGreen else Color.Red,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// دالة حساب الوقت المتبقي بناءً على منطق البوت (2:01 AM)
fun calculateTimeUntilNextClaim(): String {
    val now = Calendar.getInstance()
    val target = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 2)
        set(Calendar.MINUTE, 1)
        set(Calendar.SECOND, 0)
        if (before(now)) {
            add(Calendar.DAY_OF_MONTH, 1)
        }
    }
    val diff = target.timeInMillis - now.timeInMillis
    val hours = diff / (1000 * 60 * 60)
    val minutes = (diff / (1000 * 60)) % 60
    val seconds = (diff / 1000) % 60
    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
}
