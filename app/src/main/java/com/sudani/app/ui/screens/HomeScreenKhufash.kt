package com.sudani.app.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sudani.app.ui.theme.*

@Composable
fun HomeScreenKhufash() {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(KhufashBackground)
            .verticalScroll(scrollState)
            .padding(bottom = 16.dp)
    ) {
        // 1. الهيدر العلوي (الاسم والرصيد)
        HeaderSection()

        Spacer(modifier = Modifier.height(24.dp))

        // 2. كارت الاستهلاك المتبقي (بدون زراير الإضافة)
        RemainingUsageCard()

        Spacer(modifier = Modifier.height(16.dp))

        // 3. كارت برنامج الولاء (النقاط)
        LoyaltyPointsCard()
    }
}

@Composable
fun HeaderSection() {
    // خلفية زرقاء داكنة متناسقة مع ثيم الخفاش عشان تبرز الهيدر
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = KhufashSurface,
                shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
            )
            .padding(top = 48.dp, bottom = 32.dp, start = 24.dp, end = 24.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // صورة الحساب (حرف م) والاسم
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(KhufashPrimary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("م", color = KhufashPrimary, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("مرحباً،", color = TextGray, fontSize = 14.sp)
                        Text("مؤمن الفاضل", color = TextWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text("الدفع المقدم", color = KhufashPrimary, fontSize = 12.sp)
                    }
                }

                // رقم الهاتف
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = KhufashBackground.copy(alpha = 0.5f)
                ) {
                    Text(
                        text = "0127610123",
                        color = TextWhite,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // الرصيد وزر الشحن
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text("الرصيد الأساسي", color = TextGray, fontSize = 14.sp)
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text("1.00", color = TextWhite, fontSize = 36.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("SDG", color = TextGray, fontSize = 16.sp, modifier = Modifier.padding(bottom = 6.dp))
                    }
                }

                Button(
                    onClick = { /* إضافة رصيد */ },
                    colors = ButtonDefaults.buttonColors(containerColor = KhufashPrimary),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "إضافة رصيد", modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("شحن", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun RemainingUsageCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = KhufashSurface),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("الاستهلاك المتبقي", color = TextWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("التفاصيل", color = TextGray, fontSize = 12.sp)
                    Icon(Icons.Default.KeyboardArrowLeft, contentDescription = null, tint = TextGray)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // دوائر الاستهلاك (الإنترنت، المكالمات، الرسائل) بدون زراير إضافية
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                UsageCircleIndicator(title = "إنترنت", value = "0", unit = "GB")
                UsageCircleIndicator(title = "مكالمات", value = "0", unit = "دقيقة")
                UsageCircleIndicator(title = "رسائل", value = "0", unit = "رسالة")
            }
        }
    }
}

@Composable
fun UsageCircleIndicator(title: String, value: String, unit: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(title, color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(12.dp))
        Box(
            modifier = Modifier.size(80.dp),
            contentAlignment = Alignment.Center
        ) {
            // الدائرة الرمادية الباهتة (الخلفية)
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(
                    color = TextGray.copy(alpha = 0.2f),
                    style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
                )
            }
            // النصوص داخل الدائرة
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(value, color = TextWhite, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(unit, color = TextGray, fontSize = 12.sp)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text("من أصل 0", color = TextGray, fontSize = 10.sp)
    }
}

@Composable
fun LoyaltyPointsCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = KhufashAccent), // اللون الذهبي
        shape = RoundedCornerShape(24.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // دائرة بيضاء بداخلها النجمة
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Star, contentDescription = "نقاط", tint = KhufashAccent)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text("برنامج الولاء (الخفاش)", color = Color.Black.copy(alpha = 0.7f), fontSize = 14.sp)
                    Text("1246.0 نقطة", color = Color.Black, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                }
            }
            
            // زر عرض الكل
            Surface(
                color = Color.Black.copy(alpha = 0.1f),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "الكل",
                    color = Color.Black,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }
    }
}
