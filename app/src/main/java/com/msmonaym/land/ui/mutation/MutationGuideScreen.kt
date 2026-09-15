package com.msmonaym.land.ui.mutation

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.msmonaym.land.ui.theme.*

data class MutationDocItem(
    val title: String,
    val description: String,
    val isMandatory: Boolean = true
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MutationGuideScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current

    val checklistItems = remember {
        listOf(
            MutationDocItem("মূল হস্তান্তর দলিল", "রেজিস্ট্রিকৃত মূল দলিল বা দলিলের সার্টিফাইড নকল", true),
            MutationDocItem("বায়া / পিঠ দলিল", "ক্রয়কৃত জমির পূর্ববর্তী ক্রমিকে হস্তান্তরের দলিলসমূহ", true),
            MutationDocItem("খতিয়ান কপি", "সর্বশেষ আরএস (RS) বা বিএস (BS) বা সিটি জরিপ খতিয়ান", true),
            MutationDocItem("হাল সনের ভূমি কর দাখিলা", "বর্তমান বাংলা সনের খাজনা পরিশোধের রশিদ", true),
            MutationDocItem("জাতীয় পরিচয়পত্র (NID)", "আবেদনকারীর এনআইডি কার্ড ও মোবাইল নম্বর", true),
            MutationDocItem("ওয়ারিশান সনদপত্র", "উত্তরাধিকার সূত্রে মালিকানা হলে স্থানীয় কাউন্সিলর/চেয়ারম্যানের সনদ", false),
            MutationDocItem("পাসপোর্ট সাইজ ছবি", "আবেদনকারীর সাম্প্রতিক ছবি", false)
        )
    }

    val checkedStates = remember { mutableStateMapOf<Int, Boolean>() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "ই-নামজারি সহায়িকা ও ফি হিসাব",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "জমি খারিজের নিয়মাবলী ও প্রয়োজনীয় চেকলিস্ট",
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
            // Header Info Card
            item {
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, BorderColor),
                    shadowElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = LandGreenContainer,
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("🏛️", fontSize = 22.sp)
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "নামজারি (Mutation) কেন জরুরি?",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = LandGreenDark
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = "জমি ক্রয়ের পর নামজারি না করলে সরকারি রেকর্ডে মালিকানা কার্যকর হয় না এবং ভবিষ্যতে জমি বিক্রয়, ব্যাংক লোন বা খাজনা দেওয়া যায় না।",
                                fontSize = 11.5.sp,
                                color = TextSecondary,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }

            // Official Fee Breakdown
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                    border = BorderStroke(1.2.dp, LandGreenLight.copy(alpha = 0.6f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "💰 সরকারি ফি হিসাব (Government Fees)",
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = LandGreenDark
                            )
                            Surface(
                                color = LandGreenPrimary,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "মোট ১,১৭০/-",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = DividerColor)
                        Spacer(modifier = Modifier.height(8.dp))

                        FeeRow(label = "১. আবেদন কোর্ট ফি", amount = "২০/- টাকা")
                        FeeRow(label = "২. নোটিশ জারি ও প্রক্রিয়াকরণ ফি", amount = "৫০/- টাকা")
                        FeeRow(label = "৩. খতিয়ান প্রণয়ন / রেকর্ড সংশোধন ফি", amount = "১,০০০/- টাকা")
                        FeeRow(label = "৪. ডিসিআর (DCR) ডুপ্লিকেট ফি", amount = "১০০/- টাকা")

                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider(color = LandGreenLight.copy(alpha = 0.5f))
                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "সর্বমোট সরকারি খরচ:",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = LandGreenDark
                            )
                            Text(
                                text = "১,১৭০/- টাকা",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 13.5.sp,
                                color = LandGreenPrimary
                            )
                        }
                    }
                }
            }

            // Interactive Checklist
            item {
                Text(
                    text = "📋 প্রয়োজনীয় কাগজপত্রের চেকলিস্ট (কাগজ মিলিয়ে টিক দিন):",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            items(checklistItems.size) { index ->
                val item = checklistItems[index]
                val isChecked = checkedStates[index] ?: false

                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, if (isChecked) LandGreenPrimary else BorderColor),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { checkedStates[index] = !isChecked }
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = isChecked,
                            onCheckedChange = { checkedStates[index] = it },
                            colors = CheckboxDefaults.colors(checkedColor = LandGreenPrimary)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = item.title,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isChecked) LandGreenDark else TextPrimary
                                )
                                if (item.isMandatory) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "*বাধ্যতামূলক",
                                        fontSize = 9.5.sp,
                                        color = ErrorRed,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Text(
                                text = item.description,
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }
                    }
                }
            }

            // Step-by-Step Procedure
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, BorderColor),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "🔄 নামজারি প্রক্রিয়ার ৪টি পর্যায়:",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = LandGreenDark
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        ProcedureStep(
                            stepNumber = "১",
                            title = "আবেদন ও ফি প্রদান",
                            desc = "এনআইডি, মোবাইল নম্বর ও প্রয়োজনীয় কাগজপত্র দিয়ে ৭০/- টাকা প্রাথমিক ফি জমা দিন।"
                        )
                        ProcedureStep(
                            stepNumber = "২",
                            title = "সহকারী কমিশনার (ভূমি) যাচাই",
                            desc = "ইউনিয়ন ভূমি সহকারী কর্মকর্তা (তহশিলদার) সরেজমিনে দখল ও রেকর্ড যাচাই করবেন।"
                        )
                        ProcedureStep(
                            stepNumber = "৩",
                            title = "শুনানি ও আপত্তি নিষ্পত্তি",
                            desc = "যদি কোনো শরিক বা সাবেক মালিকের আপত্তি থাকে তা উভয় পক্ষের উপস্থিতিতে শুনানি হয়।"
                        )
                        ProcedureStep(
                            stepNumber = "৪",
                            title = "খতিয়ান সৃজন ও ডিসিআর কপি",
                            desc = "অনুমোদন সাপেক্ষে ১,১০০/- টাকা চূড়ান্ত ফি পরিশোধ করে নতুন নামজারি খতিয়ান গ্রহণ করুন।"
                        )
                    }
                }
            }

            // Citizen Charter & Hotline Support
            item {
                Surface(
                    color = IndigoTint,
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, Color(0xFFC7D2FE)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "⏱️ সরকারি সময়সীমা: সর্বোচ্চ ২৮ কার্যদিবস",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E1B4B)
                            )
                            Text(
                                text = "প্রবাসী বাংলাদেশিদের জন্য বিশেষ অগ্রাধিকার: ৯ কার্যদিবস",
                                fontSize = 10.5.sp,
                                color = Color(0xFF3730A3)
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
                            Text("১৬১২২ কল", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FeeRow(label: String, amount: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 12.sp, color = TextSecondary)
        Text(text = amount, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
    }
}

@Composable
private fun ProcedureStep(stepNumber: String, title: String, desc: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.Top
    ) {
        Surface(
            shape = CircleShape,
            color = LandGreenPrimary,
            modifier = Modifier.size(24.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(text = stepNumber, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(text = title, fontSize = 12.5.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Text(text = desc, fontSize = 11.sp, color = TextSecondary, lineHeight = 15.sp)
        }
    }
}
