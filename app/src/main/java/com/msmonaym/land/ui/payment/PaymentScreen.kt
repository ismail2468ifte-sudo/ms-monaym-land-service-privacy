package com.msmonaym.land.ui.payment

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.msmonaym.land.data.AdminManager
import com.msmonaym.land.data.UserProfileManager
import com.msmonaym.land.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

// Brand Colors for bKash and Nagad
private val BkashPink = Color(0xFFE2136E)
private val BkashPinkDark = Color(0xFFB00E55)
private val NagadOrange = Color(0xFFF7931E)
private val NagadOrangeDark = Color(0xFFD35400)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val adminManager = remember { AdminManager(context) }
    val profileManager = remember { UserProfileManager(context) }
    val adminPhone = remember { adminManager.getOfficialContactPhone() }

    var selectedTab by remember { mutableStateOf(0) } // 0: bKash, 1: Nagad, 2: Confirmation & Receipt

    // Form states for confirmation & receipt
    var customerName by remember { mutableStateOf(profileManager.getUserName()) }
    var customerPhone by remember { mutableStateOf(profileManager.getUserPhone()) }
    var selectedMethod by remember { mutableStateOf("বিকাশ (bKash)") }
    var amountPaid by remember { mutableStateOf("") }
    var servicePurpose by remember { mutableStateOf("জমি সার্ভে ও পরিমাপ ফি") }
    var transactionId by remember { mutableStateOf("") }
    var userNotes by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "বিকাশ ও নগদ সেন্ড মানি",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color.White
                        )
                        Text(
                            "এডমিন একাউন্ট: $adminPhone",
                            fontSize = 10.5.sp,
                            color = GoldAccent
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        sharePaymentDetails(context, adminPhone)
                    }) {
                        Icon(Icons.Default.Share, contentDescription = "Share Info", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = LandGreenPrimary)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF1F5F9)),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Master Admin Payment Gateway Header Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        LandGreenDark,
                                        Color(0xFF064E3B),
                                        Color(0xFF0F766E)
                                    )
                                )
                            )
                            .padding(18.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = GoldAccent,
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text("💳", fontSize = 18.sp)
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            "M.S MONAYM ENT.",
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 15.sp,
                                            color = Color.White
                                        )
                                        Text(
                                            "অফিসিয়াল মার্চেন্ট ও পার্সোনাল ওয়ালেট",
                                            fontSize = 10.sp,
                                            color = GoldAccent
                                        )
                                    }
                                }

                                Surface(
                                    color = Color(0xFF10B981).copy(alpha = 0.25f),
                                    shape = RoundedCornerShape(20.dp),
                                    border = BorderStroke(1.dp, Color(0xFF34D399))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("● ", color = Color(0xFF34D399), fontSize = 10.sp)
                                        Text("এক্টিভ", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                "এডমিনের বিকাশ ও নগদ পার্সোনাল নম্বর:",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                            Spacer(modifier = Modifier.height(4.dp))

                            // Highlighted Phone Number Box
                            Surface(
                                color = Color.Black.copy(alpha = 0.35f),
                                shape = RoundedCornerShape(14.dp),
                                border = BorderStroke(1.2.dp, GoldAccent),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        copyToClipboard(context, adminPhone, "এডমিনের বিকাশ ও নগদ নম্বর কপি করা হয়েছে")
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.Default.PhoneAndroid,
                                            contentDescription = null,
                                            tint = GoldAccent,
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(
                                            text = adminPhone,
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color.White,
                                            letterSpacing = 1.sp
                                        )
                                    }

                                    Button(
                                        onClick = {
                                            copyToClipboard(context, adminPhone, "এডমিনের বিকাশ ও নগদ নম্বর কপি করা হয়েছে")
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.ContentCopy,
                                            contentDescription = "Copy",
                                            tint = LandGreenDark,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            "কপি",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = LandGreenDark
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Quick actions: Direct Call & WhatsApp
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        try {
                                            val callIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$adminPhone"))
                                            context.startActivity(callIntent)
                                        } catch (e: Exception) {
                                            Toast.makeText(context, "কল ডায়াল করা যায়নি", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(40.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.5f)),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                                ) {
                                    Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(15.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("সরাসরি কল", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                                }

                                OutlinedButton(
                                    onClick = {
                                        openWhatsAppChat(context, adminPhone, "আসসালামু আলাইকুম, আমি M.S MONAYM ENT. অ্যাপ থেকে পেমেন্ট সংক্রান্ত তথ্য জানতে চাচ্ছি।")
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(40.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    border = BorderStroke(1.dp, Color(0xFF25D366)),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF25D366))
                                ) {
                                    Text("💬", fontSize = 13.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("হোয়াটসঅ্যাপ", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // Tab Selector (bKash / Nagad / Receipt)
            item {
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = Color.White,
                        contentColor = LandGreenPrimary,
                        divider = {}
                    ) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        color = if (selectedTab == 0) BkashPink else Color(0xFFFCE7F3),
                                        shape = CircleShape,
                                        modifier = Modifier.size(18.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text("ব", color = if (selectedTab == 0) Color.White else BkashPink, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("বিকাশ", fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal, fontSize = 13.sp)
                                }
                            }
                        )

                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        color = if (selectedTab == 1) NagadOrange else Color(0xFFFFF7ED),
                                        shape = CircleShape,
                                        modifier = Modifier.size(18.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text("ন", color = if (selectedTab == 1) Color.White else NagadOrange, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("নগদ", fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal, fontSize = 13.sp)
                                }
                            }
                        )

                        Tab(
                            selected = selectedTab == 2,
                            onClick = { selectedTab = 2 },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.ReceiptLong, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("রসিদ / নোটিফিকেশন", fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal, fontSize = 12.sp)
                                }
                            }
                        )
                    }
                }
            }

            // Tab 0: bKash (বিকাশ) Detailed Instructions & App Launcher
            if (selectedTab == 0) {
                item {
                    BkashSection(
                        adminPhone = adminPhone,
                        onOpenBkashApp = { openPaymentApp(context, "com.bKash.customerapp", "https://play.google.com/store/apps/details?id=com.bKash.customerapp") },
                        onDialUssd = { dialUssdCode(context, "*247#") }
                    )
                }
            }

            // Tab 1: Nagad (নগদ) Detailed Instructions & App Launcher
            if (selectedTab == 1) {
                item {
                    NagadSection(
                        adminPhone = adminPhone,
                        onOpenNagadApp = { openPaymentApp(context, "com.konasl.nagad", "https://play.google.com/store/apps/details?id=com.konasl.nagad") },
                        onDialUssd = { dialUssdCode(context, "*167#") }
                    )
                }
            }

            // Tab 2: Transaction Confirmation, Receipt & WhatsApp Notify
            if (selectedTab == 2) {
                item {
                    ReceiptAndNotifySection(
                        adminPhone = adminPhone,
                        customerName = customerName,
                        onCustomerNameChange = { customerName = it },
                        customerPhone = customerPhone,
                        onCustomerPhoneChange = { customerPhone = it },
                        selectedMethod = selectedMethod,
                        onMethodChange = { selectedMethod = it },
                        amountPaid = amountPaid,
                        onAmountChange = { amountPaid = it },
                        servicePurpose = servicePurpose,
                        onServicePurposeChange = { servicePurpose = it },
                        transactionId = transactionId,
                        onTransactionIdChange = { transactionId = it },
                        userNotes = userNotes,
                        onNotesChange = { userNotes = it }
                    )
                }
            }

            // Safety Guarantee Note
            item {
                Surface(
                    color = Color(0xFFFEFCE8),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFFFEF08A)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🛡️", fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "নিরাপদ ও নির্ভরযোগ্য লেনদেন",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = Color(0xFF854D0E)
                            )
                            Text(
                                "টাকা পাঠানোর পর ট্রানজেকশন আইডি (TrxID) সংরক্ষণ করুন অথবা 'রসিদ / নোটিফিকেশন' অপশন থেকে এডমিনকে সরাসরি মেসেজ পাঠিয়ে তাৎক্ষণিক কনফার্মেশন নিন।",
                                fontSize = 10.5.sp,
                                color = Color(0xFF713F12),
                                lineHeight = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BkashSection(
    adminPhone: String,
    onOpenBkashApp: () -> Unit,
    onDialUssd: () -> Unit
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // bKash Header Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = BkashPink,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.size(38.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("বিকাশ", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            "বিকাশ সেন্ড মানি (bKash Personal)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = BkashPinkDark
                        )
                        Text(
                            "পার্সোনাল বিকাশ একাউন্ট থেকে সেন্ড মানি করুন",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons for bKash
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onOpenBkashApp,
                    modifier = Modifier
                        .weight(1.2f)
                        .height(46.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BkashPink)
                ) {
                    Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("বিকাশ অ্যাপ ওপেন", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                OutlinedButton(
                    onClick = onDialUssd,
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.2.dp, BkashPink),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = BkashPink)
                ) {
                    Icon(Icons.Default.Dialpad, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("*247# ডায়াল", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Step by Step Instructions
            Text(
                "📌 বিকাশ দিয়ে যেভাবে সেন্ড মানি করবেন:",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = Color(0xFF1E293B)
            )
            Spacer(modifier = Modifier.height(10.dp))

            val bkashSteps = listOf(
                "১. আপনার ফোনের 'bKash App' চালু করুন অথবা *247# ডায়াল করুন।",
                "২. প্রধান মেনু থেকে 'Send Money' (সেন্ড মানি) অপশন নির্বাচন করুন।",
                "৩. প্রাপক নম্বর হিসেবে এডমিনের নম্বর ($adminPhone) লিখুন।",
                "৪. সেবার নির্দিষ্ট টাকার পরিমাণ (যেমন: 500, 1000) এবং রেফারেন্সে আপনার নাম বা কাজের নাম লিখুন।",
                "৫. আপনার গোপন বিকাশ পিন দিয়ে ট্যাপ করে ধরে রাখুন। সফল হলে প্রাপ্ত TrxID টি সংগ্রহে রাখুন।"
            )

            bkashSteps.forEach { step ->
                Surface(
                    color = Color(0xFFFDF2F8),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = step,
                            fontSize = 11.5.sp,
                            color = Color(0xFF831843),
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NagadSection(
    adminPhone: String,
    onOpenNagadApp: () -> Unit,
    onDialUssd: () -> Unit
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // Nagad Header Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = NagadOrange,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.size(38.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("নগদ", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            "নগদ সেন্ড মানি (Nagad Personal)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = NagadOrangeDark
                        )
                        Text(
                            "পার্সোনাল নগদ একাউন্ট থেকে সেন্ড মানি করুন",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons for Nagad
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onOpenNagadApp,
                    modifier = Modifier
                        .weight(1.2f)
                        .height(46.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NagadOrange)
                ) {
                    Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("নগদ অ্যাপ ওপেন", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                OutlinedButton(
                    onClick = onDialUssd,
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.2.dp, NagadOrange),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = NagadOrange)
                ) {
                    Icon(Icons.Default.Dialpad, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("*167# ডায়াল", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Step by Step Instructions
            Text(
                "📌 নগদ দিয়ে যেভাবে সেন্ড মানি করবেন:",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = Color(0xFF1E293B)
            )
            Spacer(modifier = Modifier.height(10.dp))

            val nagadSteps = listOf(
                "১. আপনার ফোনে 'Nagad App' চালু করুন অথবা *167# ডায়াল করুন।",
                "২. মেনু থেকে 'Send Money' (সেন্ড মানি) নির্বাচন করুন।",
                "৩. প্রাপক নম্বর হিসেবে এডমিনের নম্বর ($adminPhone) দিন।",
                "৪. টাকার পরিমাণ ও রেফারেন্সে আপনার নাম লিখুন।",
                "৫. আপনার গোপন নগদ পিন প্রদান করে সেন্ড মানি সম্পন্ন করুন এবং TrxID সংরক্ষণ করুন।"
            )

            nagadSteps.forEach { step ->
                Surface(
                    color = Color(0xFFFFF7ED),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = step,
                            fontSize = 11.5.sp,
                            color = Color(0xFF9A3412),
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ReceiptAndNotifySection(
    adminPhone: String,
    customerName: String,
    onCustomerNameChange: (String) -> Unit,
    customerPhone: String,
    onCustomerPhoneChange: (String) -> Unit,
    selectedMethod: String,
    onMethodChange: (String) -> Unit,
    amountPaid: String,
    onAmountChange: (String) -> Unit,
    servicePurpose: String,
    onServicePurposeChange: (String) -> Unit,
    transactionId: String,
    onTransactionIdChange: (String) -> Unit,
    userNotes: String,
    onNotesChange: (String) -> Unit
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = LandGreenPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "পেমেন্ট নিশ্চিতকরণ ও ডিজিটাল রসিদ ফর্ম",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = LandGreenDark
                )
            }
            Text(
                "টাকা পাঠানোর পর নিচের তথ্য পূরণ করে এডমিনকে সরাসরি মেসেজ পাঠান বা রসিদ ডাউনলোড করুন।",
                fontSize = 11.sp,
                color = Color(0xFF64748B)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Method Selector (bKash vs Nagad)
            Text("পেমেন্ট মাধ্যম নির্বাচন করুন:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF334155))
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    color = if (selectedMethod.contains("বিকাশ")) BkashPink else Color(0xFFFDF2F8),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, BkashPink),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onMethodChange("বিকাশ (bKash)") }
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            "বিকাশ (bKash)",
                            color = if (selectedMethod.contains("বিকাশ")) Color.White else BkashPink,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }

                Surface(
                    color = if (selectedMethod.contains("নগদ")) NagadOrange else Color(0xFFFFF7ED),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, NagadOrange),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onMethodChange("নগদ (Nagad)") }
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            "নগদ (Nagad)",
                            color = if (selectedMethod.contains("নগদ")) Color.White else NagadOrange,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Customer Name Input
            OutlinedTextField(
                value = customerName,
                onValueChange = onCustomerNameChange,
                label = { Text("আপনার নাম") },
                placeholder = { Text("যেমন: মোঃ করিম") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = LandGreenPrimary) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                textStyle = TextStyle(color = Color.Black, fontSize = 14.sp, fontWeight = FontWeight.Medium),
                colors = appTextFieldColors(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Customer Phone Input
            OutlinedTextField(
                value = customerPhone,
                onValueChange = onCustomerPhoneChange,
                label = { Text("যে নম্বর থেকে টাকা পাঠিয়েছেন") },
                placeholder = { Text("01XXXXXXXXX") },
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = LandGreenPrimary) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                textStyle = TextStyle(color = Color.Black, fontSize = 14.sp, fontWeight = FontWeight.Medium),
                colors = appTextFieldColors(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Amount Paid
            OutlinedTextField(
                value = amountPaid,
                onValueChange = onAmountChange,
                label = { Text("টাকার পরিমাণ (৳)") },
                placeholder = { Text("যেমন: 500") },
                leadingIcon = { Text("৳ ", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = LandGreenPrimary, modifier = Modifier.padding(start = 12.dp)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                textStyle = TextStyle(color = Color.Black, fontSize = 14.sp, fontWeight = FontWeight.Medium),
                colors = appTextFieldColors(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Service Purpose Selector / Input
            OutlinedTextField(
                value = servicePurpose,
                onValueChange = onServicePurposeChange,
                label = { Text("সেবার উদ্দেশ্য / কাজের নাম") },
                placeholder = { Text("যেমন: জমি মাপজোক ও সার্ভে ফি") },
                leadingIcon = { Icon(Icons.Default.Work, contentDescription = null, tint = LandGreenPrimary) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                textStyle = TextStyle(color = Color.Black, fontSize = 14.sp, fontWeight = FontWeight.Medium),
                colors = appTextFieldColors(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            // TrxID Input
            OutlinedTextField(
                value = transactionId,
                onValueChange = onTransactionIdChange,
                label = { Text("ট্রানজেকশন আইডি (TrxID)") },
                placeholder = { Text("যেমন: 9J87X6K1Q") },
                leadingIcon = { Icon(Icons.Default.VpnKey, contentDescription = null, tint = LandGreenPrimary) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                textStyle = TextStyle(color = Color.Black, fontSize = 14.sp, fontWeight = FontWeight.Medium),
                colors = appTextFieldColors(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Action: Notify Admin on WhatsApp
            Button(
                onClick = {
                    if (amountPaid.isBlank()) {
                        Toast.makeText(context, "টাকার পরিমাণ লিখুন", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    val msg = buildPaymentMessage(
                        adminPhone = adminPhone,
                        customerName = customerName,
                        customerPhone = customerPhone,
                        method = selectedMethod,
                        amount = amountPaid,
                        purpose = servicePurpose,
                        trxId = transactionId
                    )
                    openWhatsAppChat(context, adminPhone, msg)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366))
            ) {
                Text("💬", fontSize = 16.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text("হোয়াটসঅ্যাপে এডমিনকে জানান", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action: Send Direct SMS to Admin
            OutlinedButton(
                onClick = {
                    if (amountPaid.isBlank()) {
                        Toast.makeText(context, "টাকার পরিমাণ লিখুন", Toast.LENGTH_SHORT).show()
                        return@OutlinedButton
                    }
                    val msg = buildPaymentMessage(
                        adminPhone = adminPhone,
                        customerName = customerName,
                        customerPhone = customerPhone,
                        method = selectedMethod,
                        amount = amountPaid,
                        purpose = servicePurpose,
                        trxId = transactionId
                    )
                    sendSmsIntent(context, adminPhone, msg)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.2.dp, LandGreenPrimary),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = LandGreenPrimary)
            ) {
                Icon(Icons.Default.Sms, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("এসএমএস (SMS) এর মাধ্যমে জানান", fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action: Generate & Print Digital Slip
            Button(
                onClick = {
                    if (amountPaid.isBlank()) {
                        Toast.makeText(context, "অনুগ্রহ করে টাকার পরিমাণ লিখুন", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    printPaymentReceipt(
                        context = context,
                        adminPhone = adminPhone,
                        customerName = customerName.ifBlank { "গ্রাহক" },
                        customerPhone = customerPhone.ifBlank { "N/A" },
                        method = selectedMethod,
                        amount = amountPaid,
                        purpose = servicePurpose.ifBlank { "ডিজিটাল ভূমি সেবা" },
                        trxId = transactionId.ifBlank { "PENDING-${System.currentTimeMillis() % 100000}" }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = LandGreenDark)
            ) {
                Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("ডিজিটাল মানি রসিদ প্রিন্ট / PDF সেভ", fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
            }
        }
    }
}

private fun copyToClipboard(context: Context, text: String, message: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("Copied Text", text)
    clipboard.setPrimaryClip(clip)
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

private fun dialUssdCode(context: Context, ussdCode: String) {
    try {
        val encodedUssd = Uri.encode(ussdCode)
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$encodedUssd"))
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "ডায়াল করা যায়নি: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}

private fun openPaymentApp(context: Context, packageName: String, fallbackUrl: String) {
    try {
        val launchIntent = context.packageManager.getLaunchIntentForPackage(packageName)
        if (launchIntent != null) {
            context.startActivity(launchIntent)
        } else {
            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(fallbackUrl))
            context.startActivity(webIntent)
        }
    } catch (e: Exception) {
        try {
            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(fallbackUrl))
            context.startActivity(webIntent)
        } catch (e2: Exception) {
            Toast.makeText(context, "অ্যাপ ওপেন করা সম্ভব হয়নি", Toast.LENGTH_SHORT).show()
        }
    }
}

private fun openWhatsAppChat(context: Context, phone: String, message: String) {
    try {
        var cleanNumber = phone.replace("+", "").replace("-", "").replace(" ", "")
        if (cleanNumber.startsWith("0")) {
            cleanNumber = "88$cleanNumber"
        }
        val uri = Uri.parse("https://api.whatsapp.com/send?phone=$cleanNumber&text=${Uri.encode(message)}")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "হোয়াটসঅ্যাপ অ্যাপ পাওয়া যায়নি", Toast.LENGTH_SHORT).show()
    }
}

private fun sendSmsIntent(context: Context, phone: String, message: String) {
    try {
        val uri = Uri.parse("smsto:$phone")
        val intent = Intent(Intent.ACTION_SENDTO, uri).apply {
            putExtra("sms_body", message)
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "এসএমএস অ্যাপ ওপেন করা যায়নি", Toast.LENGTH_SHORT).show()
    }
}

private fun buildPaymentMessage(
    adminPhone: String,
    customerName: String,
    customerPhone: String,
    method: String,
    amount: String,
    purpose: String,
    trxId: String
): String {
    return """
        🏛️ *M.S MONAYM ENT. পেমেন্ট নোটিফিকেশন*
        
        👤 গ্রাহকের নাম: ${customerName.ifBlank { "N/A" }}
        📱 প্রেরক নম্বর: ${customerPhone.ifBlank { "N/A" }}
        💳 পেমেন্ট মাধ্যম: $method
        💰 টাকার পরিমাণ: ৳ $amount
        🎯 সেবার নাম: ${purpose.ifBlank { "ডিজিটাল ভূমি সেবা" }}
        🔑 TrxID: ${trxId.ifBlank { "N/A" }}
        
        অনুগ্রহ করে পেমেন্ট যাচাই করে সেবাটি নিশ্চিত করবেন। ধন্যবাদ।
    """.trimIndent()
}

private fun sharePaymentDetails(context: Context, adminPhone: String) {
    val shareText = """
        🏛️ M.S MONAYM ENT. - ডিজিটাল ভূমি সেবা
        
        💳 বিকাশ ও নগদ পার্সোনাল সেন্ড মানি নম্বর:
        👉 $adminPhone
        
        যে কোনো ভূমি সেবা, জমি সার্ভে, খতিয়ান ও পর্চা ফি পরিশোধের জন্য বিকাশ বা নগদ থেকে সেন্ড মানি করতে পারেন।
    """.trimIndent()

    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "M.S MONAYM ENT. পেমেন্ট তথ্য")
        putExtra(Intent.EXTRA_TEXT, shareText)
    }
    context.startActivity(Intent.createChooser(shareIntent, "পেমেন্ট তথ্য শেয়ার করুন"))
}

private fun printPaymentReceipt(
    context: Context,
    adminPhone: String,
    customerName: String,
    customerPhone: String,
    method: String,
    amount: String,
    purpose: String,
    trxId: String
) {
    try {
        val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        val currentDate = dateFormat.format(Date())

        val receiptHtml = """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>M.S MONAYM ENT. মানি রসিদ</title>
                <style>
                    body {
                        font-family: 'Segoe UI', Arial, sans-serif;
                        padding: 25px;
                        margin: 0;
                        background: #ffffff;
                        color: #1e293b;
                    }
                    .receipt-box {
                        max-width: 550px;
                        margin: 0 auto;
                        border: 2px solid #1B4D3E;
                        border-radius: 12px;
                        padding: 24px;
                        box-shadow: 0 4px 12px rgba(0,0,0,0.08);
                    }
                    .header {
                        text-align: center;
                        border-bottom: 2px dashed #1B4D3E;
                        padding-bottom: 12px;
                        margin-bottom: 16px;
                    }
                    .company-name {
                        font-size: 22px;
                        font-weight: bold;
                        color: #1B4D3E;
                    }
                    .sub-header {
                        font-size: 13px;
                        color: #0F766E;
                    }
                    .title {
                        display: inline-block;
                        background: #1B4D3E;
                        color: #ffffff;
                        padding: 4px 14px;
                        border-radius: 20px;
                        font-size: 13px;
                        font-weight: bold;
                        margin-top: 8px;
                    }
                    .info-table {
                        width: 100%;
                        border-collapse: collapse;
                        margin: 16px 0;
                    }
                    .info-table td {
                        padding: 8px 6px;
                        border-bottom: 1px solid #e2e8f0;
                        font-size: 13px;
                    }
                    .label {
                        color: #64748b;
                        width: 40%;
                    }
                    .value {
                        font-weight: bold;
                        color: #0f172a;
                    }
                    .amount-box {
                        background: #F0FDF4;
                        border: 1.5px solid #86EFAC;
                        border-radius: 8px;
                        padding: 12px;
                        text-align: center;
                        margin: 16px 0;
                    }
                    .amount-text {
                        font-size: 20px;
                        font-weight: bold;
                        color: #166534;
                    }
                    .footer {
                        text-align: center;
                        font-size: 11px;
                        color: #64748b;
                        margin-top: 20px;
                        border-top: 1px solid #e2e8f0;
                        padding-top: 10px;
                    }
                </style>
            </head>
            <body>
                <div class="receipt-box">
                    <div class="header">
                        <div class="company-name">🏛️ M.S MONAYM ENT.</div>
                        <div class="sub-header">ডিজিটাল ভূমি সেবা ও সার্ভে কনসালটেন্সি</div>
                        <div class="title">পেমেন্ট মানি রসিদ (Money Receipt)</div>
                    </div>
                    
                    <table class="info-table">
                        <tr>
                            <td class="label">তারিখ ও সময়:</td>
                            <td class="value">${currentDate}</td>
                        </tr>
                        <tr>
                            <td class="label">গ্রাহকের নাম:</td>
                            <td class="value">${customerName}</td>
                        </tr>
                        <tr>
                            <td class="label">প্রেরক মোবাইল:</td>
                            <td class="value">${customerPhone}</td>
                        </tr>
                        <tr>
                            <td class="label">পেমেন্ট মাধ্যম:</td>
                            <td class="value">${method}</td>
                        </tr>
                        <tr>
                            <td class="label">প্রাপক এডমিন নম্বর:</td>
                            <td class="value">${adminPhone}</td>
                        </tr>
                        <tr>
                            <td class="label">সেবার নাম / উদ্দেশ্য:</td>
                            <td class="value">${purpose}</td>
                        </tr>
                        <tr>
                            <td class="label">ট্রানজেকশন আইডি (TrxID):</td>
                            <td class="value" style="color: #0284C7;">${trxId}</td>
                        </tr>
                    </table>
                    
                    <div class="amount-box">
                        <div style="font-size: 12px; color: #15803D;">পরিশোধিত টাকার পরিমাণ</div>
                        <div class="amount-text">৳ ${amount} টাকা</div>
                    </div>
                    
                    <div class="footer">
                        এটি একটি স্বয়ংক্রিয় ডিজিটাল রসিদ। যে কোনো প্রয়োজনে যোগাযোগ: ${adminPhone}
                    </div>
                </div>
            </body>
            </html>
        """.trimIndent()

        val webView = WebView(context)
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                val printManager = context.getSystemService(Context.PRINT_SERVICE) as? android.print.PrintManager
                val printAdapter = webView.createPrintDocumentAdapter("MS_Monaym_Payment_Receipt")
                val printAttributes = android.print.PrintAttributes.Builder()
                    .setMediaSize(android.print.PrintAttributes.MediaSize.ISO_A4)
                    .setResolution(android.print.PrintAttributes.Resolution("res1", "default", 300, 300))
                    .setMinMargins(android.print.PrintAttributes.Margins.NO_MARGINS)
                    .build()
                printManager?.print("MS Monaym Payment Receipt", printAdapter, printAttributes)
            }
        }
        webView.loadDataWithBaseURL(null, receiptHtml, "text/html", "UTF-8", null)
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "রসিদ প্রিন্ট করা যায়নি: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}
