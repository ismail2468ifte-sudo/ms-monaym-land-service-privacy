package com.msmonaym.land.ui.law

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.msmonaym.land.data.LandDataRepository
import com.msmonaym.land.data.ai.AiCategory
import com.msmonaym.land.model.LandLaw
import com.msmonaym.land.ui.ai.AiFloatingActionButton
import com.msmonaym.land.ui.ai.AiLandAssistantModal
import com.msmonaym.land.ui.ai.AiTopBarAction
import com.msmonaym.land.ui.theme.LandGreenContainer
import com.msmonaym.land.ui.theme.LandGreenDark
import com.msmonaym.land.ui.theme.LandGreenPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LawScreen(
    onBack: () -> Unit
) {
    val laws = remember { LandDataRepository.landLaws }
    var expandedLawId by remember { mutableStateOf<String?>(laws.firstOrNull()?.id) }
    var showAiAssistant by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("জমি সংক্রান্ত আইন", fontWeight = FontWeight.Bold, color = Color.White) },
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
            AiFloatingActionButton(AiCategory.LAW) {
                showAiAssistant = true
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF4F7F5))
        ) {
            Surface(
                color = LandGreenContainer,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Gavel, contentDescription = null, tint = LandGreenPrimary, modifier = Modifier.size(28.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "বাংলাদেশের মূল ভূমি ও সম্পত্তি আইনের গুরুত্বপূর্ণ বিধিমালা ও নিয়মকানুন",
                        fontSize = 13.sp,
                        color = LandGreenDark,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            LazyColumn(
                contentPadding = PaddingValues(14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // AI Law Consultation Banner
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
                                    text = "জমি আইন ডিজিটাল এআই পরামর্শক",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Color.White
                                )
                                Text(
                                    text = "এসএমএস লিখে ওয়ারিশান সম্পত্তি, বাটোয়ারা বা অপরাধ আইন ২০২৩ নিয়ে তাৎক্ষণিক সমাধান পান।",
                                    fontSize = 11.sp,
                                    color = Color(0xFFD4E7DC)
                                )
                            }
                            Text("এসএমএস পাঠান >", color = Color(0xFFD4AF37), fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                        }
                    }
                }

                items(laws) { law ->
                    LawCard(
                        law = law,
                        isExpanded = expandedLawId == law.id,
                        onToggle = {
                            expandedLawId = if (expandedLawId == law.id) null else law.id
                        }
                    )
                }
            }
        }
    }

    if (showAiAssistant) {
        AiLandAssistantModal(
            initialCategory = AiCategory.LAW,
            onDismiss = { showAiAssistant = false }
        )
    }
}


@Composable
fun LawCard(
    law: LandLaw,
    isExpanded: Boolean,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Surface(
                        color = LandGreenContainer,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = law.category,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = LandGreenPrimary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = law.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = LandGreenDark
                    )
                }

                IconButton(onClick = onToggle) {
                    Icon(
                        if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "Expand",
                        tint = LandGreenPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = law.shortDescription,
                fontSize = 13.sp,
                color = Color.DarkGray
            )

            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    HorizontalDivider(color = Color(0xFFE0E0E0))
                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "আইনের বিস্তারিত:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = LandGreenPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = law.fullContent,
                        fontSize = 13.sp,
                        color = Color(0xFF222222),
                        lineHeight = 20.sp
                    )

                    if (law.keyPoints.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "গুরুত্বপূর্ণ পয়েন্টসমূহ:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = LandGreenDark
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        law.keyPoints.forEach { point ->
                            Row(
                                modifier = Modifier.padding(vertical = 2.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Text("• ", fontWeight = FontWeight.Bold, color = LandGreenPrimary)
                                Text(point, fontSize = 12.sp, color = Color.DarkGray)
                            }
                        }
                    }
                }
            }
        }
    }
}
