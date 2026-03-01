package com.sudani.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sudani.app.ui.theme.*
import com.sudani.app.viewmodel.SudaniViewModel

@Composable
fun SettingsScreenKhufash(viewModel: SudaniViewModel) {
    Column(
        modifier = Modifier.fillMaxSize().background(KhufashBackground).padding(16.dp)
    ) {
        Text("الإعدادات", color = TextWhite, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(24.dp))

        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(KhufashSurface)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("إدارة الأرقام", color = KhufashPrimary, fontWeight = FontWeight.Bold)
                Text("لديك ${viewModel.savedAccounts.size} أرقام مضافة حالياً", color = TextGray, fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { viewModel.logout() },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.8f)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("تسجيل الخروج من الرقم الحالي", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}
