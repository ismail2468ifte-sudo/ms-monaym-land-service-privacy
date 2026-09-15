package com.msmonaym.land.ui.khatian

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.msmonaym.land.data.LandDataRepository
import com.msmonaym.land.model.KhatianInfo
import com.msmonaym.land.ui.theme.LandGreenContainer
import com.msmonaym.land.ui.theme.LandGreenDark
import com.msmonaym.land.ui.theme.LandGreenPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KhatianScreen(
    onBack: () -> Unit
) {
    val khatians = remember { LandDataRepository.khatianList }
    var selectedKhatianType by remember { mutableStateOf<String?>(khatians.firstOrNull()?.type) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("খতিয়ান নির্দেশিকা (CS, SA, RS, BS)", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = LandGreenPrimary)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF4F7F5))
        ) {
            // Header Info Banner
            Surface(
                color = LandGreenContainer,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Assignment, contentDescription = null, tint = LandGreenPrimary, modifier = Modifier.size(28.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "খতিয়ান চেনার সহজ উপায় ও সময়কাল",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = LandGreenDark
                        )
                        Text(
                            text = "সিএস থেকে বিএস পর্যন্ত বাংলাদেশ ভূ-সম্পত্তির খতিয়ান পরিচিতি",
                            fontSize = 12.sp,
                            color = Color.DarkGray
                        )
                    }
                }
            }

            LazyColumn(
                contentPadding = PaddingValues(14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(khatians) { khatian ->
                    KhatianCard(
                        khatian = khatian,
                        isExpanded = selectedKhatianType == khatian.type,
                        onToggle = {
                            selectedKhatianType = if (selectedKhatianType == khatian.type) null else khatian.type
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun KhatianCard(
    khatian: KhatianInfo,
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
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = LandGreenContainer,
                        modifier = Modifier.size(46.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = khatian.type,
                                fontWeight = FontWeight.Bold,
                                color = LandGreenPrimary,
                                fontSize = 15.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = khatian.fullName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = LandGreenDark
                        )
                        Text(
                            text = "সময়কাল: ${khatian.period}",
                            fontSize = 12.sp,
                            color = Color(0xFFC62828),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                IconButton(onClick = onToggle) {
                    Icon(
                        if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "Expand",
                        tint = LandGreenPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = khatian.description,
                fontSize = 13.sp,
                color = Color.DarkGray,
                lineHeight = 18.sp
            )

            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    HorizontalDivider(color = Color(0xFFE0E0E0))
                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "মূল বৈশিষ্ট্যসমূহ:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = LandGreenPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    khatian.keyFeatures.forEach { feature ->
                        Row(
                            modifier = Modifier.padding(vertical = 2.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text("✓ ", fontWeight = FontWeight.Bold, color = LandGreenPrimary)
                            Text(feature, fontSize = 12.sp, color = Color.DarkGray)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Surface(
                        color = Color(0xFFFFF8E1),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "💡 চেনার বিশেষ নির্দেশিকা:",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = Color(0xFFE65100)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = khatian.identificationTips,
                                fontSize = 12.sp,
                                color = Color(0xFF4E342E)
                            )
                        }
                    }
                }
            }
        }
    }
}
