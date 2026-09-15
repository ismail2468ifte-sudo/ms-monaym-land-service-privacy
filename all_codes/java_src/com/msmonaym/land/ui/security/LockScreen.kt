package com.msmonaym.land.ui.security

import android.content.Context
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.msmonaym.land.R
import com.msmonaym.land.data.SecurityManager
import com.msmonaym.land.ui.theme.*
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LockScreen(
    onUnlockSuccess: () -> Unit
) {
    val context = LocalContext.current
    val securityManager = remember { SecurityManager(context) }

    var inputPin by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var showForgotDialog by remember { mutableStateOf(false) }

    // Helper to prompt fingerprint authentication
    val triggerFingerprintAuth = {
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
                        onUnlockSuccess()
                    }

                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                        super.onAuthenticationError(errorCode, errString)
                        if (errorCode != BiometricPrompt.ERROR_USER_CANCELED && errorCode != BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
                            isError = true
                            errorMessage = "ফিঙ্গারপ্রিন্ট ত্রুটি: $errString"
                        }
                    }

                    override fun onAuthenticationFailed() {
                        super.onAuthenticationFailed()
                        isError = true
                        errorMessage = "ফিঙ্গারপ্রিন্ট মেলেনি! আবার চেষ্টা করুন।"
                    }
                }
            )

            val promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle("ফিঙ্গারপ্রিন্ট যাচাই (Fingerprint Lock)")
                .setSubtitle("আপনার আঙুলের ছাপ দিয়ে সিকিউরিটি আনলক করুন")
                .setNegativeButtonText("পিন ব্যবহার করুন")
                .build()

            biometricPrompt.authenticate(promptInfo)
        } else {
            Toast.makeText(context, "ফিঙ্গারপ্রিন্ট সেন্সর যাচাই করা যাচ্ছে না", Toast.LENGTH_SHORT).show()
        }
    }

    // Check if security lock is enabled, if disabled unlock immediately
    LaunchedEffect(Unit) {
        if (!securityManager.isLockEnabled()) {
            onUnlockSuccess()
        } else if (securityManager.isFingerprintEnabled() && securityManager.canAuthenticateWithBiometrics()) {
            delay(300)
            triggerFingerprintAuth()
        }
    }

    // Process PIN when length reaches 4
    LaunchedEffect(inputPin) {
        if (inputPin.length == 4) {
            if (securityManager.verifyPin(inputPin)) {
                isError = false
                Toast.makeText(context, "অনুমোদন সফল হয়েছে!", Toast.LENGTH_SHORT).show()
                onUnlockSuccess()
            } else {
                isError = true
                errorMessage = "ভুল সিকিউরিটি কোড! আবার চেষ্টা করুন।"
                delay(1200)
                inputPin = ""
                isError = false
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        LandGreenPrimary,
                        LandGreenDark
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header with App Logo
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f))
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "Logo",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(id = R.string.app_name),
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White.copy(alpha = 0.15f))
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = null,
                        tint = GoldAccent,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "সিকিউরিটি ভেরিফিকেশন",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // PIN Display Section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "অ্যাপে প্রবেশ করতে ৪ ডিজিটের কোড দিন",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                // 4 PIN Dots Indicator
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (i in 0 until 4) {
                        val isFilled = i < inputPin.length
                        val dotScale by animateFloatAsState(
                            targetValue = if (isFilled) 1.2f else 1.0f,
                            animationSpec = spring(stiffness = Spring.StiffnessHigh),
                            label = "dotScale"
                        )

                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .scale(dotScale)
                                .clip(CircleShape)
                                .background(
                                    if (isError) ErrorRed
                                    else if (isFilled) GoldAccent
                                    else Color.White.copy(alpha = 0.3f)
                                )
                                .border(
                                    width = 2.dp,
                                    color = if (isError) ErrorRed else Color.White,
                                    shape = CircleShape
                                )
                        )
                    }
                }

                // Error Message Animation
                AnimatedVisibility(
                    visible = isError,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Text(
                        text = errorMessage,
                        color = Color(0xFFFF8A80),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Default Pin Info Chip & Fingerprint Action
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(
                        color = Color.White.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.clickable { showForgotDialog = true }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.VpnKey,
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.8f),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "ডিফল্ট কোড: ১২৩৪ (পিন ভুলে গেলে ট্যাপ করুন)",
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 11.sp
                            )
                        }
                    }

                    // Fingerprint Touch Button
                    Surface(
                        color = GoldAccent.copy(alpha = 0.25f),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent),
                        modifier = Modifier.clickable { triggerFingerprintAuth() }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Fingerprint,
                                contentDescription = "Fingerprint Sensor",
                                tint = GoldAccent,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "👆 ফিঙ্গারপ্রিন্ট দিয়ে আনলক করুন",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }

            // Numeric Keypad
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val numRows = listOf(
                    listOf("1", "2", "3"),
                    listOf("4", "5", "6"),
                    listOf("7", "8", "9"),
                    listOf("clear", "0", "back")
                )

                for (row in numRows) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        for (item in row) {
                            KeypadButton(
                                text = item,
                                onClick = {
                                    when (item) {
                                        "clear" -> inputPin = ""
                                        "back" -> {
                                            if (inputPin.isNotEmpty()) {
                                                inputPin = inputPin.dropLast(1)
                                            }
                                        }
                                        else -> {
                                            if (inputPin.length < 4) {
                                                inputPin += item
                                            }
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // Forgot PIN Dialog
    if (showForgotDialog) {
        AlertDialog(
            onDismissRequest = { showForgotDialog = false },
            title = {
                Text(
                    text = "সিকিউরিটি কোড সহায়ক",
                    fontWeight = FontWeight.Bold,
                    color = LandGreenDark
                )
            },
            text = {
                Column {
                    Text(
                        text = "অ্যাপটির ডিফল্ট সিকিউরিটি পিন হলো: 1234",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "আপনি যদি পিন পরিবর্তন করে থাকেন তবে ডিফল্ট পিন (1234)-এ রিসেট করতে চাইলে নিচের বাটনে চাপুন।",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        securityManager.savePin(SecurityManager.DEFAULT_PIN)
                        inputPin = SecurityManager.DEFAULT_PIN
                        showForgotDialog = false
                        Toast.makeText(context, "পিন রিসেট করে 1234 করা হয়েছে", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = LandGreenPrimary)
                ) {
                    Text("1234 এ রিসেট করুন")
                }
            },
            dismissButton = {
                TextButton(onClick = { showForgotDialog = false }) {
                    Text("বন্ধ করুন", color = Color.Gray)
                }
            }
        )
    }
}

@Composable
fun KeypadButton(
    text: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(72.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.2f))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        when (text) {
            "clear" -> {
                Text(
                    text = "মুছুন",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            "back" -> {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Backspace,
                    contentDescription = "Backspace",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
            else -> {
                val banglaDigit = when (text) {
                    "0" -> "০"
                    "1" -> "১"
                    "2" -> "২"
                    "3" -> "৩"
                    "4" -> "৪"
                    "5" -> "৫"
                    "6" -> "৬"
                    "7" -> "৭"
                    "8" -> "৮"
                    "9" -> "৯"
                    else -> text
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = banglaDigit,
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
