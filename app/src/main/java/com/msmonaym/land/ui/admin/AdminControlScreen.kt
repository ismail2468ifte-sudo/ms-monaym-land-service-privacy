package com.msmonaym.land.ui.admin

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.msmonaym.land.data.AdminManager
import com.msmonaym.land.ui.theme.*

data class AdminStepItem(
    val stepNumber: String,
    val title: String,
    val summary: String,
    val keyPoints: List<String>,
    val badge: String = "ধাপ"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminControlScreen(
    onNavigateToEarnings: () -> Unit = {},
    onNavigateToPremiumServices: () -> Unit = {},
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val adminManager = remember { AdminManager(context) }

    var isAuthenticated by remember { mutableStateOf(false) }
    var enteredPasskey by remember { mutableStateOf("") }
    var authError by remember { mutableStateOf("") }

    // Admin State values
    var isPublishLockActive by remember { mutableStateOf(adminManager.isPublishProtectionEnabled()) }
    var isPublicModLocked by remember { mutableStateOf(adminManager.isLockedForPublic()) }
    var announcementText by remember { mutableStateOf(adminManager.getAppAnnouncement()) }
    var adminEmail by remember { mutableStateOf(adminManager.getAdminEmail()) }
    var contactPhone by remember { mutableStateOf(adminManager.getOfficialContactPhone()) }

    var showChangePasskeyDialog by remember { mutableStateOf(false) }
    var newPasskeyInput by remember { mutableStateOf("") }
    var confirmPasskeyInput by remember { mutableStateOf("") }
    var passkeyDialogError by remember { mutableStateOf("") }

    var selectedTab by remember { mutableIntStateOf(0) } // 0: কন্ট্রোল প্যানেল, 1: ধাপে ধাপে গাইড

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "মাস্টার এডমিন কন্ট্রোল ও গাইড",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = if (isAuthenticated) GoldAccent else Color(0xFFEF4444),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    if (isAuthenticated) "MASTER ADMIN" else "RESTRICTED",
                                    fontSize = 8.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                        Text(
                            "অ্যাপের সর্বময় ক্ষমতা ও পাবলিশ-পরবর্তী সুরক্ষা গাইডলাইন",
                            fontSize = 10.sp,
                            color = GoldAccent
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = LandGreenPrimary)
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF1F5F9))
        ) {
            if (!isAuthenticated) {
                // Admin Authentication Gatekeeper Screen
                AdminLoginGatekeeper(
                    enteredPasskey = enteredPasskey,
                    onPasskeyChange = {
                        enteredPasskey = it
                        authError = ""
                    },
                    authError = authError,
                    onAuthenticate = {
                        if (adminManager.verifyAdminPasskey(enteredPasskey)) {
                            isAuthenticated = true
                            authError = ""
                            Toast.makeText(context, "মাস্টার এডমিন লগইন সফল হয়েছে!", Toast.LENGTH_SHORT).show()
                        } else {
                            authError = "ভুল মাস্টার পাসকি! শুধুমাত্র মূল এডমিন প্রবেশ করতে পারবেন।"
                        }
                    }
                )
            } else {
                // Main Authenticated Admin Control & Step-by-Step Guide
                Column(modifier = Modifier.fillMaxSize()) {
                    // Top Tab Selection
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = Color.White,
                        contentColor = LandGreenPrimary
                    ) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.AdminPanelSettings, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("এডমিন কন্ট্রোল প্যানেল", fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
                                }
                            }
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("ধাপে ধাপে নির্দেশিকা", fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
                                }
                            }
                        )
                    }

                    if (selectedTab == 0) {
                        AdminSettingsPanel(
                            adminManager = adminManager,
                            isPublishLockActive = isPublishLockActive,
                            onTogglePublishLock = {
                                isPublishLockActive = it
                                adminManager.setPublishProtectionEnabled(it)
                                Toast.makeText(context, if (it) "পাবলিশ সুরক্ষা সক্রিয় করা হয়েছে" else "পাবলিশ সুরক্ষা শিথিল করা হয়েছে", Toast.LENGTH_SHORT).show()
                            },
                            isPublicModLocked = isPublicModLocked,
                            onTogglePublicModLock = {
                                isPublicModLocked = it
                                adminManager.setLockedForPublic(it)
                                Toast.makeText(context, if (it) "সাধারণ ব্যবহারকারীর পরিবর্তন লক করা হয়েছে" else "ব্যবহারকারীর পরিবর্তন উন্মুক্ত করা হয়েছে", Toast.LENGTH_SHORT).show()
                            },
                            announcementText = announcementText,
                            onAnnouncementChange = { announcementText = it },
                            onSaveAnnouncement = {
                                adminManager.setAppAnnouncement(announcementText)
                                Toast.makeText(context, "অ্যাপ ঘোষণা বার্তা সফলভাবে সংরক্ষিত হয়েছে!", Toast.LENGTH_SHORT).show()
                            },
                            adminEmail = adminEmail,
                            onAdminEmailChange = { adminEmail = it },
                            contactPhone = contactPhone,
                            onContactPhoneChange = { contactPhone = it },
                            onSaveContacts = {
                                adminManager.setAdminEmail(adminEmail)
                                adminManager.setOfficialContactPhone(contactPhone)
                                Toast.makeText(context, "এডমিন কন্টাক্ট তথ্য সফলভাবে সংরক্ষিত হয়েছে!", Toast.LENGTH_SHORT).show()
                            },
                            onOpenChangePasskey = {
                                newPasskeyInput = ""
                                confirmPasskeyInput = ""
                                passkeyDialogError = ""
                                showChangePasskeyDialog = true
                            },
                            onNavigateToEarnings = onNavigateToEarnings,
                            onNavigateToPremiumServices = onNavigateToPremiumServices
                        )
                    } else {
                        AdminStepByStepGuideView(context = context)
                    }
                }
            }
        }
    }

    // Change Admin Passkey Dialog
    if (showChangePasskeyDialog) {
        AlertDialog(
            onDismissRequest = { showChangePasskeyDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.VpnKey, contentDescription = null, tint = LandGreenPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("মাস্টার পাসকি পরিবর্তন করুন", fontWeight = FontWeight.Bold, color = LandGreenDark, fontSize = 16.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("নতুন মাস্টার এডমিন পাসকি প্রদান করুন (কমপক্ষে ৪ ডিজিট):", fontSize = 12.sp, color = Color.Gray)

                    OutlinedTextField(
                        value = newPasskeyInput,
                        onValueChange = {
                            newPasskeyInput = it
                            passkeyDialogError = ""
                        },
                        label = { Text("নতুন পাসকি") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = TextStyle(color = Color.Black, fontSize = 15.sp, fontWeight = FontWeight.Bold),
                        colors = appTextFieldColors()
                    )

                    OutlinedTextField(
                        value = confirmPasskeyInput,
                        onValueChange = {
                            confirmPasskeyInput = it
                            passkeyDialogError = ""
                        },
                        label = { Text("পাসকি পুনরায় নিশ্চিত করুন") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = TextStyle(color = Color.Black, fontSize = 15.sp, fontWeight = FontWeight.Bold),
                        colors = appTextFieldColors()
                    )

                    if (passkeyDialogError.isNotEmpty()) {
                        Text(
                            text = passkeyDialogError,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newPasskeyInput.trim().length < 4) {
                            passkeyDialogError = "পাসকি কমপক্ষে ৪ সংখ্যার হতে হবে।"
                        } else if (newPasskeyInput.trim() != confirmPasskeyInput.trim()) {
                            passkeyDialogError = "উভয় পাসকি এক নয়! মিলিয়ে লিখুন।"
                        } else {
                            adminManager.setAdminPasskey(newPasskeyInput.trim())
                            showChangePasskeyDialog = false
                            Toast.makeText(context, "মাস্টার পাসকি সফলভাবে আপডেট করা হয়েছে!", Toast.LENGTH_LONG).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = LandGreenPrimary)
                ) {
                    Text("সংরক্ষণ করুন")
                }
            },
            dismissButton = {
                TextButton(onClick = { showChangePasskeyDialog = false }) {
                    Text("বাতিল")
                }
            }
        )
    }
}

@Composable
fun AdminLoginGatekeeper(
    enteredPasskey: String,
    onPasskeyChange: (String) -> Unit,
    authError: String,
    onAuthenticate: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    shape = CircleShape,
                    color = LandGreenPrimary.copy(alpha = 0.12f),
                    modifier = Modifier.size(68.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.Shield,
                            contentDescription = null,
                            tint = LandGreenPrimary,
                            modifier = Modifier.size(38.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "মাস্টার এডমিন সুরক্ষা ভল্ট",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                    color = LandGreenDark
                )

                Text(
                    text = "এই অংশটি শুধুমাত্র অ্যাপ্লিকেশন স্বত্বাধিকারী/এডমিনের জন্য সংরক্ষিত। অ্যাপ পাবলিশের পর অন্য কোনো ব্যবহারকারী এই সেটিংস পরিবর্তন করতে পারবে না।",
                    fontSize = 11.5.sp,
                    color = Color.Gray,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                )

                OutlinedTextField(
                    value = enteredPasskey,
                    onValueChange = onPasskeyChange,
                    label = { Text("মাস্টার এডমিন সিক্রেট পাসকি") },
                    placeholder = { Text("পাসকি লিখুন...") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    leadingIcon = {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = LandGreenPrimary)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    textStyle = TextStyle(color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.Bold),
                    colors = appTextFieldColors()
                )

                if (authError.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = authError,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = onAuthenticate,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = LandGreenPrimary)
                ) {
                    Icon(Icons.Default.Key, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("এডমিন প্যানেলে প্রবেশ করুন", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    color = Color(0xFFFEF3C7),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, GoldAccent)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("💡 ডিফল্ট মাস্টার পাসকি: ", fontSize = 11.sp, color = Color(0xFF92400E))
                        Text("998877", fontWeight = FontWeight.Bold, fontSize = 11.5.sp, color = Color(0xFFB45309))
                    }
                }
            }
        }
    }
}

