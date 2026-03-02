package com.sudani.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sudani.app.ui.theme.*
import com.sudani.app.viewmodel.SudaniViewModel

// تعريف عناصر القائمة السفلية
sealed class BottomNavItem(val title: String, val icon: ImageVector) {
    object Home : BottomNavItem("الرئيسية", Icons.Default.Home)
    object Services : BottomNavItem("الخدمات", Icons.Default.List)
    object Settings : BottomNavItem("الإعدادات", Icons.Default.Settings)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KhufashMainScreen(viewModel: SudaniViewModel = viewModel()) {
    var selectedItem by remember { mutableStateOf<BottomNavItem>(BottomNavItem.Home) }
    
    // حالة التحكم في قائمة تبديل الحسابات
    val sheetState = rememberModalBottomSheetState()
    var showSheet by remember { mutableStateOf(false) }

    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Services,
        BottomNavItem.Settings
    )

    Scaffold(
        containerColor = KhufashBackground,
        bottomBar = {
            NavigationBar(
                containerColor = KhufashSurface,
                contentColor = TextWhite
            ) {
                items.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.title) },
                        label = { Text(item.title) },
                        selected = selectedItem == item,
                        onClick = { selectedItem = item },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = KhufashBackground,
                            selectedTextColor = KhufashPrimary,
                            indicatorColor = KhufashPrimary,
                            unselectedIconColor = TextGray,
                            unselectedTextColor = TextGray
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedItem) {
                // الشاشة الرئيسية مع دعم زر التحديث وتبديل الحسابات
                BottomNavItem.Home -> HomeScreenKhufash(
                    viewModel = viewModel, 
                    onSwitchClick = { showSheet = true }
                )
                // شاشة الخدمات المحدثة بالـ 15 خدمة
                BottomNavItem.Services -> ServicesScreenKhufash(viewModel = viewModel)
                // شاشة الإعدادات لإدارة الحسابات
                BottomNavItem.Settings -> SettingsScreenKhufash(viewModel = viewModel)
            }
        }

        // قائمة تبديل الحسابات (Bottom Sheet) -
        if (showSheet) {
            ModalBottomSheet(
                onDismissRequest = { showSheet = false },
                sheetState = sheetState,
                containerColor = KhufashSurface,
                dragHandle = { BottomSheetDefaults.DragHandle(color = TextGray) }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 24.dp, end = 24.dp, bottom = 40.dp)
                ) {
                    Text(
                        "تبديل الحساب (الخفاش) 🦇", 
                        color = TextWhite, 
                        fontSize = 20.sp, 
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(20.dp))

                    // عرض الأرقام الـ 10 المحفوظة
                    viewModel.savedAccounts.forEach { account ->
                        val phone = account.customerId ?: "رقم غير معروف"
                        
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { 
                                    viewModel.switchAccount(account) // تبديل الحساب واستعادة التوكن
                                    showSheet = false 
                                }
                                .padding(vertical = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Person, 
                                contentDescription = null, 
                                tint = if (phone == viewModel.msisdn) KhufashPrimary else TextGray
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = phone,
                                color = if (phone == viewModel.msisdn) KhufashPrimary else TextWhite,
                                fontSize = 16.sp,
                                fontWeight = if (phone == viewModel.msisdn) FontWeight.Bold else FontWeight.Normal
                            )
                            if (phone == viewModel.msisdn) {
                                Spacer(modifier = Modifier.weight(1f))
                                Icon(Icons.Default.Check, contentDescription = null, tint = SuccessGreen)
                            }
                        }
                        Divider(color = TextGray.copy(alpha = 0.1f))
                    }

                    // خيار إضافة رقم جديد (حتى 10 أرقام)
                    if (viewModel.savedAccounts.size < 10) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { 
                                    viewModel.isLoggedIn = false
                                    viewModel.isOtpSent = false
                                    showSheet = false 
                                }
                                .padding(vertical = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = KhufashAccent)
                            Spacer(modifier = Modifier.width(16.dp))
                            Text("إضافة رقم سوداني جديد", color = KhufashAccent, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        }
    }
}
