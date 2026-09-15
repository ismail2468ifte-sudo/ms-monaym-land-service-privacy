package com.msmonaym.land.ui.tax

import android.content.Intent
import android.net.Uri
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.msmonaym.land.ui.theme.*

enum class LandUsageType(val title: String, val emoji: String) {
    AGRICULTURAL("কৃষিজমি (ধানি/নাল)", "🌾"),
    RESIDENTIAL("আবাসিক (বাস্তু/ভিটা)", "🏡"),
    COMMERCIAL("বাণিজ্যিক (দোকান/মার্কেট)", "🏢"),
    INDUSTRIAL("শিল্প কারখানা ও প্রকল্প", "🏭")
}

enum class LocationArea(val title: String, val rateFactor: Double) {
    CITY_CORP("সিটি কর্পোরেশন এলাকা", 1.8),
    POURASHAVA("পৌরসভা এলাকা", 1.2),
    UNION_RURAL("ইউনিয়ন / গ্রামীণ এলাকা", 0.6)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LandTaxCalculatorScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current

    var selectedUsage by remember { mutableStateOf(LandUsageType.AGRICULTURAL) }
    var selectedLocation by remember { mutableStateOf(LocationArea.UNION_RURAL) }
    var inputDecimals by remember { mutableStateOf("") }
    var calculatedTaxResult by remember { mutableStateOf<Double?>(null) }
    var isExempted by remember { mutableStateOf(false) }
    var taxMessage by remember { mutableStateOf("") }

