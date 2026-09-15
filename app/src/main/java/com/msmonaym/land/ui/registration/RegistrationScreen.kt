package com.msmonaym.land.ui.registration

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.msmonaym.land.data.LandDataRepository
import com.msmonaym.land.data.ai.AiCategory
import com.msmonaym.land.ui.ai.AiFloatingActionButton
import com.msmonaym.land.ui.ai.AiLandAssistantModal
import com.msmonaym.land.ui.ai.AiTopBarAction
import com.msmonaym.land.ui.theme.*
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationScreen(
    onBack: () -> Unit,
    onNavigateToDeedWriter: () -> Unit = {}
) {
    var landPriceInput by remember { mutableStateOf("1000000") } // Default 10 Lac BDT
    val parsedPrice = landPriceInput.toDoubleOrNull() ?: 0.0
    val result = remember(parsedPrice) {
        LandDataRepository.calculateRegistrationFee(parsedPrice)
    }

    var showAiAssistant by remember { mutableStateOf(false) }
    val bdtFormatter = remember { NumberFormat.getNumberInstance(Locale("bn", "BD")) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("রেজিস্ট্রেশন ফি ও নিয়মাবলী", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    AiTopBarAction { showAiAssistant = true }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = LandGreenPrimary)
            )
        },
        floatingActionButton = {
            AiFloatingActionButton(AiCategory.REGISTRATION) {
                showAiAssistant = true
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF4F7F5)),
            contentPadding = PaddingValues(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Digital Deed Writer & Word Editor Banner
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToDeedWriter() },
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E3A8A)), // Deep Blue
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.2f),
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("🖋️", fontSize = 22.sp)
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "ডিজিটাল দলিল রাইটার ও ওয়ার্ড এডিটর",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.5.sp,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = Color(0xFFF59E0B),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = "নতুন",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = "সাফ-কবলা, হেবা, দানপত্র, বায়না ও বন্টন দলিলের আদর্শ বয়ান লিখুন ও Word (.doc) ফাইল ডাউনলোড করুন।",
                                fontSize = 11.sp,
                                color = Color(0xFFE0E7FF),
                                lineHeight = 15.sp
                            )
                        }
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                }
            }

            // AI Fee Consultant Banner
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showAiAssistant = true },
                    colors = CardDefaults.cardColors(containerColor = LandGreenDark),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        androidx.compose.foundation.Image(
                            painter = androidx.compose.ui.res.painterResource(id = com.msmonaym.land.R.drawable.ic_ai_digital_bot),
                            contentDescription = "AI",
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "দলিল ফি ও ট্যাক্স ডিজিটাল এআই সহকারী",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color.White
                            )
                            Text(
                                text = "এসএমএস লিখে হেবা, দানপত্র বা সাফ-কবলা দলিলের সরকারি স্ট্যাম্প ডিউটি ও উৎসে করের নিয়ম জানুন।",
                                fontSize = 11.sp,
                                color = Color(0xFFD4E7DC)
                            )
                        }
                        Text("এসএমএস পাঠান >", color = Color(0xFFD4AF37), fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                    }
                }
            }

            // Calculator Card

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Calculate, contentDescription = null, tint = LandGreenPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "দলিল রেজিস্ট্রেশন ফি ক্যালকুলেটর",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = LandGreenDark
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = landPriceInput,
                            onValueChange = { landPriceInput = it.filter { char -> char.isDigit() } },
                            label = { Text("জমির সরকারি বা ক্রয় মূল্য (টাকায়)") },
                            suffix = { Text("টাকা") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            textStyle = TextStyle(color = Color.Black, fontWeight = FontWeight.SemiBold, fontSize = 15.sp),
                            colors = appTextFieldColors()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Surface(
                            color = LandGreenContainer,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "ফি হিসাবের ব্রেকডাউন (আনুমানিক):",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = LandGreenDark
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                FeeRow("১. রেজিস্ট্রেশন ফি (১%):", "৳ ${bdtFormatter.format(result.registrationFee.toLong())}")
                                FeeRow("২. স্ট্যাম্প ডিউটি (১.৫%):", "৳ ${bdtFormatter.format(result.stampDuty.toLong())}")
                                FeeRow("৩. স্থানীয় সরকার কর (২%):", "৳ ${bdtFormatter.format(result.localGovtTax.toLong())}")
                                FeeRow("৪. উৎস কর / AIT (২%):", "৳ ${bdtFormatter.format(result.sourceTax.toLong())}")

                                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = LandGreenPrimary.copy(alpha = 0.3f))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "মোট সরকারি ফি (৬.৫%):",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = LandGreenDark
                                    )
                                    Text(
                                        text = "৳ ${bdtFormatter.format(result.totalFee.toLong())}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = Color(0xFFC62828)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Required Documents Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = LandGreenPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "দলিল রেজিস্ট্রেশনে প্রয়োজনীয় কাগজপত্র",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = LandGreenDark
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        val docs = listOf(
                            "ক্রেতা ও বিক্রেতার জাতীয় পরিচয়পত্র (NID) ও ছবি",
                            "সর্বশেষ সিএস, এসএ, আরএস ও বিএস খতিয়ানের সত্যায়িত কপি",
                            "বিক্রেতার নামে হালনগদ ই-নামজারি ও জমাভাগ খতিয়ান",
                            "হালনগদ ভূমি উন্নয়ন কর (খাজনা) দাখিলা রশিদ",
                            "বিক্রেতা ওয়ারিশ সূত্রে পেলে ওয়ারিশান সনদ ও ফরায়েজ বিবরণী",
                            "বায়না দলিল (যদি আগে করা হয়ে থাকে)",
                            "নাগরিক সনদপত্র ও ই-টিন (e-TIN) সনদ"
                        )

                        docs.forEach { doc ->
                            Row(
                                modifier = Modifier.padding(vertical = 4.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Text("📌 ", fontSize = 13.sp)
                                Text(doc, fontSize = 13.sp, color = Color.DarkGray, lineHeight = 18.sp)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAiAssistant) {
        AiLandAssistantModal(
            initialCategory = AiCategory.REGISTRATION,
            onDismiss = { showAiAssistant = false }
        )
    }
}


@Composable
fun FeeRow(label: String, amount: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 12.sp, color = Color.DarkGray)
        Text(text = amount, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = LandGreenDark)
    }
}
