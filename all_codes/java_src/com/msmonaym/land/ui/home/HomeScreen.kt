package com.msmonaym.land.ui.home

import android.bluetooth.BluetoothAdapter
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.msmonaym.land.R
import com.msmonaym.land.data.UserProfileManager
import com.msmonaym.land.ui.navigation.Screen
import com.msmonaym.land.ui.theme.*

data class ServiceGridItem(
    val title: String,
    val emojiIcon: String,
    val screen: Screen,
    val tintColor: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigate: (Screen) -> Unit
) {
    val context = LocalContext.current
    val profileManager = remember { UserProfileManager(context) }

    var userName by remember { mutableStateOf(profileManager.getUserName()) }
    var userPhone by remember { mutableStateOf(profileManager.getUserPhone()) }
    var showProfileDialog by remember { mutableStateOf(false) }

    var inputName by remember { mutableStateOf("") }
    var inputPhone by remember { mutableStateOf("") }
    var profileError by remember { mutableStateOf("") }

    val serviceItems = remember {
        listOf(
            ServiceGridItem("জমি আইন", "⚖️", Screen.Law, EmeraldTint),
            ServiceGridItem("খতিয়ান (CS/RS)", "📜", Screen.Khatian, AmberTint),
            ServiceGridItem("ই-পর্চা", "📄", Screen.Porcha, SkyTint),
            ServiceGridItem("ই-নামজারি", "🏛️", Screen.Mutation, IndigoTint),
            ServiceGridItem("ভূমি উন্নয়ন কর", "💰", Screen.Tax, RoseTint),
            ServiceGridItem("সরকারি সেবা", "🌐", Screen.Services, TealTint),
            ServiceGridItem("রেজিস্ট্রেশন ফি", "📝", Screen.Registration, EmeraldTint),
            ServiceGridItem("জমি পরিমাপ", "📐", Screen.Calculator, SkyTint)
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.logo),
                                contentDescription = "Logo",
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(8.dp))
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = stringResource(id = R.string.app_name),
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp,
                                color = Color.White,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "ভূমি সেবার বিশ্বস্ত সহায়ক",
                                fontSize = 10.sp,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(
                                Intent.EXTRA_TEXT,
                                "M.S MONAYM ENT. - বাংলাদেশের সেরা ভূমি সেবা অ্যাপ!\n\nক্যামেরা এআর দিয়ে জমি মাপা, স্যাটেলাইট পরিমাপ, খতিয়ান, ই-পর্চা, ই-নামজারি ও ভূমি কর সংক্রান্ত যাবতীয় সেবা এক অ্যাপে পান।"
                            )
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "অ্যাপ শেয়ার করুন"))
                    }) {
                        Icon(Icons.Default.Share, contentDescription = "Share App", tint = Color.White)
                    }
                    IconButton(onClick = {
                        inputName = userName
                        inputPhone = userPhone
                        profileError = ""
                        showProfileDialog = true
                    }) {
                        Icon(Icons.Default.Person, contentDescription = "Profile", tint = Color.White)
                    }
                    IconButton(onClick = { onNavigate(Screen.Search) }) {
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.White)
                    }
                    IconButton(onClick = { onNavigate(Screen.Settings) }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = LandGreenPrimary
                )
            )
        },
        bottomBar = {
            Surface(
                color = Color.White,
                border = BorderStroke(1.dp, BorderColor),
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Home Tab (Active)
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { }
                            .padding(horizontal = 10.dp, vertical = 2.dp)
                    ) {
                        Surface(
                            color = LandGreenContainer,
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text(
                                text = "🏠",
                                fontSize = 16.sp,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("হোম", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = LandGreenPrimary)
                    }

                    // Search Tab
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { onNavigate(Screen.Search) }
                            .padding(horizontal = 10.dp, vertical = 2.dp)
                    ) {
                        Text("🔍", fontSize = 18.sp)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("সার্চ", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = Color.Gray)
                    }

                    // Contact Tab
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { onNavigate(Screen.Contact) }
                            .padding(horizontal = 10.dp, vertical = 2.dp)
                    ) {
                        Text("📞", fontSize = 18.sp)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("যোগাযোগ", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = Color.Gray)
                    }

                    // About Tab
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { onNavigate(Screen.About) }
                            .padding(horizontal = 10.dp, vertical = 2.dp)
                    ) {
                        Text("ℹ️", fontSize = 18.sp)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("তথ্য", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = Color.Gray)
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(SurfaceLight)
        ) {
            // Hero Welcome Banner with Dynamic User Name
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(LandGreenPrimary, LandGreenLight)
                        )
                    )
                    .padding(20.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "স্বাগতম, $userName",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Surface(
                            color = Color.White.copy(alpha = 0.2f),
                            shape = CircleShape,
                            modifier = Modifier.clickable {
                                inputName = userName
                                inputPhone = userPhone
                                profileError = ""
                                showProfileDialog = true
                            }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Edit,
                                    contentDescription = "Edit Profile",
                                    tint = GoldAccent,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "প্রোফাইল এডিশন",
                                    fontSize = 11.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "আপনার ভূমি সংক্রান্ত সব তথ্য এখানে পাবেন।",
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f))
                            .clickable {
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$userPhone"))
                                context.startActivity(intent)
                            }
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Phone,
                            contentDescription = null,
                            tint = GoldAccent,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "মোবাইল: $userPhone",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                }
            }

            // Google Play Store Quick Banner
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .clickable {
                        try {
                            val intent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("market://details?id=${context.packageName}")
                            ).apply {
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            }
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            val webIntent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://play.google.com/store/apps/details?id=${context.packageName}")
                            ).apply {
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            }
                            context.startActivity(webIntent)
                        }
                    },
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.dp, BorderColor),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = LandGreenContainer,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("▶️", fontSize = 20.sp)
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Google Play Store (গুগল প্লে স্টোর)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = TextPrimary
                            )
                            Text(
                                text = "প্লে স্টোরে অ্যাপ দেখুন ও রেটিং দিন",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                        }
                    }
                    Button(
                        onClick = {
                            try {
                                val intent = Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("market://details?id=${context.packageName}")
                                ).apply {
                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                }
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                val webIntent = Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("https://play.google.com/store/apps/details?id=${context.packageName}")
                                ).apply {
                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                }
                                context.startActivity(webIntent)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = LandGreenPrimary),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text("ওপেন করুন", fontSize = 11.sp)
                    }
                }
            }

            // Device Auto Link & Google Browser Integration Banner Card
            val deviceAppUrl = "https://ais-pre-2pbv6u37on6ftvr6eh3ut3-773309380409.asia-east1.run.app"
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.dp, BorderColor),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = EmeraldTint,
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("🌐", fontSize = 20.sp)
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "গুগল ব্রাউজারে অটোমেশন (Google Chrome Web)",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "মোবাইল ও গুগল ক্রোমে স্বয়ংক্রিয়ভাবে অ্যাপ ব্যবহার করুন",
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Google Browser App Link", deviceAppUrl)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "গুগল ব্রাউজার লিংক কপি করা হয়েছে!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("📋 কপি ব্রাউজার লিংক", fontSize = 11.sp)
                        }

                        Button(
                            onClick = {
                                try {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(deviceAppUrl)).apply {
                                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        setPackage("com.android.chrome")
                                    }
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    try {
                                        val genericIntent = Intent(Intent.ACTION_VIEW, Uri.parse(deviceAppUrl)).apply {
                                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        }
                                        context.startActivity(genericIntent)
                                    } catch (e2: Exception) {
                                        Toast.makeText(context, "ব্রাউজারে লিংক ওপেন করতে সমস্যা হচ্ছে", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = LandGreenPrimary),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("🌐 ব্রাউজারে খুলুন", fontSize = 11.sp)
                        }
                    }
                }
            }

            // Fingerprint & Bluetooth Quick Action Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.dp, BorderColor),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = "সিকিউরিটি ও ডিভাইস কানেকশন (Security & Bluetooth)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Fingerprint Quick Button
                        OutlinedButton(
                            onClick = {
                                val activity = context as? FragmentActivity
                                if (activity != null) {
                                    val executor = ContextCompat.getMainExecutor(context)
                                    val biometricPrompt = BiometricPrompt(
                                        activity,
                                        executor,
                                        object : BiometricPrompt.AuthenticationCallback() {
                                            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                                                super.onAuthenticationSucceeded(result)
                                                Toast.makeText(context, "ফিঙ্গারপ্রিন্ট সফলভাবে যাচাই হয়েছে!", Toast.LENGTH_SHORT).show()
                                            }

                                            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                                                super.onAuthenticationError(errorCode, errString)
                                                Toast.makeText(context, "ফিঙ্গারপ্রিন্ট পরীক্ষা: $errString", Toast.LENGTH_SHORT).show()
                                            }

                                            override fun onAuthenticationFailed() {
                                                super.onAuthenticationFailed()
                                                Toast.makeText(context, "ফিঙ্গারপ্রিন্ট মেলেনি!", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    )

                                    val promptInfo = BiometricPrompt.PromptInfo.Builder()
                                        .setTitle("ফিঙ্গারপ্রিন্ট যাচাই")
                                        .setSubtitle("আঙুলের ছাপ স্পর্শ করুন")
                                        .setNegativeButtonText("বাতিল")
                                        .build()

                                    biometricPrompt.authenticate(promptInfo)
                                } else {
                                    Toast.makeText(context, "ফিঙ্গারপ্রিন্ট সেন্সর উপলব্ধ নয়", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.Fingerprint, contentDescription = null, tint = LandGreenPrimary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("👆 ফিঙ্গারপ্রিন্ট", fontSize = 11.sp, color = TextPrimary)
                        }

                        // Bluetooth Open Button
                        Button(
                            onClick = {
                                try {
                                    val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE).apply {
                                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    }
                                    context.startActivity(enableBtIntent)
                                } catch (e: Exception) {
                                    try {
                                        val btSettingsIntent = Intent(Settings.ACTION_BLUETOOTH_SETTINGS).apply {
                                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        }
                                        context.startActivity(btSettingsIntent)
                                    } catch (e2: Exception) {
                                        Toast.makeText(context, "ব্লুটুথ ওপেন করতে সমস্যা হচ্ছে", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = LandGreenPrimary),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.Bluetooth, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("ᛔ ব্লুটুথ ওপেন", fontSize = 11.sp)
                        }
                    }
                }
            }

            // Grid Items
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(serviceItems) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(125.dp)
                            .clickable { onNavigate(item.screen) },
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(24.dp),
                        border = BorderStroke(1.dp, BorderColor),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(RoundedCornerShape(18.dp))
                                    .background(item.tintColor),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = item.emojiIcon, fontSize = 26.sp)
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = item.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = TextPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }

    // User Profile Setup / Edit Dialog
    if (showProfileDialog) {
        AlertDialog(
            onDismissRequest = { showProfileDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = LandGreenPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "ব্যবহারকারীর প্রোফাইল সেটিংস",
                        fontWeight = FontWeight.Bold,
                        color = LandGreenDark,
                        fontSize = 16.sp
                    )
                }
            },
            text = {
                Column {
                    Text(
                        text = "আপনার নাম এবং মোবাইল নম্বর দিন। এটি অ্যাপের ড্যাশবোর্ডে প্রদর্শিত হবে:",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = inputName,
                        onValueChange = {
                            inputName = it
                            profileError = ""
                        },
                        label = { Text("আপনার নাম (User Name)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = LandGreenPrimary,
                            focusedLabelColor = LandGreenPrimary
                        )
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
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = LandGreenPrimary,
                            focusedLabelColor = LandGreenPrimary
                        )
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
                            Toast.makeText(context, "প্রোফাইল সফলভাবে আপডেট করা হয়েছে!", Toast.LENGTH_SHORT).show()
                        } else {
                            profileError = "অনুগ্রহ করে আপনার নাম ও মোবাইল নম্বর লিখুন।"
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = LandGreenPrimary)
                ) {
                    Text("সংরক্ষণ করুন")
                }
            },
            dismissButton = {
                TextButton(onClick = { showProfileDialog = false }) {
                    Text("বাতিল", color = Color.Gray)
                }
            }
        )
    }
}