    fun calculateTax() {
        val decimals = inputDecimals.toDoubleOrNull() ?: 0.0
        if (decimals <= 0.0) {
            calculatedTaxResult = null
            taxMessage = "অনুগ্রহ করে সঠিক জমির পরিমাণ (শতাংশ) লিখুন"
            isExempted = false
            return
        }

        // 25 Bighas = 825 decimals
        if (selectedUsage == LandUsageType.AGRICULTURAL) {
            if (decimals <= 825.0) {
                isExempted = true
                calculatedTaxResult = 0.0
                taxMessage = "সরকারি গেজেট অনুযায়ী ২৫ বিঘা (৮২৫ শতাংশ) পর্যন্ত কৃষি জমির কোনো ভূমি উন্নয়ন কর প্রদেয় নহে। কর সম্পূর্ণ মওকুফ!"
            } else {
                isExempted = false
                // Agricultural tax above 25 bighas: nominal progressive rate
                val excess = decimals - 825.0
                val tax = (excess * 2.5 * selectedLocation.rateFactor).coerceAtLeast(50.0)
                calculatedTaxResult = tax
                taxMessage = "২৫ বিঘার অতিরিক্ত জমির জন্য আনুমানিক বার্ষিক কর হিসাব করা হয়েছে।"
            }
        } else {
            isExempted = false
            val baseRatePerDecimal = when (selectedUsage) {
                LandUsageType.RESIDENTIAL -> 25.0
                LandUsageType.COMMERCIAL -> 60.0
                LandUsageType.INDUSTRIAL -> 80.0
                else -> 20.0
            }
            val tax = decimals * baseRatePerDecimal * selectedLocation.rateFactor
            calculatedTaxResult = tax
            taxMessage = "${selectedUsage.title} এবং ${selectedLocation.title} অনুযায়ী প্রাক্কলিত বাৎসরিক খাজনা।"
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "ভূমি উন্নয়ন কর ক্যালকুলেটর",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "খাজনা হিসাব ও ২৫ বিঘা মওকুফ নীতিমালা",
                            fontSize = 11.sp,
                            color = GoldAccent
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
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
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Calculator Card
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, BorderColor),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = RoseTint,
                                modifier = Modifier.size(38.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("💰", fontSize = 20.sp)
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "বাৎসরিক খাজনা / কর ক্যালকুলেটর",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = LandGreenDark
                                )
                                Text(
                                    text = "জমির বিবরণ দিয়ে আনুমানিক কর হিসাব করুন",
                                    fontSize = 11.sp,
                                    color = TextMuted
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Land Usage Selector
                        Text(
                            text = "১. জমির শ্রেণী ও ব্যবহার:",
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            LandUsageType.values().forEach { type ->
                                val isSelected = selectedUsage == type
                                Surface(
                                    color = if (isSelected) LandGreenPrimary else Color(0xFFF1F5F9),
                                    shape = RoundedCornerShape(10.dp),
                                    border = BorderStroke(1.dp, if (isSelected) LandGreenPrimary else BorderColor),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable {
                                            selectedUsage = type
                                            if (calculatedTaxResult != null) calculateTax()
                                        }
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 2.dp)
                                    ) {
                                        Text(text = type.emoji, fontSize = 16.sp)
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = type.title.split(" ")[0],
                                            fontSize = 10.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) Color.White else TextPrimary
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Location Selector
                        Text(
                            text = "২. জমির ভৌগোলিক অবস্থান:",
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        LocationArea.values().forEach { area ->
                            val isSelected = selectedLocation == area
                            Surface(
                                color = if (isSelected) LandGreenContainer else Color.White,
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, if (isSelected) LandGreenPrimary else BorderColor),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp)
                                    .clickable {
                                        selectedLocation = area
                                        if (calculatedTaxResult != null) calculateTax()
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = {
                                            selectedLocation = area
                                            if (calculatedTaxResult != null) calculateTax()
                                        },
                                        colors = RadioButtonDefaults.colors(selectedColor = LandGreenPrimary)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = area.title,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) LandGreenDark else TextPrimary
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Land Amount Input
                        Text(
                            text = "৩. জমির পরিমাণ (শতাংশ / ডেসিমাল):",
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        OutlinedTextField(
                            value = inputDecimals,
                            onValueChange = { inputDecimals = it.filter { ch -> ch.isDigit() || ch == '.' } },
                            label = { Text("মোট জমি (শতাংশ হিসেবে)") },
                            placeholder = { Text("যেমন: ২০ অথবা ৮২৫") },
                            trailingIcon = { Text("শতাংশ", fontSize = 11.sp, color = TextMuted, modifier = Modifier.padding(end = 12.dp)) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.fillMaxWidth(),
                            textStyle = TextStyle(color = Color.Black, fontSize = 14.sp, fontWeight = FontWeight.Bold),
                            colors = appTextFieldColors()
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = { calculateTax() },
                            colors = ButtonDefaults.buttonColors(containerColor = LandGreenPrimary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(vertical = 12.dp)
                        ) {
                            Icon(Icons.Default.Calculate, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("কর হিসাব করুন", fontSize = 13.5.sp, fontWeight = FontWeight.Bold)
                        }

                        // Calculation Result
                        if (calculatedTaxResult != null) {
                            Spacer(modifier = Modifier.height(14.dp))
                            Surface(
                                color = if (isExempted) EmeraldTint else Color(0xFFFFFBEB),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, if (isExempted) SuccessGreen else GoldAccent),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = if (isExempted) "🎉 সরকারি কর মওকুফ" else "💵 আনুমানিক বাৎসরিক কর:",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isExempted) LandGreenDark else Color(0xFF92400E)
                                        )
                                        Text(
                                            text = if (isExempted) "০.০০ টাকা" else String.format("%.2f টাকা", calculatedTaxResult),
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = if (isExempted) SuccessGreen else Color(0xFFB45309)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = taxMessage,
                                        fontSize = 11.5.sp,
                                        color = TextSecondary,
                                        lineHeight = 15.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 25 Bighas Exemption Rule
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, BorderColor),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("📜", fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "২৫ বিঘা কৃষি জমি কর মওকুফ নীতিমালা",
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = LandGreenDark
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "• ১৯৭২ সালের রাষ্ট্রপতির ৯৮ নং আদেশ অনুযায়ী কোনো ব্যক্তি বা পরিবারের মোট কৃষিজমির পরিমাণ ২৫ বিঘা (৮.২৫ একর / ৮২৫ শতাংশ) পর্যন্ত হলে কোনো ভূমি উন্নয়ন কর দিতে হয় না।\n• তবে জমির মালিকানা সক্রিয় রাখতে প্রতি ৩ বছর পর পর সংশ্লিষ্ট ইউনিয়ন ভূমি অফিসে নামজারি ও হোল্ডিং নবায়ন দাখিলা গ্রহণ করা উত্তম।",
                            fontSize = 11.5.sp,
                            color = TextSecondary,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            // Arrears & Surcharge Rules
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, BorderColor),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("⚠️", fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "বকেয়া খাজনা ও জরিমানার নিয়মাবলী",
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = LandGreenDark
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "• বিগত বছরের খাজনা বকেয়া থাকলে ভূমি উন্নয়ন কর আইন ২০২৩ অনুযায়ী বাৎসরিক ৬.২৫% হারে সাধারণ সুদ বা জরিমানা যুক্ত হতে পারে।\n• নিয়মিত হাল সনের খাজনা পরিশোধ করলে কিউআর কোড সংবলিত ডিজিটাল দাখিলা পাওয়া যায় যা নামজারি ও ব্যাংক ঋণের জন্য অপরিহার্য।",
                            fontSize = 11.5.sp,
                            color = TextSecondary,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            // Hotline Support Card
            item {
                Surface(
                    color = LandGreenContainer,
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, LandGreenLight.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "ভূমি কর ও দাখিলা হেল্পলাইন: ১৬১২২",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = LandGreenDark
                            )
                            Text(
                                text = "হোল্ডিং সমস্যা বা কর সংক্রান্ত তথ্যে কল করুন",
                                fontSize = 10.5.sp,
                                color = TextMuted
                            )
                        }
                        Button(
                            onClick = {
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:16122"))
                                context.startActivity(intent)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = LandGreenPrimary),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text("কল করুন", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
