package com.sudani.app

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sudani.app.ui.theme.*
import com.sudani.app.viewmodel.SudaniViewModel
import com.sudani.app.viewmodel.UiState
import androidx.compose.foundation.text.KeyboardOptions

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SudaniAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = DarkBackground
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun SudaniAppTheme(content: @Composable () -> Unit) {
    val colorScheme = darkColorScheme(
        primary = BluePrimary,
        secondary = BlueSecondary,
        tertiary = BlueTertiary,
        background = DarkBackground,
        surface = SurfaceDark,
        onPrimary = Color.White,
        onSecondary = Color.White,
        onTertiary = Color.White,
        onBackground = TextPrimary,
        onSurface = TextPrimary,
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}

@Composable
fun AppNavigation(viewModel: SudaniViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(uiState) {
        when (uiState) {
            is UiState.Success -> {
                Toast.makeText(context, (uiState as UiState.Success).message, Toast.LENGTH_SHORT).show()
            }
            is UiState.Error -> {
                Toast.makeText(context, (uiState as UiState.Error).message, Toast.LENGTH_LONG).show()
            }
            else -> {}
        }
    }

    if (!viewModel.isLoggedIn) {
        LoginScreen(viewModel)
    } else {
        DashboardScreen(viewModel)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(viewModel: SudaniViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "مرحباً بك في تطبيق سوداني",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = BlueTertiary
        )
        Spacer(modifier = Modifier.height(32.dp))

        if (!viewModel.isOtpSent) {
            OutlinedTextField(
                value = viewModel.msisdn,
                onValueChange = { if (it.length <= 10) viewModel.msisdn = it },
                label = { Text("رقم الهاتف (10 أرقام)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { viewModel.sendOtp() },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("إرسال OTP")
            }
        } else {
            OutlinedTextField(
                value = viewModel.otp,
                onValueChange = { viewModel.otp = it },
                label = { Text("أدخل رمز OTP") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { viewModel.verifyOtp() },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("تحقق وتسجيل الدخول")
            }
        }
    }
}

@Composable
fun DashboardScreen(viewModel: SudaniViewModel) {
    val data = viewModel.dashboardData
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("مرحباً،", color = TextSecondary)
                Text(data?.customerName ?: "مستخدم سوداني", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
            IconButton(onClick = { viewModel.fetchDashboard() }) {
                Icon(Icons.Default.Refresh, contentDescription = "تحديث", tint = BlueTertiary)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Balance & Points Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    InfoItem(label = "الرصيد", value = "${data?.balance ?: "0"} SDG", icon = Icons.Default.AccountBalanceWallet)
                    InfoItem(label = "النقاط", value = data?.totalLoyaltyPoints ?: "0", icon = Icons.Default.Star, color = AccentGold)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Circular Services (Active Offers)
        Text("الخدمات المشترك فيها", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(12.dp))
        
        if (data?.activeOffers.isNullOrEmpty()) {
            Text("لا توجد خدمات نشطة حالياً", color = TextSecondary, modifier = Modifier.padding(8.dp))
        } else {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(data?.activeOffers ?: emptyList()) { offer ->
                    ServiceCircle(name = offer.offerName ?: "خدمة", volume = offer.remainingVolume ?: "")
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Action Buttons
        Text("عروض وخدمات سريعة", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ActionButton(
                text = "300 ميقا",
                subText = "70 نقطة",
                icon = Icons.Default.NetworkCheck,
                modifier = Modifier.weight(1f),
                onClick = { viewModel.activateOffer("300MB") }
            )
            ActionButton(
                text = "1 جيجا",
                subText = "100 نقطة",
                icon = Icons.Default.SignalCellularAlt,
                modifier = Modifier.weight(1f),
                onClick = { viewModel.activateOffer("1GB") }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ActionButton(
                text = "تجميع نقاط",
                subText = "تجميع تلقائي",
                icon = Icons.Default.AddCircle,
                color = SuccessGreen,
                modifier = Modifier.weight(1f),
                onClick = { viewModel.claimPoints() }
            )
            ActionButton(
                text = "دخول الخدمات",
                subText = "كل العروض",
                icon = Icons.Default.List,
                modifier = Modifier.weight(1f),
                onClick = { /* Navigate to services */ }
            )
        }
    }
}

@Composable
fun InfoItem(label: String, value: String, icon: ImageVector, color: Color = BlueTertiary) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(label, fontSize = 12.sp, color = TextSecondary)
            Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ServiceCircle(name: String, volume: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(BluePrimary, BlueTertiary))),
            contentAlignment = Alignment.Center
        ) {
            Text(volume.take(5), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(name.take(10), fontSize = 10.sp, color = TextSecondary)
    }
}

@Composable
fun ActionButton(
    text: String,
    subText: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    color: Color = BluePrimary,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier.height(100.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        border = BorderStroke(1.dp, color.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(text, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(subText, fontSize = 10.sp, color = TextSecondary)
        }
    }
}
