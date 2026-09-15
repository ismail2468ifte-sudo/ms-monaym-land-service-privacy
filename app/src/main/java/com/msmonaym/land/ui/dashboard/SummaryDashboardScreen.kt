package com.msmonaym.land.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.msmonaym.land.data.ai.AiCategory
import com.msmonaym.land.ui.ai.AiLandAssistantModal
import com.msmonaym.land.ui.navigation.Screen
import com.msmonaym.land.ui.theme.LandGreenDark
import com.msmonaym.land.ui.theme.LandGreenPrimary
import com.msmonaym.land.ui.theme.SurfaceLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SummaryDashboardScreen(
    onNavigate: (Screen) -> Unit,
    onBack: () -> Unit
) {
    var showAiModal by remember { mutableStateOf(false) }
    var currentAiCategory by remember { mutableStateOf(AiCategory.GENERAL) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "সার্ভিস সামারি ড্যাশবোর্ড",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color.White
                        )
                        Text(
                            text = "ই-পর্চা, ভূমি উন্নয়ন কর ও সরকারি সেবা স্ট্যাটাস",
                            fontSize = 10.5.sp,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = LandGreenDark
                )
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
            item {
                SummaryDashboardView(
                    onNavigate = onNavigate,
                    onLaunchAi = { category ->
                        currentAiCategory = category
                        showAiModal = true
                    },
                    isEmbedded = false
                )
            }
        }
    }

    if (showAiModal) {
        AiLandAssistantModal(
            initialCategory = currentAiCategory,
            onDismiss = { showAiModal = false }
        )
    }
}
