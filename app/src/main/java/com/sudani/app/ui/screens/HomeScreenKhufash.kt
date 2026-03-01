package com.sudani.app.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sudani.app.ui.theme.*
import com.sudani.app.viewmodel.SudaniViewModel

@Composable
fun HomeScreenKhufash(
    viewModel: SudaniViewModel = viewModel(),
    onSwitchClick: () -> Unit // دالة لفتح قائمة تبديل الحسابات
) {
    val scrollState = rememberScrollState()
    val data = viewModel.dashboardData
    val msisdn = viewModel.msisdn

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(KhufashBackground)
            .verticalScroll(scrollState)
            .padding(bottom = 16.dp)
    ) {
        // 1. الهيدر العلوي (الاسم، الرقم، الرصيد)
        HeaderSection(
            name = data?.customerName ?: "مستخدم سوداني",
            phone = msisdn,
            balance = data?.balance ?: "0.00",
            onSwitchClick = onSwitchClick,
            onRefresh = { viewModel.fetchDashboard() }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 2. كارت الاستهلاك المتبقي
        RemainingUsageCard(activeOffers = data?.activeOffers)

        Spacer(modifier = Modifier.height(16.dp))

        // 3. كارت برنامج الولاء (النقاط)
        LoyaltyPointsCard(points = data?.totalLoyaltyPoints ?: "0")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeaderSection(
    name: String, 
    phone: String, 
    balance: String, 
    onSwitchClick: () -> Unit,
    onRefresh: () -> Unit
) {
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(KhufashPrimary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (name.isNotEmpty()) name.take(1) else "س",
                            color = KhufashPrimary,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("مرحباً،", color = TextGray, fontSize = 14.sp)
                        Text(name, color = TextWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text("الدفع المقدم", color = KhufashPrimary, fontSize = 12.sp)
                    }
                }

                // كبسولة الرقم أصبحت قابلة للضغط لتبديل الحساب
                Surface(
                    onClick = onSwitchClick,
                    shape = RoundedCornerShape(16.dp),
                    color = KhufashBackground.copy(alpha = 0.5f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = phone,
                            color = TextWhite,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Icon(
                            Icons.Default.ArrowDropDown, 
                            contentDescription = null, 
                            tint = TextWhite,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text("الرصيد الأساسي", color = TextGray, fontSize = 14.sp)
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(balance, color = TextWhite, fontSize = 36.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("SDG", color = TextGray, fontSize = 16.sp, modifier = Modifier.padding(bottom = 6.dp))
                    }
                }

                Button(
                    onClick = onRefresh,
                    colors = ButtonDefaults.buttonColors(containerColor = KhufashPrimary),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text("تحديث", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun RemainingUsageCard(activeOffers: List<com.sudani.app.data.model.ActiveOffer>?) {
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
                Icon(Icons.Default.KeyboardArrowLeft, contentDescription = null, tint = TextGray)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // منطق استخراج حجم البيانات من العروض النشطة
            val dataRemaining = activeOffers?.firstOrNull { 
                it.offerName?.contains("MB", ignoreCase = true) == true || 
                it.offerName?.contains("GB", ignoreCase = true) == true 
            }?.remainingVolume ?: "0"
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                UsageCircleIndicator(title = "إنترنت", value = dataRemaining, unit = "MB")
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
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(
                    color = TextGray.copy(alpha = 0.2f),
                    style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(value, color = TextWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(unit, color = TextGray, fontSize = 10.sp)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoyaltyPointsCard(points: String) {
    Card(
        onClick = { /* التنقل لصفحة النقاط */ },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = KhufashAccent),
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
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = KhufashAccent)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text("برنامج الولاء (الخفاش)", color = Color.Black.copy(alpha = 0.7f), fontSize = 14.sp)
                    Text("$points نقطة", color = Color.Black, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                }
            }
            
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
