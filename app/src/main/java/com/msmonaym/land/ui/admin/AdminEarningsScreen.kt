package com.msmonaym.land.ui.admin

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.msmonaym.land.data.AdminManager
import com.msmonaym.land.data.EarningRecord
import com.msmonaym.land.data.MonetizationManager
import com.msmonaym.land.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminEarningsScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val adminManager = remember { AdminManager(context) }
    val monetizationManager = remember { MonetizationManager(context) }

    // Pricing States
    var surveyFee by remember { mutableStateOf(monetizationManager.getSurveyFieldFee()) }
    var consultFee by remember { mutableStateOf(monetizationManager.getConsultationFee()) }
    var khatianFee by remember { mutableStateOf(monetizationManager.getKhatianCheckFee()) }
    var docDraftFee by remember { mutableStateOf(monetizationManager.getDocDraftFee()) }
    var reportFee by remember { mutableStateOf(monetizationManager.getReportDownloadFee()) }

    var totalEarnings by remember { mutableStateOf(monetizationManager.getTotalEarnings()) }
    var earningsList by remember { mutableStateOf(monetizationManager.getEarningsHistory()) }

    // Manual Entry Dialog States
    var showAddEarningDialog by remember { mutableStateOf(false) }
    var newAmount by remember { mutableStateOf("") }
    var newCustomer by remember { mutableStateOf("") }
    var newService by remember { mutableStateOf("ডিজিটাল সার্ভেয়ার ফি") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "দৈনিক আয় ও ফি কন্ট্রোল প্যানেল",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color.White
                        )
                        Text(
                            "এডমিন রেভিনিউ ও প্রাইজ ম্যানেজমেন্ট",
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
            // Earnings Overview Dashboard Card
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
                                        Color(0xFF1E3A8A),
                                        Color(0xFF1E40AF),
                                        Color(0xFF3B82F6)
                                    )
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
                                    "মোট সংগৃহীত আয় (Total Revenue)",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White.copy(alpha = 0.85f)
                                )

                                Surface(
                                    color = GoldAccent,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        "লাইভ হিসাব",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = LandGreenDark,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                "৳ $totalEarnings",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = { showAddEarningDialog = true },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(42.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = GoldAccent)
                                ) {
                                    Icon(Icons.Default.AddCircle, contentDescription = null, tint = LandGreenDark, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("আয় যোগ করুন", color = LandGreenDark, fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                                }
                            }
                        }
                    }
                }
            }

            // Daily Earning Strategies Guide for Admin
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("💡", fontSize = 22.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "অ্যাপ দিয়ে প্রতিদিন টাকা উপার্জনের ৫টি সরাসরি উপায়",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = LandGreenDark
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        val strategies = listOf(
                            "১. ফিল্ড সার্ভেয়ার বুকিং: আপনার এলাকার জমি মাপজোকের সরাসরি অর্ডার নিন (প্রতি কাজে ৳১,৫০০ - ৳৩,০০০)।",
                            "২. টেলিফোন বা হোয়াটসঅ্যাপ পরামর্শ: জমি কেনা-বেচার দলিল যাচাই বা আইনি পরামর্শ দিয়ে ফি নিন (৳২০০ - ৳৫০০)।",
                            "৩. বায়নানামা ও চুক্তিপত্র ড্রাফটিং: অ্যাপে ফর্ম পূরণ করে দিলে গ্রাহকের জন্য প্রফেশনাল ড্রাফট প্রস্তুত করুন (৳৩০০ - ৳১,০০০)।",
                            "৪. খতিয়ান ও পর্চা অনুসন্ধান ফি: অনলাইনে দাগের তথ্য বা খতিয়ান কপি বের করে দিয়ে প্রতিটিতে ৳১০০-৳২০০ আয় করুন।",
                            "৫. ডিজিটাল কিউআর পোস্টার: আপনার অফিস বা ফেসবুক পেজে কিউআর কোড ছড়িয়ে দিয়ে সারা দেশ থেকে কাস্টমার পান।"
                        )

                        strategies.forEach { s ->
                            Text(
                                text = s,
                                fontSize = 11.5.sp,
                                color = Color(0xFF334155),
                                lineHeight = 17.sp,
                                modifier = Modifier.padding(vertical = 3.dp)
                            )
                        }
                    }
                }
            }

            // Service Pricing Control Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.PriceChange, contentDescription = null, tint = LandGreenPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "সেবা ফি ও রেট নির্ধারণ (Rate Chart)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = LandGreenDark
                            )
                        }
                        Text(
                            "এখানে আপনার পছন্দমতো যেকোনো সেবার রেট পরিবর্তন করে 'সংরক্ষণ করুন' বাটনে চাপ দিন। সকল ব্যবহারকারী নতুন রেট দেখতে পাবে।",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Price Inputs
                        OutlinedTextField(
                            value = surveyFee,
                            onValueChange = { surveyFee = it },
                            label = { Text("সার্ভেয়ার ফিল্ড ভিজিট ফি (টাকা)") },
                            leadingIcon = { Text("৳ ", fontWeight = FontWeight.Bold, color = LandGreenPrimary, modifier = Modifier.padding(start = 12.dp)) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            textStyle = TextStyle(color = Color.Black, fontSize = 14.sp, fontWeight = FontWeight.Bold),
                            colors = appTextFieldColors()
                        )

                        OutlinedTextField(
                            value = consultFee,
                            onValueChange = { consultFee = it },
                            label = { Text("আইনি ও দলিল পরামর্শ ফি (টাকা)") },
                            leadingIcon = { Text("৳ ", fontWeight = FontWeight.Bold, color = LandGreenPrimary, modifier = Modifier.padding(start = 12.dp)) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            textStyle = TextStyle(color = Color.Black, fontSize = 14.sp, fontWeight = FontWeight.Bold),
                            colors = appTextFieldColors()
                        )

                        OutlinedTextField(
                            value = docDraftFee,
                            onValueChange = { docDraftFee = it },
                            label = { Text("বায়নানামা ও চুক্তিপত্র ড্রাফট ফি (টাকা)") },
                            leadingIcon = { Text("৳ ", fontWeight = FontWeight.Bold, color = LandGreenPrimary, modifier = Modifier.padding(start = 12.dp)) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            textStyle = TextStyle(color = Color.Black, fontSize = 14.sp, fontWeight = FontWeight.Bold),
                            colors = appTextFieldColors()
                        )

                        OutlinedTextField(
                            value = khatianFee,
                            onValueChange = { khatianFee = it },
                            label = { Text("খতিয়ান ও পর্চা অনুসন্ধান ফি (টাকা)") },
                            leadingIcon = { Text("৳ ", fontWeight = FontWeight.Bold, color = LandGreenPrimary, modifier = Modifier.padding(start = 12.dp)) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            textStyle = TextStyle(color = Color.Black, fontSize = 14.sp, fontWeight = FontWeight.Bold),
                            colors = appTextFieldColors()
                        )

                        OutlinedTextField(
                            value = reportFee,
                            onValueChange = { reportFee = it },
                            label = { Text("অফিসিয়াল সার্ভে রিপোর্ট ফি (টাকা)") },
                            leadingIcon = { Text("৳ ", fontWeight = FontWeight.Bold, color = LandGreenPrimary, modifier = Modifier.padding(start = 12.dp)) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            textStyle = TextStyle(color = Color.Black, fontSize = 14.sp, fontWeight = FontWeight.Bold),
                            colors = appTextFieldColors()
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Button(
                            onClick = {
                                monetizationManager.setSurveyFieldFee(surveyFee)
                                monetizationManager.setConsultationFee(consultFee)
                                monetizationManager.setDocDraftFee(docDraftFee)
                                monetizationManager.setKhatianCheckFee(khatianFee)
                                monetizationManager.setReportDownloadFee(reportFee)
                                Toast.makeText(context, "নতুন সার্ভিস চার্জ রেট সফলভাবে সংরক্ষিত হয়েছে!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = LandGreenPrimary)
                        ) {
                            Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("নতুন ফি চার্ট সংরক্ষণ করুন", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Earnings History Log
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "আয়ের সাম্প্রতিক হিস্ট্রি",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color(0xFF1E293B)
                    )
                    Text(
                        "মোট ${earningsList.size} টি এন্ট্রি",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }

            if (earningsList.isEmpty()) {
                item {
                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("📝", fontSize = 32.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "এখনও কোনো আয় এন্ট্রি করা হয়নি",
                                fontSize = 12.sp,
                                color = Color.Gray,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            } else {
                items(earningsList) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    color = Color(0xFF10B981).copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text("৳", color = Color(0xFF10B981), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        item.service,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = LandGreenDark
                                    )
                                    Text(
                                        "গ্রাহক: ${item.customer} • ${formatDate(item.timestamp)}",
                                        fontSize = 10.5.sp,
                                        color = Color(0xFF64748B)
                                    )
                                }
                            }

                            Text(
                                "+৳${item.amount}",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 15.sp,
                                color = Color(0xFF10B981)
                            )
                        }
                    }
                }
            }
        }
    }

    // Add Earning Dialog
    if (showAddEarningDialog) {
        AlertDialog(
            onDismissRequest = { showAddEarningDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = LandGreenPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("নতুন আয় এন্ট্রি করুন", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = LandGreenDark)
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = newAmount,
                        onValueChange = { newAmount = it },
                        label = { Text("টাকার পরিমাণ (৳)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        textStyle = TextStyle(color = Color.Black, fontSize = 14.sp, fontWeight = FontWeight.Bold),
                        colors = appTextFieldColors()
                    )

                    OutlinedTextField(
                        value = newCustomer,
                        onValueChange = { newCustomer = it },
                        label = { Text("গ্রাহকের নাম বা ফোন") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true,
                        textStyle = TextStyle(color = Color.Black, fontSize = 14.sp, fontWeight = FontWeight.Medium),
                        colors = appTextFieldColors()
                    )

                    OutlinedTextField(
                        value = newService,
                        onValueChange = { newService = it },
                        label = { Text("সেবার বিবরণ / উদ্দেশ্য") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true,
                        textStyle = TextStyle(color = Color.Black, fontSize = 14.sp, fontWeight = FontWeight.Medium),
                        colors = appTextFieldColors()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amt = newAmount.toLongOrNull() ?: 0L
                        if (amt <= 0L) {
                            Toast.makeText(context, "সঠিক টাকার পরিমাণ দিন", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        monetizationManager.recordNewEarning(
                            amount = amt,
                            customer = newCustomer.ifBlank { "সাধারণ গ্রাহক" },
                            service = newService.ifBlank { "ভূমি সেবা" }
                        )
                        totalEarnings = monetizationManager.getTotalEarnings()
                        earningsList = monetizationManager.getEarningsHistory()
                        showAddEarningDialog = false
                        Toast.makeText(context, "আয় সফলভাবে রেকর্ড হয়েছে!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = LandGreenPrimary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("সেভ করুন")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddEarningDialog = false }) {
                    Text("বাতিল", color = Color.Gray)
                }
            }
        )
    }
}

private fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
