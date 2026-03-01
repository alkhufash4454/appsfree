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
import com.sudani.app.ui.theme.*

@Composable
fun LoyaltyScreenKhufash() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(KhufashBackground)
    ) {
        // الهيدر الذهبي (مطابق للصورة)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(KhufashAccent, RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                .padding(top = 40.dp, bottom = 24.dp, start = 16.dp, end = 16.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.Black)
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("سجل التجميع (الخفاش)", color = Color.Black, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(24.dp))
                Text("إجمالي النقاط", color = Color.Black.copy(alpha = 0.6f), fontSize = 14.sp)
                Text("1246 نقطة", color = Color.Black, fontSize = 32.sp, fontWeight = FontWeight.Bold)
            }
        }

        // كارت العداد التنازلي والـ Streak
        Card(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = KhufashSurface),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("المكافأة القادمة بعد:", color = TextGray, fontSize = 14.sp)
                Text("02:32:57", color = KhufashPrimary, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // شبكة الأيام (Grid) زي الصورة بالظبط
                LazyVerticalGrid(
                    columns = GridCells.Fixed(5),
                    modifier = Modifier.height(180.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(15) { index ->
                        DayStreakItem(day = index + 1, isCompleted = index < 12) // مجمع 12 يوم مثلاً
                    }
                }
            }
        }

        // سجل النقاط المعرب
        Text(
            "سجل العمليات", 
            color = TextWhite, 
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
            fontSize = 18.sp, 
            fontWeight = FontWeight.Bold
        )

        LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
            val history = listOf(
                Pair("تجميع يومي - يوم 25", "+10 نقطة"),
                Pair("استبدال باقة 300 ميقا", "-70 نقطة"),
                Pair("استبدال باقة 1 جيجا", "-100 نقطة")
            )
            items(history) { item ->
                HistoryItem(title = item.first, points = item.second)
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
fun HistoryItem(title: String, points: String) {
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
                    Text("2026-03-02", color = TextGray, fontSize = 11.sp)
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
