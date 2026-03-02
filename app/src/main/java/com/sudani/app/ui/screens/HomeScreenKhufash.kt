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
import com.sudani.app.ui.theme.*
import com.sudani.app.viewmodel.ServiceOffering
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
        // 1. الهيدر العلوي (الاسم، الرقم، الرصيد)
        HeaderSection(
            name = data?.customerName ?: "مستخدم سوداني",
            phone = msisdn,
            balance = data?.balance?.toString() ?: "0.00",
            onSwitchClick = onSwitchClick,
            onRefresh = { viewModel.fetchDashboard() }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 2. كارت الاستهلاك المتبقي
        RemainingUsageCard(activeOffers = data?.activeOffers)

        Spacer(modifier = Modifier.height(16.dp))

        // 3. كارت برنامج الولاء (النقاط)
        LoyaltyPointsCard(points = data?.totalLoyaltyPoints ?: "0")

        Spacer(modifier = Modifier.height(24.dp))

        // 4. قسم العمليات السريعة (الخفاش) -
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
            // زر تفعيل 300 ميجا (70 نقطة) -
            QuickActionBtn(
                title = "300 ميجا",
                subtitle = "70 نقطة",
                icon = Icons.Default.Public,
                modifier = Modifier.weight(1f),
                onClick = {
                    val offer300 = ServiceOffering(
                        offeringId = "320196",
                        name = "300MB Offer",
                        category = "Data",
                        price = "0",
                        voiceMinutes = "0",
                        dataMb = "300",
                        productId = "2002"
                    )
                    viewModel.subscribeToService(offer300)
                }
            )

            // زر تفعيل 1 جيجا (100 نقطة) -
            QuickActionBtn(
                title = "1 جيجا",
                subtitle = "100 نقطة",
                icon = Icons.Default.Public,
                modifier = Modifier.weight(1f),
                onClick = {
                    val offer1GB = ServiceOffering(
                        offeringId = "320197",
                        name = "1GB Offer",
                        category = "Data",
                        price = "0",
                        voiceMinutes = "0",
                        dataMb = "1024",
                        productId = "2023"
                    )
                    viewModel.subscribeToService(offer1GB)
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // زر تجميع النقاط اليدوي -
        Button(
            onClick = { viewModel.claimPoints() },
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickActionBtn(
    title: String, 
    subtitle: String, 
    icon: ImageVector, 
    modifier: Modifier, 
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier.height(100.dp),
        colors = CardDefaults.cardColors(containerColor = KhufashSurface),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = null, tint = KhufashPrimary, modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(title, color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text(subtitle, color = KhufashAccent, fontSize = 11.sp)
        }
    }
}

// الكود أدناه يظل كما هو (HeaderSection, RemainingUsageCard, UsageCircleIndicator, LoyaltyPointsCard)
// تم تعديل HeaderSection ليقبل balance كـ String
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

                IconButton(
                    onClick = onRefresh,
                    modifier = Modifier.background(KhufashPrimary, CircleShape)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "تحديث", tint = Color.White)
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
