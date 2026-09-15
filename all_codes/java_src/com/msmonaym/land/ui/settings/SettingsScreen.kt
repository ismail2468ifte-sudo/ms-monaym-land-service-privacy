package com.msmonaym.land.ui.settings

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.StarRate
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.msmonaym.land.R
import com.msmonaym.land.data.SecurityManager
import com.msmonaym.land.data.UserProfileManager
import com.msmonaym.land.ui.theme.GoldAccent
import com.msmonaym.land.ui.theme.LandGreenDark
import com.msmonaym.land.ui.theme.LandGreenPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val securityManager = remember { SecurityManager(context) }
    val profileManager = remember { UserProfileManager(context) }

    var userName by remember { mutableStateOf(profileManager.getUserName()) }
    var userPhone by remember { mutableStateOf(profileManager.getUserPhone()) }

    var isLockEnabled by remember { mutableStateOf(securityManager.isLockEnabled()) }
    var isFingerprintEnabled by remember { mutableStateOf(securityManager.isFingerprintEnabled()) }
    var showChangePinDialog by remember { mutableStateOf(false) }
    var newPinInput by remember { mutableStateOf("") }
    var pinError by remember { mutableStateOf("") }

    val bluetoothManager = remember { context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager }
    val bluetoothAdapter = remember { bluetoothManager?.adapter }

    val openBluetooth = {
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
    var inputName by remember { mutableStateOf("") }
    var inputPhone by remember { mutableStateOf("") }
    var profileError by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("সেটিংস ও প্রোফাইল", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
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
                .background(Color(0xFFF4F7F5)),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // User Profile Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = LandGreenPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "ব্যবহারকারীর তথ্য ও প্রোফাইল",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = LandGreenDark
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        SettingItem("ব্যবহারকারীর নাম:", userName)
                        SettingItem("মোবাইল নম্বর:", userPhone)

                        Spacer(modifier = Modifier.height(10.dp))

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
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("প্রোফাইল সম্পাদন / পরিবর্তন করুন")
                        }
                    }
                }
            }

            // Security Lock Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = LandGreenPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "অ্যাপ সিকিউরিটি লক",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = LandGreenDark
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "সিকিউরিটি পিন লক সক্রিয় করুন",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "অ্যাপ চালু করার সময় পিন চাইবে",
                                    fontSize = 12.sp,
                                    color = Color.Gray
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

                        Spacer(modifier = Modifier.height(10.dp))

                        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))

                        Spacer(modifier = Modifier.height(10.dp))

                        // Fingerprint Option Switch
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
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "ফিঙ্গারপ্রিন্ট আনলক (Biometric Lock)",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                Text(
                                    text = "আপনার আঙুলের ছাপ দিয়ে লক খুলুন",
                                    fontSize = 12.sp,
                                    color = Color.Gray
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
                                Text("পিন পরিবর্তন", fontSize = 12.sp)
                            }

                            Button(
                                onClick = { testFingerprintScan() },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = LandGreenPrimary),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.Fingerprint, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("ফিঙ্গারপ্রিন্ট টেস্ট", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // Bluetooth Option Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Bluetooth, contentDescription = null, tint = LandGreenPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "ব্লুটুথ নিয়ন্ত্রণ (Bluetooth Setting)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = LandGreenDark
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "স্মার্ট ডিভাইস বা প্রিন্টারে কানেক্ট করতে মোবাইল ব্লুটুথ অন করুন",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = { openBluetooth() },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = LandGreenPrimary),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Bluetooth, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("ব্লুটুথ ওপেন / চালু করুন (Open Bluetooth)")
                        }
                    }
                }
            }

            // App Info Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = LandGreenPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "অ্যাপ্লিকেশন তথ্য",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = LandGreenDark
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        SettingItem("অ্যাপের নাম:", stringResource(id = R.string.app_name))
                        SettingItem("ভার্সন:", "v১.০.২ (প্রো)")
                        SettingItem("ডেভেলপার:", "MS Monaym (Expert App Builder)")
                    }
                }
            }

            // Share App Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Share, contentDescription = null, tint = LandGreenPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "অন্যদের সাথে শেয়ার করুন",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = LandGreenDark
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "আপনার বন্ধু-বান্ধব ও পরিচিতদের সাথে এই জমি পরিমাপ ও খতিয়ান সেবার অ্যাপটি শেয়ার করুন:",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(
                                        Intent.EXTRA_TEXT,
                                        "M.S MONAYM ENT. - বাংলাদেশের সেরা ভূমি সেবা অ্যাপ!\n\nক্যামেরা এআর দিয়ে জমি মাপা, স্যাটেলাইট পরিমাপ, খতিয়ান, ই-পর্চা, ই-নামজারি ও ভূমি কর সংক্রান্ত যাবতীয় সেবা এক অ্যাপে পান।"
                                    )
                                }
                                context.startActivity(Intent.createChooser(shareIntent, "অ্যাপ শেয়ার করুন"))
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = LandGreenPrimary),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("অ্যাপ শেয়ার করুন (Share App)")
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedButton(
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
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.StarRate, contentDescription = null, modifier = Modifier.size(16.dp), tint = GoldAccent)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("গুগল প্লে স্টোর (Google Play Store)")
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
                        modifier = Modifier.fillMaxWidth()
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
                        modifier = Modifier.fillMaxWidth()
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
                        modifier = Modifier.fillMaxWidth()
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
}

@Composable
fun SettingItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 13.sp, color = Color.Gray)
        Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color.DarkGray)
    }
}