@Composable
fun AdminSettingsPanel(
    adminManager: AdminManager,
    isPublishLockActive: Boolean,
    onTogglePublishLock: (Boolean) -> Unit,
    isPublicModLocked: Boolean,
    onTogglePublicModLock: (Boolean) -> Unit,
    announcementText: String,
    onAnnouncementChange: (String) -> Unit,
    onSaveAnnouncement: () -> Unit,
    adminEmail: String,
    onAdminEmailChange: (String) -> Unit,
    contactPhone: String,
    onContactPhoneChange: (String) -> Unit,
    onSaveContacts: () -> Unit,
    onOpenChangePasskey: () -> Unit,
    onNavigateToEarnings: () -> Unit = {},
    onNavigateToPremiumServices: () -> Unit = {}
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Daily Earning & Monetization Control Hub (High Priority)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.5.dp, GoldAccent),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = GoldAccent.copy(alpha = 0.2f),
                                modifier = Modifier.size(38.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("💰", fontSize = 20.sp)
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    "দৈনিক আয় ও রেভিনিউ ম্যানেজমেন্ট",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.5.sp,
                                    color = LandGreenDark
                                )
                                Text(
                                    "সার্ভিস ফি, রেট চার্ট ও লাইভ রেভিনিউ লগ",
                                    fontSize = 10.5.sp,
                                    color = Color(0xFF64748B)
                                )
                            }
                        }
                        Surface(
                            color = Color(0xFF10B981),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                "সক্রিয়",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        "অ্যাপ পাবলিশ হওয়ার পর আপনার বিকাশ/নগদ অ্যাকাউন্টে সরাসরি ক্লায়েন্টের সার্ভিস পেমেন্ট আসবে। আপনি যে কোনো সময় সব সেবার ফি বাড়াতে বা কমাতে পারেন।",
                        fontSize = 11.5.sp,
                        color = Color(0xFF334155),
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = onNavigateToEarnings,
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = LandGreenDark)
                        ) {
                            Icon(Icons.Default.MonetizationOn, contentDescription = null, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("আয় ও ফি কন্ট্রোল", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = onNavigateToPremiumServices,
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, LandGreenPrimary),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = LandGreenPrimary)
                        ) {
                            Text("💎", fontSize = 13.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("পাবলিক বুকিং ভিউ", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Status Overview Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = LandGreenDark),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("🛡️ সার্বিক নিয়ন্ত্রণ স্থিতি", color = GoldAccent, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text("মাস্টার এডমিন এক্সেস সক্রিয়", color = Color.White.copy(alpha = 0.85f), fontSize = 11.sp)
                        }
                        Surface(
                            color = Color(0xFF10B981),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                "FULL CONTROL",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        "অ্যাপ্লিকেশনটি গুগল প্লে স্টোর বা অন্য যে কোনো মাধ্যমে পাবলিশ করার পর একমাত্র আপনি (মাস্টার এডমিন) ছাড়া কেউ এই সেটিং ও কনফিগারেশন পরিবর্তন করতে পারবে না।",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 11.5.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // Security & Publication Locks
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = LandGreenPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("পাবলিকেশন ও সিকিউরিটি লক", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = LandGreenDark)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Switch 1: Protect modification post-publication
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("পাবলিশ পরবর্তী সুরক্ষা লক (Publish Lock)", fontWeight = FontWeight.SemiBold, fontSize = 13.5.sp)
                            Text("সাধারণ ইউজারদের জন্য সমস্ত কোড ও সিস্টেম রুলস সুরক্ষিত রাখবে", fontSize = 11.sp, color = Color.Gray)
                        }
                        Switch(
                            checked = isPublishLockActive,
                            onCheckedChange = onTogglePublishLock,
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = LandGreenPrimary)
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = Color(0xFFE2E8F0))

                    // Switch 2: Lock general profile/config tampering
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("সিস্টেম আর্কিটেকচার সুরক্ষা", fontWeight = FontWeight.SemiBold, fontSize = 13.5.sp)
                            Text("এডমিন কনফিগারেশন ছাড়া অন্যান্য কোর মডিউলে অননুমোদিত হস্তক্ষেপ রোধ", fontSize = 11.sp, color = Color.Gray)
                        }
                        Switch(
                            checked = isPublicModLocked,
                            onCheckedChange = onTogglePublicModLock,
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = LandGreenPrimary)
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = Color(0xFFE2E8F0))

                    Button(
                        onClick = onOpenChangePasskey,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F766E)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.VpnKey, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("মাস্টার এডমিন পাসকি পরিবর্তন করুন")
                    }
                }
            }
        }

        // Live App Announcement Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Campaign, contentDescription = null, tint = LandGreenPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("অ্যাপ ব্যানার ও ঘোষণা বার্তা", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = LandGreenDark)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("এডমিন হিসেবে আপনি যে বার্তাটি লিখবেন তা অ্যাপের সমস্ত ব্যবহারকারীর কাছে প্রদর্শিত হবে:", fontSize = 11.5.sp, color = Color.Gray)

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = announcementText,
                        onValueChange = onAnnouncementChange,
                        label = { Text("এডমিন অফিসিয়াল নোটিশ") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        maxLines = 5,
                        shape = RoundedCornerShape(10.dp),
                        textStyle = TextStyle(color = Color.Black, fontSize = 14.sp, fontWeight = FontWeight.Normal),
                        colors = appTextFieldColors()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = onSaveAnnouncement,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = LandGreenPrimary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("ঘোষণা বার্তা আপডেট করুন")
                    }
                }
            }
        }

        // Official Admin Contact & Ownership Management
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = LandGreenPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("এডমিন মালিকানা ও হেল্পলাইন তথ্য", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = LandGreenDark)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = adminEmail,
                        onValueChange = onAdminEmailChange,
                        label = { Text("মাস্টার এডমিন অফিসিয়াল ইমেইল") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        textStyle = TextStyle(color = Color.Black, fontSize = 14.sp, fontWeight = FontWeight.Medium),
                        colors = appTextFieldColors()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = contactPhone,
                        onValueChange = onContactPhoneChange,
                        label = { Text("অফিসিয়াল কন্ট্রোল মোবাইল নম্বর") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        textStyle = TextStyle(color = Color.Black, fontSize = 14.sp, fontWeight = FontWeight.Medium),
                        colors = appTextFieldColors()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = onSaveContacts,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = LandGreenPrimary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("মালিকানা তথ্য সংরক্ষণ করুন")
                    }
                }
            }
        }
    }
}

