package com.msmonaym.land.ui.services

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.msmonaym.land.data.AdminManager
import com.msmonaym.land.data.MonetizationManager
import com.msmonaym.land.data.UserProfileManager
import com.msmonaym.land.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PremiumServicesScreen(
    onNavigateToPayment: () -> Unit = {},
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val adminManager = remember { AdminManager(context) }
    val monetizationManager = remember { MonetizationManager(context) }
    val profileManager = remember { UserProfileManager(context) }

    val adminPhone = remember { adminManager.getOfficialContactPhone() }

    var selectedServiceForBooking by remember { mutableStateOf<PremiumServiceItem?>(null) }
    var customerName by remember { mutableStateOf(profileManager.getUserName()) }
    var customerPhone by remember { mutableStateOf(profileManager.getUserPhone()) }
    var customerLocation by remember { mutableStateOf("") }
    var serviceDetailsNote by remember { mutableStateOf("") }

    val services = listOf(
        PremiumServiceItem(
            id = "survey_field",
            title = "বিশেষজ্ঞ ডিজিটাল সার্ভেয়ার / আমিন বুকিং",
            shortDesc = "সরেজমিনে অভিজ্ঞ আমিন দ্বারা সীমানা নির্ধারণ, বন্টন ও সঠিক পরিমাপ।",
            fee = monetizationManager.getSurveyFieldFee(),
            iconEmoji = "📐",
            badge = "জনপ্রিয় সেবা",
            badgeColor = Color(0xFF10B981),
            features = listOf(
                "ডিজিটাল ফিতা ও থিওডোলাইট/টোটাল স্টেশন ভিত্তিক নির্ভুল পরিমাপ",
                "সীমানা বিরোধ নিষ্পত্তি ও অংশীদারদের অংশ বণ্টন",
                "অফিসিয়াল নকশা মিলিয়ে সীমানা দাগ নির্ধারণ",
                "সার্ভে শেষে তাৎক্ষণিক ফিল্ড স্কেচ ও হস্তাক্ষরিত রিপোর্ট প্রদান"
            )
        ),
        PremiumServiceItem(
            id = "legal_consult",
            title = "ভূমি আইন ও দলিল যাচাই স্পেশাল কনসালটেন্সি",
            shortDesc = "জমি কেনা-বেচার পূর্বে স্বত্ব, সিএস-এসএ-আরএস খতিয়ান ও দলিল লিগ্যাল ভেটিং।",
            fee = monetizationManager.getConsultationFee(),
            iconEmoji = "⚖️",
            badge = "তাৎক্ষণিক কল",
            badgeColor = GoldAccent,
            features = listOf(
                "রেকর্ডীয় মালিকানা ও ওয়ারিশান হিস্যা যাচাই",
                "জাল দলিল ও মামলার ঝুঁকি এনালাইসিস",
                "নামজারি জট ও ডিসিআর সংক্রান্ত জটিলতার আইনি সমাধান",
                "টেলিফোন বা হোয়াটসঅ্যাপে বিশেষজ্ঞ আইনজীবীর সরাসরি পরামর্শ"
            )
        ),
        PremiumServiceItem(
            id = "doc_draft",
            title = "বায়নানামা, চুক্তিপত্র ও এফিডেভিট ড্রাফটিং",
            shortDesc = "সরকারি ফরম্যাট অনুযায়ী জমি ক্রয়-বিক্রয় ও বায়নানামা দলিল ড্রাফট তৈরি।",
            fee = monetizationManager.getDocDraftFee(),
            iconEmoji = "📝",
            badge = "ডিজিটাল কপি",
            badgeColor = Color(0xFF6366F1),
            features = listOf(
                "কোর্ট ও সাব-রেজিস্ট্রার স্বীকৃত স্ট্যান্ডার্ড আইনি ভাষা",
                "সকল প্রয়োজনীয় শর্তাবলী ও টাকার কিস্তি বিন্যাস",
                "Word (DOCX) ও প্রিন্টযোগ্য PDF কপি প্রদান",
                "প্রয়োজনে সংশোধন ও কাস্টমাইজেশন সুবিধা"
            )
        ),
        PremiumServiceItem(
            id = "khatian_check",
            title = "অনলাইন খতিয়ান, পর্চা ও দাগের তথ্য বের করা",
            shortDesc = "খতিয়ান নম্বর বা দাগ দিয়ে জটিল রেকর্ড উদ্ধার ও অফিশিয়াল প্রিন্ট কপি সহায়তা।",
            fee = monetizationManager.getKhatianCheckFee(),
            iconEmoji = "📜",
            badge = "দ্রুত ডেলিভারি",
            badgeColor = Color(0xFFF59E0B),
            features = listOf(
                "খতিয়ানের সঠিক মালিকানা ও হিস্যা যাচাই",
                "অনলাইনে মিসিং দাগের তথ্য অনুসন্ধান",
                "ই-পর্চার জন্য সরকারি ডাটাবেজে সঠিক আবেদন প্রস্তুতি",
                "জরুরি ভিত্তিতে হোয়াটসঅ্যাপে রেজাল্ট পাঠানো"
            )
        ),
        PremiumServiceItem(
            id = "full_report",
            title = "অফিসিয়াল ডিজিটাল সার্ভে রিপোর্ট ও সিএস-আরএস তুলনামূলক চার্ট",
            shortDesc = "রং ও মাপসহ A4 সাইজ পূর্ণাঙ্গ সার্ভে রিপোর্ট ও নকশা অ্যানালাইসিস।",
            fee = monetizationManager.getReportDownloadFee(),
            iconEmoji = "📊",
            badge = "অফিসিয়াল সিল",
            badgeColor = Color(0xFFEC4899),
            features = listOf(
                "জমির চতুর্দিকের বাহুর মাপ (ফুট/ইঞ্চি/লিংক)",
                "অক্ষাংশ-দ্রাঘিমাংশ (GPS Coordinates) সংযুক্তি",
                "সার্ভেয়ারের নাম ও রেজিস্ট্রেশন সিল সম্বলিত ডকুমেন্ট",
                "কোর্টে বা সাব-রেজিস্ট্রিতে উপস্থাপনের উপযোগী ফরম্যাট"
            )
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "প্রিমিয়াম ভূমি সেবা ও কনসালটেন্সি",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color.White
                        )
                        Text(
                            "M.S MONAYM ENT. অফিসিয়াল সার্ভিস বুকিং",
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
                    IconButton(onClick = onNavigateToPayment) {
                        Icon(Icons.Default.AccountBalanceWallet, contentDescription = "Payment", tint = GoldAccent)
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
                .background(Color(0xFFF8FAFC)),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hero Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                    elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
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
                                Surface(
                                    color = GoldAccent,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        "💎 VIP প্রিমিয়াম সেবা",
                                        color = LandGreenDark,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }

                                Text(
                                    "হেল্পলাইন: $adminPhone",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                "অভিজ্ঞ ভূমি বিশেষজ্ঞ ও আমিন দ্বারা সরাসরি সেবা নিন",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                lineHeight = 22.sp
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                "আপনার জমি সংক্রান্ত যেকোনো জটিলতা, সীমানা বিরোধ নিষ্পত্তি, বায়নানামা তৈরি বা ফিল্ড সার্ভে করার জন্য নিচে বুকিং করুন।",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.9f),
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }

            // Quick Direct Pay Bar
            item {
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.2.dp, Color(0xFFE2136E).copy(alpha = 0.4f)),
                    shadowElevation = 2.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToPayment() }
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = Color(0xFFFDF2F8),
                            shape = CircleShape,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("💳", fontSize = 20.sp)
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "বিকাশ ও নগদ দিয়ে সরাসরি পেমেন্ট করুন",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.5.sp,
                                color = LandGreenDark
                            )
                            Text(
                                "এডমিন একাউন্ট ($adminPhone) এ সেন্ড মানি করে রসিদ পান",
                                fontSize = 11.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                        Surface(
                            color = Color(0xFFE2136E),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                "পেমেন্ট",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }
                }
            }

            // Available Services List
            items(services) { service ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = LandGreenPrimary.copy(alpha = 0.1f),
                                    modifier = Modifier.size(44.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(service.iconEmoji, fontSize = 22.sp)
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        service.title,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = LandGreenDark
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Surface(
                                        color = service.badgeColor.copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            service.badge,
                                            color = service.badgeColor,
                                            fontSize = 9.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }

                            // Price Tag
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    "ফি শুরু",
                                    fontSize = 10.sp,
                                    color = Color(0xFF94A3B8)
                                )
                                Text(
                                    "৳${service.fee}",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = LandGreenPrimary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            service.shortDesc,
                            fontSize = 11.5.sp,
                            color = Color(0xFF475569),
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        // Feature bullets
                        service.features.forEach { feat ->
                            Row(
                                modifier = Modifier.padding(vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("✓", color = Color(0xFF10B981), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(feat, fontSize = 11.sp, color = Color(0xFF334155))
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Action Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    val msg = "আসসালামু আলাইকুম এডমিন, আমি '${service.title}' সেবাটি সম্পর্কে জানতে ও বুকিং করতে চাই। (ফি: ৳${service.fee})"
                                    openWhatsAppChat(context, adminPhone, msg)
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(42.dp),
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, Color(0xFF25D366)),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF25D366))
                            ) {
                                Text("💬", fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("হোয়াটসঅ্যাপে আলাপ", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = {
                                    selectedServiceForBooking = service
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(42.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = LandGreenDark)
                            ) {
                                Icon(Icons.Default.EventAvailable, contentDescription = null, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("সরাসরি বুকিং", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Bottom trust notice
            item {
                Surface(
                    color = Color(0xFFEFF6FF),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFFBFDBFE)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🛡️", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            "সকল সেবা সরকারি নিয়ম ও অভিজ্ঞ ভূমি সার্ভেয়ার ও আইনি পরামর্শকদের তত্ত্বাবধানে প্রদান করা হয়। শতভাগ বিশ্বস্ত ও নির্ভরতার নিশ্চয়তা।",
                            fontSize = 11.sp,
                            color = Color(0xFF1E40AF),
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }
    }

    // Direct Booking Dialog
    if (selectedServiceForBooking != null) {
        val s = selectedServiceForBooking!!
        AlertDialog(
            onDismissRequest = { selectedServiceForBooking = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(s.iconEmoji, fontSize = 22.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "বুকিং আবেদন: ${s.title}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = LandGreenDark
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        color = GoldAccent.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("নির্ধারিত সেবা ফি:", fontSize = 11.5.sp, color = Color(0xFF854D0E))
                            Text("৳${s.fee}", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = LandGreenPrimary)
                        }
                    }

                    OutlinedTextField(
                        value = customerName,
                        onValueChange = { customerName = it },
                        label = { Text("আপনার নাম") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true,
                        textStyle = TextStyle(color = Color.Black, fontSize = 13.5.sp, fontWeight = FontWeight.Medium),
                        colors = appTextFieldColors()
                    )

                    OutlinedTextField(
                        value = customerPhone,
                        onValueChange = { customerPhone = it },
                        label = { Text("মোবাইল নম্বর") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        textStyle = TextStyle(color = Color.Black, fontSize = 13.5.sp, fontWeight = FontWeight.Medium),
                        colors = appTextFieldColors()
                    )

                    OutlinedTextField(
                        value = customerLocation,
                        onValueChange = { customerLocation = it },
                        label = { Text("জমির ঠিকানা / এলাকা (জেলা, উপজেলা)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true,
                        textStyle = TextStyle(color = Color.Black, fontSize = 13.5.sp, fontWeight = FontWeight.Medium),
                        colors = appTextFieldColors()
                    )

                    OutlinedTextField(
                        value = serviceDetailsNote,
                        onValueChange = { serviceDetailsNote = it },
                        label = { Text("কাজের বিস্তারিত বিবরণ (ঐচ্ছিক)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        maxLines = 3,
                        textStyle = TextStyle(color = Color.Black, fontSize = 13.5.sp, fontWeight = FontWeight.Medium),
                        colors = appTextFieldColors()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (customerPhone.isBlank() || customerName.isBlank()) {
                            Toast.makeText(context, "নাম ও মোবাইল নম্বর পূরণ করুন", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        val bookingMsg = buildString {
                            appendLine("📋 *নতুন প্রিমিয়াম সেবা বুকিং আবেদন*")
                            appendLine("━━━━━━━━━━━━━━━━━━━")
                            appendLine("📌 *সেবার নাম:* ${s.title}")
                            appendLine("💰 *সেবা ফি:* ৳${s.fee}")
                            appendLine("👤 *গ্রাহকের নাম:* $customerName")
                            appendLine("📞 *ফোন নম্বর:* $customerPhone")
                            if (customerLocation.isNotBlank()) {
                                appendLine("📍 *জমির এলাকা:* $customerLocation")
                            }
                            if (serviceDetailsNote.isNotBlank()) {
                                appendLine("📝 *বিবরণ:* $serviceDetailsNote")
                            }
                            appendLine("━━━━━━━━━━━━━━━━━━━")
                            appendLine("M.S MONAYM ENT. ডিজিটাল অ্যাপের মাধ্যমে পাঠানো হয়েছে।")
                        }
                        openWhatsAppChat(context, adminPhone, bookingMsg)
                        selectedServiceForBooking = null
                        Toast.makeText(context, "বুকিং তথ্য এডমিনের কাছে পাঠানো হচ্ছে...", Toast.LENGTH_LONG).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = LandGreenPrimary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("কনফার্ম ও পাঠান")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedServiceForBooking = null }) {
                    Text("বাতিল", color = Color.Gray)
                }
            }
        )
    }
}

data class PremiumServiceItem(
    val id: String,
    val title: String,
    val shortDesc: String,
    val fee: String,
    val iconEmoji: String,
    val badge: String,
    val badgeColor: Color,
    val features: List<String>
)

private fun openWhatsAppChat(context: Context, phone: String, message: String) {
    try {
        val cleanPhone = if (phone.startsWith("0")) "+880" + phone.substring(1) else phone
        val url = "https://api.whatsapp.com/send?phone=$cleanPhone&text=${Uri.encode(message)}"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "হোয়াটসঅ্যাপ চালু করা সম্ভব হয়নি", Toast.LENGTH_SHORT).show()
    }
}
