package com.msmonaym.land.ui.about

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.msmonaym.land.R
import com.msmonaym.land.ui.theme.GoldAccent
import com.msmonaym.land.ui.theme.LandGreenContainer
import com.msmonaym.land.ui.theme.LandGreenDark
import com.msmonaym.land.ui.theme.LandGreenPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var showPrivacyDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("আমাদের সম্পর্কে", fontWeight = FontWeight.Bold, color = Color.White) },
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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(110.dp)
                                .clip(CircleShape)
                                .background(LandGreenContainer)
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

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = stringResource(id = R.string.app_name),
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = LandGreenDark
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(Icons.Default.Verified, contentDescription = "Verified", tint = LandGreenPrimary, modifier = Modifier.size(20.dp))
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = stringResource(id = R.string.welcome),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = LandGreenPrimary,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        HorizontalDivider(color = Color(0xFFE0E0E0))

                        Spacer(modifier = Modifier.height(16.dp))

                        Surface(
                            color = LandGreenContainer,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                InfoRow("প্রতিষ্ঠাতা ও মালিক:", stringResource(id = R.string.owner))
                                InfoRow("যোগাযোগ নম্বর:", stringResource(id = R.string.phone))
                                InfoRow("মোবাইল:", "01976444504")
                                InfoRow("সেবার পরিধি:", "সমগ্র বাংলাদেশ")
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "M.S MONAYM ENT. বাংলাদেশের ভূমি ও সম্পত্তি হস্তান্তরে একটি নির্ভরযোগ্য প্রতিষ্ঠান। আমাদের লক্ষ্য হলো জমি কেনাবেচা, বায়নাপত্র, ই-পর্চা, ই-নামজারি, ভূমি উন্নয়ন কর প্রদান এবং দলিল রেজিস্ট্রি সংক্রান্ত যাবতীয় অনলাইন ও প্রত্যক্ষ সেবা সহজ, দ্রুত ও স্বচ্ছ প্রক্রিয়ায় প্রতিটি নাগরিকের নিকট পৌঁছে দেওয়া।",
                            fontSize = 13.sp,
                            color = Color.DarkGray,
                            lineHeight = 19.sp,
                            textAlign = TextAlign.Start
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Google Play Store Mandatory Government Disclaimer Card
                        Surface(
                            color = Color(0xFFFEF3C7),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF59E0B)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.VerifiedUser,
                                        contentDescription = null,
                                        tint = Color(0xFF92400E),
                                        modifier = Modifier.size(17.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "সরকারি তথ্যের উৎস ও লিগ্যাল ডিসক্লেইমার",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = Color(0xFF92400E)
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "⚠️ M.S MONAYM ENT. কোনো সরকারি সংস্থা বা প্রতিষ্ঠান নয়। এটি একটি ব্যক্তিগত আইনি ও প্রযুক্তিগত সহকারী অ্যাপ। সরকারি তথ্যের মূল উৎস: ভূমি মন্ত্রণালয় বাংলাদেশ (minland.gov.bd), ই-পর্চা (eporcha.gov.bd), ভূমি উন্নয়ন কর (ldtax.gov.bd)।",
                                    fontSize = 11.sp,
                                    lineHeight = 16.sp,
                                    color = Color(0xFF78350F)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Privacy Policy Button for Play Store
                        OutlinedButton(
                            onClick = { showPrivacyDialog = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.2.dp, LandGreenPrimary),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = LandGreenDark)
                        ) {
                            Text("🛡️ সম্পূর্ণ গোপনীয়তা নীতি ও শর্তাবলী দেখুন", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:01976444504"))
                                    context.startActivity(intent)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = LandGreenPrimary),
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("কল করুন", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }

                            Button(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/8801976444504"))
                                    context.startActivity(intent)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("হোয়াটসঅ্যাপ", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showPrivacyDialog) {
        com.msmonaym.land.data.PrivacyPolicyDialog(
            onDismiss = { showPrivacyDialog = false }
        )
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 13.sp, color = Color.DarkGray)
        Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = LandGreenDark)
    }
}
