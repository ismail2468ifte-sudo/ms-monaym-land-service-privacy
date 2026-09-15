package com.msmonaym.land.ui.home

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.msmonaym.land.R
import com.msmonaym.land.data.ai.AiCategory
import com.msmonaym.land.ui.ai.AiFloatingActionButton
import com.msmonaym.land.ui.ai.AiLandAssistantModal
import com.msmonaym.land.ui.navigation.Screen
import com.msmonaym.land.ui.theme.*

data class AppLauncherItem(
    val title: String,
    val emojiIcon: String,
    val screen: Screen? = null,
    val tintColor: Color,
    val badge: String? = null,
    val testTag: String,
    val action: (() -> Unit)? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigate: (Screen) -> Unit
) {
    val context = LocalContext.current

    // AI Assistant System State
    var showAiAssistant by remember { mutableStateOf(false) }
    var currentAiCategory by remember { mutableStateOf(AiCategory.GENERAL) }
    var showTopMenu by remember { mutableStateOf(false) }

    val launcherItems = remember {
        listOf(
            AppLauncherItem("জমি পরিমাপ", "📐", Screen.Calculator, SkyTint, "জনপ্রিয়", "icon_calculator"),
            AppLauncherItem("খতিয়ান পর্চা", "📜", Screen.Khatian, AmberTint, "ই-পর্চা", "icon_khatian"),
            AppLauncherItem("দলিল রাইটার", "🖋️", Screen.DeedWriter, PurpleTint, "নতুন", "icon_deed_writer"),
            AppLauncherItem("কোম্পানি কনসালটেন্ট", "💼", Screen.CompanyConsultant, BlueTint, "বেসরকারি", "icon_company_consultant"),
            AppLauncherItem("উত্তরাধিকার", "⚖️", Screen.WarishCalculator, AmberTint, "১০০%", "icon_warish"),
            AppLauncherItem("এআই স্ক্যানার", "📷", Screen.AiDocScanner, EmeraldTint, "Gemini", "icon_ai_scanner"),
            AppLauncherItem("সার্ভে রিপোর্ট", "📊", Screen.SurveyReport, TealTint, "PRO", "icon_survey_report"),
            AppLauncherItem("ই-নামজারি", "🏛️", Screen.Mutation, IndigoTint, "সহায়িকা", "icon_mutation"),
            AppLauncherItem("ভূমি কর", "💰", Screen.Tax, AmberTint, "হিসাব", "icon_tax"),
            AppLauncherItem("রেজিস্ট্রি ও ফি", "📝", Screen.Registration, EmeraldTint, "ফি হিসাব", "icon_registration"),
            AppLauncherItem("জমি আইন", "📖", Screen.Law, SkyTint, "আইন", "icon_law"),
            AppLauncherItem("সেবা নির্দেশিকা", "📋", Screen.Services, IndigoTint, "গাইড", "icon_services"),
            AppLauncherItem("প্রিমিয়াম আমিন", "💎", Screen.PremiumServices, AmberTint, "VIP", "icon_premium"),
            AppLauncherItem("বিকাশ ও নগদ", "💳", Screen.Payment, RoseTint, "পেমেন্ট", "icon_payment"),
            AppLauncherItem("ড্যাশবোর্ড", "📊", Screen.SummaryDashboard, PurpleTint, "লাইভ", "icon_dashboard"),
            AppLauncherItem("অনুসন্ধান", "🔍", Screen.Search, SkyTint, "সার্চ", "icon_search"),
            AppLauncherItem("এআই সহকারী", "🤖", null, IndigoTint, "AI বট", "icon_ai_bot", action = {
                currentAiCategory = AiCategory.GENERAL
                showAiAssistant = true
            }),
            AppLauncherItem("অ্যাপ QR কোড", "📲", Screen.AppQrCode, SkyTint, "শেয়ার", "icon_qr_code"),
            AppLauncherItem("হটলাইন হেল্প", "📞", Screen.Contact, TealTint, "১৬১২২", "icon_contact"),
            AppLauncherItem("সেটিংস", "⚙️", Screen.Settings, EmeraldTint, "কন্ট্রোল", "icon_settings"),
            AppLauncherItem("সকল সেবা", "🗂️", Screen.AllServicesHub, LandGreenContainer, "সব ফাইল", "icon_all_services")
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.22f),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.4f)),
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Image(
                                    painter = painterResource(id = R.drawable.logo),
                                    contentDescription = "Logo",
                                    modifier = Modifier
                                        .size(30.dp)
                                        .clip(CircleShape)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f, fill = false)) {
                            Text(
                                text = stringResource(id = R.string.app_name),
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.5.sp,
                                color = Color.White,
                                letterSpacing = 0.4.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "স্মার্ট ডিজিটাল ভূমি সেবা ও এআই প্ল্যাটফর্ম",
                                fontSize = 10.sp,
                                color = GoldAccent,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { onNavigate(Screen.SummaryDashboard) }) {
                        Icon(Icons.Default.Dashboard, contentDescription = "Summary Dashboard", tint = Color.White)
                    }
                    IconButton(onClick = { onNavigate(Screen.AppQrCode) }) {
                        Icon(Icons.Default.QrCode2, contentDescription = "App QR Code", tint = Color.White)
                    }
                    IconButton(onClick = { onNavigate(Screen.Search) }) {
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.White)
                    }
                    Box {
                        IconButton(onClick = { showTopMenu = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "More Options", tint = Color.White)
                        }
                        DropdownMenu(
                            expanded = showTopMenu,
                            onDismissRequest = { showTopMenu = false },
                            modifier = Modifier.background(Color.White)
                        ) {
                            DropdownMenuItem(
                                text = { Text("📊 সার্ভিস সামারি ড্যাশবোর্ড", fontWeight = FontWeight.SemiBold, fontSize = 13.sp) },
                                onClick = {
                                    showTopMenu = false
                                    onNavigate(Screen.SummaryDashboard)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("🗂️ সকল সেবা ও বিস্তারিত ফাইল", fontWeight = FontWeight.SemiBold, fontSize = 13.sp) },
                                onClick = {
                                    showTopMenu = false
                                    onNavigate(Screen.AllServicesHub)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("💎 প্রিমিয়াম আমিন ও চুক্তি", fontWeight = FontWeight.SemiBold, fontSize = 13.sp) },
                                onClick = {
                                    showTopMenu = false
                                    onNavigate(Screen.PremiumServices)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("💳 বিকাশ ও নগদ পেমেন্ট", fontWeight = FontWeight.SemiBold, fontSize = 13.sp) },
                                onClick = {
                                    showTopMenu = false
                                    onNavigate(Screen.Payment)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("⚙️ অ্যাপ সেটিংস", fontWeight = FontWeight.SemiBold, fontSize = 13.sp) },
                                onClick = {
                                    showTopMenu = false
                                    onNavigate(Screen.Settings)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("ℹ️ অ্যাপ পরিচিতি", fontWeight = FontWeight.SemiBold, fontSize = 13.sp) },
                                onClick = {
                                    showTopMenu = false
                                    onNavigate(Screen.About)
                                }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = LandGreenDark,
                    titleContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            AiFloatingActionButton(
                onClick = {
                    currentAiCategory = AiCategory.GENERAL
                    showAiAssistant = true
                }
            )
        },
        bottomBar = {
            Surface(
                color = Color.White,
                shadowElevation = 8.dp,
                border = BorderStroke(1.dp, BorderColor)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Home Tab (Active)
                    Surface(
                        color = LandGreenContainer,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.padding(2.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("🏠", fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("হোম", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = LandGreenDark)
                        }
                    }

                    // Scanner Tab
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onNavigate(Screen.AiDocScanner) }
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text("📷", fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("স্ক্যানার", fontSize = 10.5.sp, fontWeight = FontWeight.Medium, color = TextSecondary)
                    }

                    // Search Tab
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onNavigate(Screen.Search) }
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text("🔍", fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("সার্চ", fontSize = 10.5.sp, fontWeight = FontWeight.Medium, color = TextSecondary)
                    }

                    // Contact Tab
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onNavigate(Screen.Contact) }
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text("📞", fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("যোগাযোগ", fontSize = 10.5.sp, fontWeight = FontWeight.Medium, color = TextSecondary)
                    }

                    // Hub Tab
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onNavigate(Screen.AllServicesHub) }
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text("🗂️", fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("সকল সেবা", fontSize = 10.5.sp, fontWeight = FontWeight.Medium, color = TextSecondary)
                    }
                }
            }
        }
    ) { innerPadding ->
        // First Page: ONLY Icons! Fast, clean, responsive icon launcher grid.
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(SurfaceLight),
            contentPadding = PaddingValues(start = 12.dp, end = 12.dp, top = 16.dp, bottom = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // All Services displayed purely as launcher icons
            items(launcherItems) { item ->
                AppLauncherIconTile(
                    item = item,
                    onClick = {
                        if (item.action != null) {
                            item.action.invoke()
                        } else if (item.screen != null) {
                            onNavigate(item.screen)
                        }
                    }
                )
            }

            // Shortcut banner to open all files, dashboards & details inside "সকল সেবা"
            item(span = { GridItemSpan(maxLineSpan) }) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(1.2.dp, LandGreenLight.copy(alpha = 0.8f)),
                    shadowElevation = 2.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigate(Screen.AllServicesHub) }
                        .testTag("all_services_hub_link")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = LandGreenContainer,
                                border = BorderStroke(1.dp, LandGreenPrimary),
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("🗂️", fontSize = 20.sp)
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "অন্য সকল ফাইল ও ড্যাশবোর্ড",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = LandGreenDark
                                )
                                Text(
                                    text = "নোটিশ, লাইভ স্ট্যাটাস, বিস্তারিত বিবরণ ও হাব দেখুন",
                                    fontSize = 10.5.sp,
                                    color = TextSecondary
                                )
                            }
                        }

                        Surface(
                            color = LandGreenPrimary,
                            shape = CircleShape
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "সকল সেবা",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(11.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // AI Land Assistant Modal Sheet
    if (showAiAssistant) {
        AiLandAssistantModal(
            initialCategory = currentAiCategory,
            onDismiss = { showAiAssistant = false }
        )
    }
}

/**
 * Highly responsive, accessible, clean App Launcher Icon Composable.
 * Features a 54dp rounded icon with dynamic tint, centered emoji, optional badge,
 * and high-contrast Bengali title below with touch targets >= 48dp.
 */
@Composable
fun AppLauncherIconTile(
    item: AppLauncherItem,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .testTag(item.testTag)
            .padding(vertical = 4.dp, horizontal = 2.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(54.dp)
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = RoundedCornerShape(15.dp),
                color = item.tintColor,
                border = BorderStroke(1.dp, BorderColor.copy(alpha = 0.7f)),
                shadowElevation = 2.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = item.emojiIcon, fontSize = 23.sp)
                }
            }
            if (item.badge != null) {
                Surface(
                    color = when (item.badge) {
                        "নতুন" -> Color(0xFFF59E0B)
                        "VIP", "PRO" -> GoldAccent
                        "পেমেন্ট" -> Color(0xFFF43F5E)
                        "Gemini" -> Color(0xFF6366F1)
                        else -> LandGreenPrimary
                    },
                    shape = RoundedCornerShape(6.dp),
                    shadowElevation = 2.dp,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 4.dp, y = (-2).dp)
                ) {
                    Text(
                        text = item.badge,
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (item.badge == "VIP" || item.badge == "PRO" || item.badge == "নতুন") Color.Black else Color.White,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = item.title,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            lineHeight = 13.5.sp,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