@Composable
fun AdminStepByStepGuideView(context: Context) {
    val steps = remember {
        listOf(
            AdminStepItem(
                stepNumber = "১",
                title = "মাস্টার এডমিন পাসকি সেটআপ ও কাস্টমাইজেশন",
                summary = "অ্যাপের সার্বিক নিয়ন্ত্রণ নিজের নিয়ন্ত্রণে রাখতে সর্বপ্রথম ডিফল্ট পাসকি (998877) পরিবর্তন করে নিজস্ব গোপনীয় পাসকি সেট করুন।",
                keyPoints = listOf(
                    "সেটিংস মেনু বা হোম স্ক্রিনের সিকিউরিটি সেকশন থেকে 'মাস্টার এডমিন ভল্ট' এ প্রবেশ করুন।",
                    "ডিফল্ট কোড (998877) দিয়ে লগইন করে 'মাস্টার এডমিন পাসকি পরিবর্তন করুন' এ ক্লিক করুন।",
                    "নিজের মনে রাখার মতো ৪-৮ সংখ্যার একটি শক্তিশালী পাসকি সংরক্ষণ করুন যা শুধু আপনার জানা থাকবে।"
                )
            ),
            AdminStepItem(
                stepNumber = "২",
                title = "পাবলিকেশন সুরক্ষা (Publish-Protection) লক চালু রাখা",
                summary = "অ্যাপটি গুগল প্লে স্টোর বা অন্য যে কোনো মাধ্যমে পাবলিশ করার পর বহিরাগত কেউ যাতে ইন্টারনাল সেটিংস বিকৃত করতে না পারে তার ব্যবস্থা।",
                keyPoints = listOf(
                    "এডমিন কন্ট্রোল প্যানেলে 'পাবলিশ পরবর্তী সুরক্ষা লক' অপশনটি সবসময় চালু (Active) রাখবেন।",
                    "এর ফলে সাধারণ কোনো ব্যবহারকারী কোনোভাবেই অ্যাপের কোর সেটিংস বা মাস্টার কনফিগারেশনে হস্তক্ষেপ করতে পারবে না।",
                    "অ্যাপটির রিলিজ ভার্সনে (APK/AAB) সকল সিক্রেট ভ্যারিয়েবল এনক্রিপ্টেড থাকে যা সাধারণ ব্যবহারকারীদের জন্য পরিবর্তন অযোগ্য।"
                )
            ),
            AdminStepItem(
                stepNumber = "৩",
                title = "অ্যাপের সকল ফিচার ও অপশন উন্নত ও নিয়মিত আপডেট করার পদ্ধতি",
                summary = "পর্যায়ক্রমে প্রয়োজন অনুসারে নতুন খতিয়ান, নতুন ক্যালকুলেটর, কিংবা সরকারি নতুন গেজেটের নিয়মাবলি উন্নত করার উপায়।",
                keyPoints = listOf(
                    "অ্যাপের 'ঘোষণা বার্তা (Live Announcement)' অপশন থেকে তাৎক্ষণিক যে কোনো নোটিশ লিখে 'আপডেট' বাটনে চাপলে সকল ইউজার তা সাথে সাথে দেখতে পাবে।",
                    "সার্ভে রিপোর্ট ও ক্যালকুলেশন টুলবক্সের মাধ্যমে নতুন নতুন প্লট বা ওয়ারিশ ক্যালকুলেশন রুলস অন-ডিভাইসে কনফিগার করা সম্ভব।",
                    "নতুন বড় কোনো আইন বা ফিচার যোগ করতে চাইলে এআই স্টুডিওতে আপনার নতুন ইনস্ট্রাকশন দিলে এক ক্লিকে সম্পূর্ণ ফিচার আপডেট হয়ে নতুন বিল্ড তৈরি হবে।"
                )
            ),
            AdminStepItem(
                stepNumber = "৪",
                title = "গুগল প্লে কনসোল ও রিলিজ কি-স্টোর (Release Keystore) সুরক্ষা",
                summary = "প্লে স্টোরে অ্যাপ পাবলিশ করার পর আপডেট দেওয়ার একমাত্র অধিকার নিশ্চিত করার কৌশল।",
                keyPoints = listOf(
                    "গুগল প্লে কনসোলে অ্যাপ আপলোড করার সময় Play App Signing এবং আপনার প্রাইভেট কী (SHA-256 Fingerprint) ব্যবহৃত হয়।",
                    "প্লে স্টোরে একবার প্যাকেজ নেইম দিয়ে পাবলিশ হলে, শুধুমাত্র আপনার গুগল ডেভেলপার একাউন্ট ছাড়া পৃথিবীর অন্য কেউ আপডেট ভার্সন ছাড়তে পারবে না।",
                    "ফলে সাধারণ ব্যবহারকারী কিংবা কোনো বহিরাগত ডেভেলপার কোনোভাবেই অ্যাপটিতে পরিবর্তন বা প্রতিস্থাপন করতে পারবে না।"
                )
            ),
            AdminStepItem(
                stepNumber = "৫",
                title = "ব্যবহারকারী তথ্য ও সিকিউরিটি লগ পর্যবেক্ষণ",
                summary = "ব্যবহারকারীদের দেওয়া তথ্য ও আমিনদের সার্ভে রিপোর্টের সুরক্ষা বজায় রাখার নিয়ম।",
                keyPoints = listOf(
                    "ব্যবহারকারীরা তাদের ডিভাইসে যে সার্ভে বা হিসাব সংরক্ষণ করেন তা অফলাইন সুরক্ষিত ডাটাবেজে জমা থাকে।",
                    "এডমিন হেল্পলাইন ও অফিসিয়াল মোবাইল নম্বর সবসময় আপনার নির্ধারিত নম্বরে রিডাইরেক্ট থাকবে।"
                )
            )
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F766E)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "📘 সম্পূর্ণ এডমিন কন্ট্রোল ও সুরক্ষা নির্দেশিকা",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "অ্যাপ পাবলিশ হওয়ার পর কীভাবে সকল নিয়ন্ত্রণ একমাত্র এডমিনের হাতে থাকবে এবং কীভাবে অপশনগুলো উন্নত করবেন তার পূর্ণাঙ্গ রূপরেখা নিচে দেওয়া হলো:",
                        fontSize = 11.5.sp,
                        color = Color.White.copy(alpha = 0.9f),
                        lineHeight = 16.sp
                    )
                }
            }
        }

        itemsIndexed(steps) { _, step ->
            AdminStepCard(step = step, context = context)
        }

        item {
            Spacer(modifier = Modifier.height(10.dp))
            Surface(
                color = Color.White,
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, GoldAccent),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🔒", fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("এডমিন সার্টিফিকেশন নিশ্চয়তা", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = LandGreenDark)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "এই অ্যাপ্লিকেশনের সোর্স কোড ও এডমিন স্থাপত্য এমনভাবে নির্মিত যে পাবলিশের পর কোনো বহিরাগত বা অননুমোদিত ব্যক্তি অ্যাপের নিয়ন্ত্রণ নিতে পারবে না। সকল নিয়ন্ত্রণ শুধু এডমিন পাসকি এবং গুগল কনসোল অথেনটিকেশনের সাথেই সুরক্ষিত।",
                        fontSize = 11.sp,
                        color = Color(0xFF334155),
                        lineHeight = 15.sp
                    )
                }
            }
        }
    }
}

@Composable
fun AdminStepCard(step: AdminStepItem, context: Context) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    shape = CircleShape,
                    color = LandGreenPrimary,
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = step.stepNumber,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 14.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = step.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = LandGreenDark
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = step.summary,
                fontSize = 12.sp,
                color = Color(0xFF475569),
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF8FAFC), shape = RoundedCornerShape(8.dp))
                    .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                step.keyPoints.forEach { point ->
                    Row(verticalAlignment = Alignment.Top) {
                        Text("• ", color = LandGreenPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(
                            text = point,
                            fontSize = 11.5.sp,
                            color = Color(0xFF1E293B),
                            lineHeight = 15.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Admin Guide Step", "${step.title}\n\n${step.summary}\n\n" + step.keyPoints.joinToString("\n• ", "• "))
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "ধাপের বিবরণ কপি করা হয়েছে!", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp), tint = LandGreenPrimary)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("কপি করুন", fontSize = 11.sp, color = LandGreenPrimary)
                }
            }
        }
    }
}
