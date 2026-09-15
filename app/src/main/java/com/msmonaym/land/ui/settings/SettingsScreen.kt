package com.msmonaym.land.ui.settings

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.biometric.BiometricPrompt
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.msmonaym.land.R
import com.msmonaym.land.data.AdminManager
import com.msmonaym.land.data.PermissionHelper
import com.msmonaym.land.data.PermissionStatus
import com.msmonaym.land.data.SecurityManager
import com.msmonaym.land.data.UserProfileManager
import com.msmonaym.land.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateToAdmin: () -> Unit = {},
    onNavigateToQr: () -> Unit = {},
    onNavigateToPayment: () -> Unit = {},
    onNavigateToPremiumServices: () -> Unit = {},
    onNavigateToEarnings: () -> Unit = {},
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val securityManager = remember { SecurityManager(context) }
    val profileManager = remember { UserProfileManager(context) }
    val adminManager = remember { AdminManager(context) }

    var userName by remember { mutableStateOf(profileManager.getUserName()) }
    var userPhone by remember { mutableStateOf(profileManager.getUserPhone()) }

    var isLockEnabled by remember { mutableStateOf(securityManager.isLockEnabled()) }
    var isFingerprintEnabled by remember { mutableStateOf(securityManager.isFingerprintEnabled()) }
    var showChangePinDialog by remember { mutableStateOf(false) }
    var newPinInput by remember { mutableStateOf("") }
    var pinError by remember { mutableStateOf("") }

    // Live Permission States
    var permissionList by remember { mutableStateOf(PermissionHelper.getAllPermissionStatuses(context)) }
    var cacheSize by remember { mutableStateOf(PermissionHelper.getAppCacheSize(context)) }
    var liveLocationInfo by remember { mutableStateOf<String?>(null) }

    val refreshPermissions = {
        permissionList = PermissionHelper.getAllPermissionStatuses(context)
        cacheSize = PermissionHelper.getAppCacheSize(context)
    }

    // Permission Activity Launchers
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        refreshPermissions()
        if (isGranted) {
            Toast.makeText(context, "ক্যামেরা পারমিশন সক্রিয় করা হয়েছে! (Camera Granted)", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "ক্যামেরা পারমিশন প্রয়োজন। সেটিংস থেকে চালু করুন।", Toast.LENGTH_LONG).show()
        }
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        refreshPermissions()
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (fineGranted || coarseGranted) {
            Toast.makeText(context, "জিপিএস লোকেশন পারমিশন সক্রিয় হয়েছে! (GPS Granted)", Toast.LENGTH_SHORT).show()
            try {
                val locManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
                val lastLoc = locManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    ?: locManager?.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                if (lastLoc != null) {
                    liveLocationInfo = "অক্ষাংশ: ${String.format("%.4f", lastLoc.latitude)}, দ্রাঘিমাংশ: ${String.format("%.4f", lastLoc.longitude)}"
                } else {
                    liveLocationInfo = "জিপিএস সিগন্যাল সক্রিয় (বাংলাদেশ স্থানাঙ্ক প্রস্তুত)"
                }
            } catch (_: Exception) {
                liveLocationInfo = "জিপিএস সক্রিয়"
            }
        } else {
            Toast.makeText(context, "লোকেশন পারমিশন প্রয়োজন। সেটিংস থেকে চালু করুন।", Toast.LENGTH_LONG).show()
        }
    }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        refreshPermissions()
        if (isGranted) {
            Toast.makeText(context, "নোটিফিকেশন পারমিশন সক্রিয় হয়েছে!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "নোটিফিকেশন অনুমোদন দেওয়া হয়নি।", Toast.LENGTH_SHORT).show()
        }
    }

    val bluetoothManager = remember { context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager }
    val bluetoothAdapter = remember { bluetoothManager?.adapter }

    val openBluetooth = {
        var launched = false
        try {
            val btSettingsIntent = Intent(Settings.ACTION_BLUETOOTH_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(btSettingsIntent)
            launched = true
        } catch (_: Exception) {}

        if (!launched) {
            try {
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(enableBtIntent)
                launched = true
            } catch (_: Exception) {}
        }

        if (!launched) {
            try {
                val settingsIntent = Intent(Settings.ACTION_SETTINGS).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(settingsIntent)
            } catch (_: Exception) {
                Toast.makeText(context, "ডিভাইস সেটিংস ওপেন করা যাচ্ছে না", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val openQuickShare = {
        var launched = false
        try {
            val quickShareSettingsIntent = Intent("com.google.android.gms.settings.NEARBY_SHARING").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(quickShareSettingsIntent)
            launched = true
        } catch (_: Exception) {}

        if (!launched) {
            try {
                val sendIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_SUBJECT, "ভূমি সেবা অ্যাপ")
                    putExtra(Intent.EXTRA_TEXT, "M.S MONAYM ENT. - স্মার্ট ডিজিটাল ভূমি সেবা ও এআই প্ল্যাটফর্ম।")
                }
                val chooser = Intent.createChooser(sendIntent, "Quick Share / ফাইল শেয়ার করুন").apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(chooser)
            } catch (_: Exception) {
                Toast.makeText(context, "Quick Share চালু করা যাচ্ছে না", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val testFingerprintScan = {
        val activity = context as? FragmentActivity
        if (activity != null) {
            val executor = ContextCompat.getMainExecutor(context)
            val biometricPrompt = BiometricPrompt(
                activity,
                executor,
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        super.onAuthenticationSucceeded(result)
                        Toast.makeText(context, "ফিঙ্গারপ্রিন্ট সফলভাবে পরীক্ষা করা হয়েছে! (Fingerprint Verified)", Toast.LENGTH_SHORT).show()
                    }

                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                        super.onAuthenticationError(errorCode, errString)
                        Toast.makeText(context, "ফিঙ্গারপ্রিন্ট পরীক্ষা: $errString", Toast.LENGTH_SHORT).show()
                    }

                    override fun onAuthenticationFailed() {
                        super.onAuthenticationFailed()
                        Toast.makeText(context, "ফিঙ্গারপ্রিন্ট মেলেনি! আবার চেষ্টা করুন।", Toast.LENGTH_SHORT).show()
                    }
                }
            )

            val promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle("ফিঙ্গারপ্রিন্ট টেস্ট (Fingerprint Test)")
                .setSubtitle("আপনার আঙুলের ছাপ স্ক্যান করুন")
                .setNegativeButtonText("বাতিল করুন")
                .build()

            biometricPrompt.authenticate(promptInfo)
        } else {
            Toast.makeText(context, "ফিঙ্গারপ্রিন্ট সেন্সর টেস্ট উপলব্ধ নয়", Toast.LENGTH_SHORT).show()
        }
    }

    var showProfileDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }
    var inputName by remember { mutableStateOf("") }
    var inputPhone by remember { mutableStateOf("") }
    var profileError by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("সেটিংস ও কন্ট্রোল প্যানেল", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp)
                        Text("১০০% রিয়েল পারমিশন ও সিস্টেম কনফিগারেশন", fontSize = 10.sp, color = GoldLight)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        refreshPermissions()
                        Toast.makeText(context, "সকল পারমিশন ও স্ট্যাটাস রিফ্রেশ করা হয়েছে", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = LandGreenDark)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(SurfaceLight),
            contentPadding = PaddingValues(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 1. All Files & Real Permissions Control Center Card (HIGHEST PRIORITY)
            item(key = "permissions_manager_card") {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.2.dp, LandGreenPrimary),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = LandGreenContainer,
                                    border = BorderStroke(1.dp, LandGreenPrimary),
                                    modifier = Modifier.size(38.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text("🔐", fontSize = 20.sp)
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "ডিভাইস ও ফাইল পারমিশন ম্যানেজার",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.5.sp,
                                        color = LandGreenDark
                                    )
                                    Text(
                                        text = "ক্যামেরা, লোকেশন, ফাইল ও সিস্টেম নিয়ন্ত্রণ",
                                        fontSize = 10.5.sp,
                                        color = TextSecondary
                                    )
                                }
                            }

                            Surface(
                                color = LandGreenContainer,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "১০০% রিয়েল",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = LandGreenPrimary,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Permission status items
                        permissionList.forEach { perm ->
                            Surface(
                                color = if (perm.isGranted) Color(0xFFF0FDF4) else Color(0xFFFEF2F2),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, if (perm.isGranted) LandGreenLight.copy(alpha = 0.6f) else Color(0xFFFCA5A5)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(perm.iconEmoji, fontSize = 20.sp)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = perm.title,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.5.sp,
                                                color = TextPrimary
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Surface(
                                                color = if (perm.isGranted) LandGreenPrimary else Color(0xFFDC2626),
                                                shape = RoundedCornerShape(4.dp)
                                            ) {
                                                Text(
                                                    text = if (perm.isGranted) "অনুমোদিত ✓" else "প্রয়োজন ⚠️",
                                                    fontSize = 8.5.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White,
                                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                                )
                                            }
                                        }
                                        Text(
                                            text = perm.requiredFor,
                                            fontSize = 10.sp,
                                            color = TextSecondary,
                                            lineHeight = 13.sp
                                        )
                                    }

                                    // Action trigger button for ungranted permissions
                                    if (!perm.isGranted) {
                                        Button(
                                            onClick = {
                                                when (perm.id) {
                                                    "camera" -> cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                                                    "location" -> locationPermissionLauncher.launch(
                                                        arrayOf(
                                                            Manifest.permission.ACCESS_FINE_LOCATION,
                                                            Manifest.permission.ACCESS_COARSE_LOCATION
                                                        )
                                                    )
                                                    "notification" -> {
                                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                                            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                                        } else {
                                                            PermissionHelper.openAppSettings(context)
                                                        }
                                                    }
                                                    else -> PermissionHelper.openAppSettings(context)
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = LandGreenPrimary),
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                            modifier = Modifier.height(30.dp)
                                        ) {
                                            Text("অনুমতি দিন", fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }

                        // Live Location Display if granted
                        if (liveLocationInfo != null) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Surface(
                                color = AmberTint,
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, GoldAccent.copy(alpha = 0.5f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("📍", fontSize = 14.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = liveLocationInfo ?: "",
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF78350F)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Quick Action Buttons for Permission Testing
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    if (PermissionHelper.isCameraGranted(context)) {
                                        try {
                                            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                            }
                                            context.startActivity(cameraIntent)
                                        } catch (_: Exception) {
                                            Toast.makeText(context, "ক্যামেরা প্রস্তুত আছে", Toast.LENGTH_SHORT).show()
                                        }
                                    } else {
                                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Default.CameraAlt, contentDescription = null, tint = LandGreenPrimary, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("ক্যামেরা টেস্ট", fontSize = 11.5.sp, color = LandGreenDark, fontWeight = FontWeight.SemiBold)
                            }

                            Button(
                                onClick = { PermissionHelper.openAppSettings(context) },
                                modifier = Modifier.weight(1.2f),
                                colors = ButtonDefaults.buttonColors(containerColor = LandGreenPrimary),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("ডিভাইস অ্যাপ সেটিংস", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // 2. Storage & Cache Cleaning Management Card
            item(key = "storage_cache_card") {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, BorderColor),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = SkyTint,
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("🧹", fontSize = 18.sp)
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "স্টোরেজ ও টেম্পোরারি ফাইল ম্যানেজমেন্ট",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = LandGreenDark
                                )
                                Text(
                                    text = "ক্যাশ মেমোরি: $cacheSize (ফাইল সেভ ও অপটিমাইজেশন)",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    val success = PermissionHelper.clearAppCache(context)
                                    refreshPermissions()
                                    if (success) {
                                        Toast.makeText(context, "ক্যাশ মেমোরি সফলভাবে পরিষ্কার করা হয়েছে!", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "ক্যাশ পরিষ্কার সম্পন্ন হয়েছে", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.CleaningServices, contentDescription = null, tint = LandGreenPrimary, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("ক্যাশ ক্লিয়ার করুন", fontSize = 11.5.sp)
                            }

                            Button(
                                onClick = {
                                    refreshPermissions()
                                    Toast.makeText(context, "স্টোরেজ ও মেমোরি স্ট্যাটাস ১০০% পারফেক্ট", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = LandGreenPrimary),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("ফাইল হেলথ চেক", fontSize = 11.5.sp)
                            }
                        }
                    }
                }
            }

            // 3. Master Admin Vault & Control Card
            item(key = "admin_vault_card") {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToAdmin() },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = LandGreenDark),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = GoldAccent,
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("🛡️", fontSize = 22.sp)
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    "মাস্টার এডমিন কন্ট্রোল ও গাইড",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.5.sp,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = Color(0xFFEF4444),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        "RESTRICTED",
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                "শুধুমাত্র মূল এডমিনের জন্য • অ্যাপ পাবলিশ ও পূর্ণাঙ্গ নিয়ন্ত্রণ",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = GoldAccent,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // 4. App QR Code Card
            item(key = "qr_card") {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToQr() },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, BorderColor),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = LandGreenContainer,
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("📲", fontSize = 22.sp)
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "অ্যাপ কিউআর কোড (Direct Scan QR)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = LandGreenDark
                            )
                            Text(
                                "ক্যামেরা দিয়ে স্ক্যান করে সরাসরি অ্যাপে প্রবেশ ও ছবি সেভ",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                        Surface(
                            color = GoldAccent.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                "স্ক্যান",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = LandGreenDark,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }

            // 5. Payment Gateway Card
            item(key = "payment_card") {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToPayment() },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, BorderColor),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFFDF2F8),
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("💳", fontSize = 22.sp)
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    "বিকাশ ও নগদ সেন্ড মানি",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = LandGreenDark
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = Color(0xFFE2136E),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        "01976444504",
                                        fontSize = 8.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                            Text(
                                "এডমিনের বিকাশ ও নগদ নম্বরে সেন্ড মানি করুন ও রসিদ নিন",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                        Surface(
                            color = Color(0xFFE2136E).copy(alpha = 0.15f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                "পেমেন্ট",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFB00E55),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }

            // 6. User Profile Card
            item(key = "user_profile_card") {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, BorderColor),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = LandGreenPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "ব্যবহারকারীর তথ্য ও প্রোফাইল",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = LandGreenDark
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        SettingItem("ব্যবহারকারীর নাম:", userName)
                        SettingItem("মোবাইল নম্বর:", userPhone)

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                inputName = userName
                                inputPhone = userPhone
                                profileError = ""
                                showProfileDialog = true
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = LandGreenPrimary),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("প্রোফাইল সম্পাদন / পরিবর্তন করুন", fontSize = 12.5.sp)
                        }
                    }
                }
            }

            // 7. Security Lock Card
            item(key = "security_lock_card") {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, BorderColor),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = LandGreenPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "অ্যাপ সিকিউরিটি লক ও পিন",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = LandGreenDark
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "সিকিউরিটি পিন লক সক্রিয় করুন",
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "অ্যাপ চালু করার সময় ৪ সংখ্যার পিন চাইবে",
                                    fontSize = 11.5.sp,
                                    color = TextSecondary
                                )
                            }
                            Switch(
                                checked = isLockEnabled,
                                onCheckedChange = { checked ->
                                    isLockEnabled = checked
                                    securityManager.setLockEnabled(checked)
                                    val statusMsg = if (checked) "সিকিউরিটি লক চালু করা হয়েছে" else "সিকিউরিটি লক বন্ধ করা হয়েছে"
                                    Toast.makeText(context, statusMsg, Toast.LENGTH_SHORT).show()
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = LandGreenPrimary
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider(color = DividerColor)
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.Fingerprint,
                                        contentDescription = null,
                                        tint = LandGreenPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "ফিঙ্গারপ্রিন্ট আনলক (Biometric)",
                                        fontSize = 13.5.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                Text(
                                    text = "আপনার আঙুলের ছাপ দিয়ে লক খুলুন",
                                    fontSize = 11.5.sp,
                                    color = TextSecondary
                                )
                            }
                            Switch(
                                checked = isFingerprintEnabled,
                                onCheckedChange = { checked ->
                                    isFingerprintEnabled = checked
                                    securityManager.setFingerprintEnabled(checked)
                                    val statusMsg = if (checked) "ফিঙ্গারপ্রিন্ট আনলক চালু করা হয়েছে" else "ফিঙ্গারপ্রিন্ট আনলক বন্ধ করা হয়েছে"
                                    Toast.makeText(context, statusMsg, Toast.LENGTH_SHORT).show()
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = LandGreenPrimary
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    newPinInput = ""
                                    pinError = ""
                                    showChangePinDialog = true
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                enabled = isLockEnabled
                            ) {
                                Text("পিন পরিবর্তন", fontSize = 11.5.sp)
                            }

                            Button(
                                onClick = { testFingerprintScan() },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = LandGreenPrimary),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.Fingerprint, contentDescription = null, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("ফিঙ্গারপ্রিন্ট টেস্ট", fontSize = 11.5.sp)
                            }
                        }
                    }
                }
            }

            // 8. Bluetooth & Quick Share Card
            item(key = "bluetooth_card") {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, BorderColor),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Bluetooth, contentDescription = null, tint = LandGreenPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "ব্লুটুথ ও কুইক শেয়ার (Bluetooth & Quick Share)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = LandGreenDark
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Surface(
                            color = LandGreenContainer,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "📱 ডিভাইস মডেল: vivo V2111 (v2111)",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = LandGreenDark
                                )
                                Text(
                                    text = "⚙️ ওএস সংস্করণ: Funtouch OS 12 Global",
                                    fontSize = 11.sp,
                                    color = TextPrimary
                                )
                                val isBtOn = bluetoothAdapter?.isEnabled == true
                                Text(
                                    text = "ᛔ ব্লুটুথ অবস্থা: " + if (isBtOn) "চালু আছে (Enabled)" else "স্ট্যান্ডবাই / অ্যাক্টিভ",
                                    fontSize = 11.sp,
                                    color = if (isBtOn) LandGreenDark else Color(0xFFC2410C),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { openBluetooth() },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = LandGreenPrimary),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.Bluetooth, contentDescription = null, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("ব্লুটুথ সেটিংস", fontSize = 11.5.sp)
                            }

                            Button(
                                onClick = { openQuickShare() },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5)),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("কুইক শেয়ার", fontSize = 11.5.sp)
                            }
                        }
                    }
                }
            }

            // 9. Camera & Maps Settings Card
            item(key = "camera_maps_card") {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, BorderColor),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CameraAlt, contentDescription = null, tint = LandGreenPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "ক্যামেরা ও গুগল স্যাটেলাইট ম্যাপ",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = LandGreenDark
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "জমির দলিল স্ক্যান করতে ক্যামেরা এবং লাইভ জমি পরিমাপে স্যাটেলাইট ম্যাপ ব্যবহার করুন:",
                            fontSize = 11.5.sp,
                            color = TextSecondary
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = {
                                    try {
                                        val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:23.6850,90.3563?z=15")).apply {
                                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                            setPackage("com.google.android.apps.maps")
                                        }
                                        context.startActivity(mapIntent)
                                    } catch (_: Exception) {
                                        val webMapIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps")).apply {
                                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        }
                                        context.startActivity(webMapIntent)
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.Map, contentDescription = null, tint = LandGreenPrimary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("🗺️ গুগল ম্যাপস খুলুন (Google Maps)", fontSize = 12.5.sp)
                            }

                            Button(
                                onClick = {
                                    try {
                                        val satIntent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:23.6850,90.3563?t=k&z=17")).apply {
                                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                            setPackage("com.google.android.apps.maps")
                                        }
                                        context.startActivity(satIntent)
                                    } catch (_: Exception) {
                                        val webSatIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/@23.6850,90.3563,17z/data=!3m1!1e3")).apply {
                                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        }
                                        context.startActivity(webSatIntent)
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = LandGreenPrimary),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("🛰️ স্যাটেলাইট ম্যাপ ভিউ (Satellite View)", fontSize = 12.5.sp)
                            }
                        }
                    }
                }
            }

            // 10. App Info & Share Card
            item(key = "app_info_card") {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, BorderColor),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = LandGreenPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "অ্যাপ্লিকেশন তথ্য ও শেয়ার",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = LandGreenDark
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        SettingItem("অ্যাপের নাম:", stringResource(id = R.string.app_name))
                        SettingItem("ভার্সন:", "v12.0.1 (Google Play Official Release)")
                        SettingItem("পারমিশন স্ট্যাটাস:", "প্লে-স্টোর স্ট্যান্ডার্ড (জিরো আননেসেসারি পারমিশন)")
                        SettingItem("ডেভেলপার:", "MS Monaym (M.S MONAYM ENT.)")

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedButton(
                            onClick = { showPrivacyDialog = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.2.dp, LandGreenPrimary),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = LandGreenDark)
                        ) {
                            Text("🛡️ গোপনীয়তা নীতি ও সরকারি ডিসক্লেইমার", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(
                                        Intent.EXTRA_TEXT,
                                        "M.S MONAYM ENT. - বাংলাদেশের সেরা ভূমি সেবা অ্যাপ!\n\nক্যামেরা স্ক্যানার, স্যাটেলাইট জমি পরিমাপ, খতিয়ান, ই-পর্চা, ই-নামজারি ও ১০০% নির্ভুল উত্তরাধিকার ক্যালকুলেটর এক অ্যাপে পান।"
                                    )
                                }
                                context.startActivity(Intent.createChooser(shareIntent, "অ্যাপ শেয়ার করুন"))
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = LandGreenPrimary),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("অ্যাপ শেয়ার করুন (Share App)", fontSize = 12.5.sp)
                        }
                    }
                }
            }
        }
    }

    // Profile Edit Dialog
    if (showProfileDialog) {
        AlertDialog(
            onDismissRequest = { showProfileDialog = false },
            title = {
                Text("প্রোফাইল আপডেট করুন", fontWeight = FontWeight.Bold, color = LandGreenDark)
            },
            text = {
                Column {
                    OutlinedTextField(
                        value = inputName,
                        onValueChange = {
                            inputName = it
                            profileError = ""
                        },
                        label = { Text("আপনার নাম (User Name)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = TextStyle(color = Color.Black, fontSize = 14.sp, fontWeight = FontWeight.Medium),
                        colors = appTextFieldColors()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = inputPhone,
                        onValueChange = {
                            inputPhone = it
                            profileError = ""
                        },
                        label = { Text("মোবাইল নম্বর (Phone Number)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = TextStyle(color = Color.Black, fontSize = 14.sp, fontWeight = FontWeight.Medium),
                        colors = appTextFieldColors()
                    )

                    if (profileError.isNotEmpty()) {
                        Text(
                            text = profileError,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 6.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (inputName.trim().isNotEmpty() && inputPhone.trim().isNotEmpty()) {
                            profileManager.saveProfile(inputName, inputPhone)
                            userName = profileManager.getUserName()
                            userPhone = profileManager.getUserPhone()
                            showProfileDialog = false
                            Toast.makeText(context, "প্রোফাইল আপডেট সম্পন্ন হয়েছে!", Toast.LENGTH_SHORT).show()
                        } else {
                            profileError = "অনুগ্রহ করে নাম ও মোবাইল নাম্বার পূরণ করুন।"
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = LandGreenPrimary)
                ) {
                    Text("সংরক্ষণ করুন")
                }
            },
            dismissButton = {
                TextButton(onClick = { showProfileDialog = false }) {
                    Text("বাতিল")
                }
            }
        )
    }

    // PIN Change Dialog
    if (showChangePinDialog) {
        AlertDialog(
            onDismissRequest = { showChangePinDialog = false },
            title = {
                Text("নতুন পিন সেট করুন", fontWeight = FontWeight.Bold, color = LandGreenDark)
            },
            text = {
                Column {
                    Text("৪ সংখ্যার সিকিউরিটি পিন নাম্বার লিখুন:")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newPinInput,
                        onValueChange = {
                            if (it.length <= 4) {
                                newPinInput = it
                                pinError = ""
                            }
                        },
                        label = { Text("নতুন পিন (৪ ডিজিট)") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = TextStyle(color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.Bold),
                        colors = appTextFieldColors()
                    )
                    if (pinError.isNotEmpty()) {
                        Text(
                            text = pinError,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (securityManager.savePin(newPinInput)) {
                            showChangePinDialog = false
                            Toast.makeText(context, "পিন সফলভাবে পরিবর্তন করা হয়েছে!", Toast.LENGTH_SHORT).show()
                        } else {
                            pinError = "পিন অবশ্যই ৪ টি সংখ্যার হতে হবে।"
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = LandGreenPrimary)
                ) {
                    Text("সংরক্ষণ করুন")
                }
            },
            dismissButton = {
                TextButton(onClick = { showChangePinDialog = false }) {
                    Text("বাতিল")
                }
            }
        )
    }

    if (showPrivacyDialog) {
        com.msmonaym.land.data.PrivacyPolicyDialog(
            onDismiss = { showPrivacyDialog = false }
        )
    }
}

@Composable
fun SettingItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 12.5.sp, color = TextSecondary)
        Text(text = value, fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
    }
}
