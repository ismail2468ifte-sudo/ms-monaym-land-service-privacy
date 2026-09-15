package com.msmonaym.land.ui.dashboard

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.msmonaym.land.data.ai.AiCategory
import com.msmonaym.land.ui.navigation.Screen
import com.msmonaym.land.ui.theme.*

/**
 * Material 3 Summary Dashboard View.
 * Displays system status icons, live availability indicators, and quick-action cards
 * for common services like 'E-Porcha', 'Land Tax' (LD Tax), 'E-Mutation', and 'Measurement'.
 */
@Composable
fun SummaryDashboardView(
    modifier: Modifier = Modifier,
    onNavigate: (Screen) -> Unit,
    onLaunchAi: (AiCategory) -> Unit,
    isEmbedded: Boolean = false
) {
    val context = LocalContext.current
    var selectedFilter by remember { mutableStateOf("সব সেবা") }
    var trackingDialogService by remember { mutableStateOf<String?>(null) }

    val filterOptions = remember {
        listOf("সব সেবা", "ই-পর্চা ও খতিয়ান", "ভূমি উন্নয়ন কর", "ই-নামজারি", "পরিমাপ ও হিস্যা")
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 1. Dashboard Master Header & Status Indicator Card
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Top row: Title + Live Status Badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = LandGreenContainer,
                            border = BorderStroke(1.dp, LandGreenPrimary.copy(alpha = 0.3f)),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.Dashboard,
                                    contentDescription = "Dashboard",
                                    tint = LandGreenPrimary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "সার্ভিস সামারি ড্যাশবোর্ড",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 15.sp,
                                color = LandGreenDark
                            )
                            Text(
                                text = "লাইভ পোর্টাল স্ট্যাটাস ও কুইক অ্যাকশন",
                                fontSize = 10.5.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    // Live Status Pill
                    Surface(
                        color = Color(0xFFF0FDF4),
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.dp, SuccessGreen.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(SuccessGreen)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "পোর্টাল সক্রিয় (Online)",
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = LandGreenDark
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Status Icons Row (M3 Surface Badges with Status Icons)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatusIndicatorPill(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.CheckCircle,
                        label = "ই-পর্চা",
                        status = "অনলাইন",
                        statusColor = SuccessGreen,
                        bgColor = EmeraldTint
                    )
                    StatusIndicatorPill(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Payment,
                        label = "ভূমি কর",
                        status = "সক্রিয়",
                        statusColor = SuccessGreen,
                        bgColor = AmberTint
                    )
                    StatusIndicatorPill(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Speed,
                        label = "এআই রেসপন্স",
                        status = "০.৪ সে.",
                        statusColor = LandGreenPrimary,
                        bgColor = SkyTint
                    )
                    StatusIndicatorPill(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.GppGood,
                        label = "গেটওয়ে",
                        status = "ভেরিফাইড",
                        statusColor = Color(0xFF2563EB),
                        bgColor = IndigoTint
                    )
                }
            }
        }

        // 2. Material 3 Category Filter Chips
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 2.dp)
        ) {
            items(filterOptions) { filter ->
                val isSelected = selectedFilter == filter
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedFilter = filter },
                    label = {
                        Text(
                            text = filter,
                            fontSize = 11.5.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = LandGreenPrimary,
                        selectedLabelColor = Color.White,
                        containerColor = Color.White,
                        labelColor = TextPrimary
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        borderColor = BorderColor,
                        selectedBorderColor = LandGreenPrimary
                    ),
                    shape = RoundedCornerShape(10.dp)
                )
            }
        }

        // 3. Quick Action Cards for Common Services

        // CARD 1: E-Porcha & Khatian Quick-Action Card
        if (selectedFilter == "সব সেবা" || selectedFilter == "ই-পর্চা ও খতিয়ান") {
            CommonServiceDashboardCard(
                title = "খতিয়ান ও পর্চা নির্দেশিকা",
                subtitle = "সিএস, এসএ, আরএস, বিএস ও সিটি জরিপের রেকর্ড চেনার উপায় ও নকল সংগ্রহের নিয়ম",
                portalBadge = "খতিয়ান গাইড",
                emoji = "📜",
                statusText = "অফলাইন নির্দেশিকা প্রস্তুত",
                statusIcon = Icons.Default.CheckCircle,
                statusColor = SuccessGreen,
                tintColor = AmberTint,
                highlightFeatures = listOf("CS/SA/RS/BS চেনার উপায়", "হিস্যা ও দাগ বিবরণী", "নকল উত্তোলনের নিয়ম"),
                primaryActionText = "খতিয়ান পরিচিতি",
                primaryActionIcon = Icons.Default.Search,
                onPrimaryAction = { onNavigate(Screen.Khatian) },
                secondaryActionText = "পর্চা গাইড",
                secondaryActionIcon = Icons.Default.Description,
                onSecondaryAction = { onNavigate(Screen.Khatian) },
                tertiaryActionText = "আবেদন ট্র্যাকিং",
                onTertiaryAction = { trackingDialogService = "ই-পর্চা" },
                aiActionText = "পর্চা এআই পরামর্শ",
                onAiAction = { onLaunchAi(AiCategory.PORCHA) }
            )
        }

        // CARD 2: Land Tax (LD Tax) Quick-Action Card
        if (selectedFilter == "সব সেবা" || selectedFilter == "ভূমি উন্নয়ন কর") {
            CommonServiceDashboardCard(
                title = "ভূমি উন্নয়ন কর ও খাজনা হিসাব",
                subtitle = "জমির শ্রেণী অনুযায়ী বাৎসরিক খাজনা হিসাব ও ২৫ বিঘা মওকুফ নীতিমালা",
                portalBadge = "কর ক্যালকুলেটর",
                emoji = "💰",
                statusText = "স্বয়ংক্রিয় ক্যালকুলেটর সক্রিয়",
                statusIcon = Icons.Default.Payment,
                statusColor = SuccessGreen,
                tintColor = RoseTint,
                highlightFeatures = listOf("২৫ বিঘা মওকুফ নিয়ম", "শ্রেণীভিত্তিক করের হার", "বকেয়া করের সুদ তথ্য"),
                primaryActionText = "কর হিসাব করুন",
                primaryActionIcon = Icons.Default.Calculate,
                onPrimaryAction = { onNavigate(Screen.Tax) },
                secondaryActionText = "দাখিলার নিয়ম",
                secondaryActionIcon = Icons.Default.ReceiptLong,
                onSecondaryAction = { onNavigate(Screen.Tax) },
                tertiaryActionText = "কর চালান ট্র্যাক",
                onTertiaryAction = { trackingDialogService = "ভূমি উন্নয়ন কর" },
                aiActionText = "কর সহায়িকা এআই",
                onAiAction = { onLaunchAi(AiCategory.TAX) }
            )
        }

        // CARD 3: E-Mutation (ই-নামজারি ও খারিজ) Quick-Action Card
        if (selectedFilter == "সব সেবা" || selectedFilter == "ই-নামজারি") {
            CommonServiceDashboardCard(
                title = "ই-নামজারি ও খারিজ সহায়িকা",
                subtitle = "মালিকানা পরিবর্তনের নামজারি নিয়মাবলী, সরকারি ফি ও প্রয়োজনীয় কাগজ চেকলিস্ট",
                portalBadge = "নামজারি সহায়িকা",
                emoji = "🏛️",
                statusText = "সিটিজেন চার্টার সক্রিয় (২৮ দিন)",
                statusIcon = Icons.Default.AccountBalance,
                statusColor = LandGreenPrimary,
                tintColor = IndigoTint,
                highlightFeatures = listOf("সরকারি ফি ১,১৭০/-", "কাগজপত্রের চেকলিস্ট", "৪ ধাপে নামজারি গাইড"),
                primaryActionText = "নামজারি সহায়িকা",
                primaryActionIcon = Icons.Default.Send,
                onPrimaryAction = { onNavigate(Screen.Mutation) },
                secondaryActionText = "আইন ও নিয়মাবলী",
                secondaryActionIcon = Icons.Default.MenuBook,
                onSecondaryAction = { onNavigate(Screen.Law) },
                tertiaryActionText = "কেস স্টেটাস ট্র্যাক",
                onTertiaryAction = { trackingDialogService = "ই-নামজারি" },
                aiActionText = "নামজারি এআই",
                onAiAction = { onLaunchAi(AiCategory.MUTATION) }
            )
        }

        // CARD 4: Land Calculator & Warish Quick-Action Card
        if (selectedFilter == "সব সেবা" || selectedFilter == "পরিমাপ ও হিস্যা") {
            CommonServiceDashboardCard(
                title = "ডিজিটাল জমি পরিমাপ ও ওয়ারিশ বন্টন",
                subtitle = "ফুট/লিঙ্ক থেকে শতাংশ/কাঠা এবং ১৯৬১ মুসলিম পারিবারিক আইন অনুযায়ী ফারায়েজ হিস্যা",
                portalBadge = "স্মার্ট টুলবক্স",
                emoji = "📐",
                statusText = "১০০% অফলাইন ও ডিজিটাল প্রস্তুত",
                statusIcon = Icons.Default.Straighten,
                statusColor = GoldAccent,
                tintColor = SkyTint,
                highlightFeatures = listOf("A4 PDF রিপোর্ট", "কোরআনিক ফারায়েজ", "আন্তর্জাতিক একক"),
                primaryActionText = "জমি পরিমাপ করুন",
                primaryActionIcon = Icons.Default.SquareFoot,
                onPrimaryAction = { onNavigate(Screen.Calculator) },
                secondaryActionText = "ফারায়েজ ক্যালকুলেটর",
                secondaryActionIcon = Icons.Default.Balance,
                onSecondaryAction = { onNavigate(Screen.WarishCalculator) },
                tertiaryActionText = "সার্ভে টুলবক্স",
                onTertiaryAction = { onNavigate(Screen.SurveyReport) },
                aiActionText = "পরিমাপ এআই সহকারী",
                onAiAction = { onLaunchAi(AiCategory.CALCULATOR) }
            )
        }

        // 4. Hotline & Direct Support Banner
        OutlinedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.outlinedCardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, BorderColor)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = LandGreenContainer,
                        modifier = Modifier.size(34.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.Headphones,
                                contentDescription = null,
                                tint = LandGreenPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "জরুরি সরকারি ভূমি সেবা হটলাইন",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = LandGreenDark
                        )
                        Text(
                            text = "যেকোনো বিভ্রান্তি বা সহযোগিতায় কল করুন ১৬১২২",
                            fontSize = 10.sp,
                            color = TextSecondary
                        )
                    }
                }

                Button(
                    onClick = {
                        val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:16122"))
                        context.startActivity(dialIntent)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = LandGreenPrimary),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("১৬১২২", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    // Interactive Tracking Dialog
    if (trackingDialogService != null) {
        ServiceTrackingDialog(
            serviceName = trackingDialogService ?: "",
            onDismiss = { trackingDialogService = null },
            onOpenPortal = {
                when (trackingDialogService) {
                    "ই-পর্চা" -> onNavigate(Screen.Porcha)
                    "ভূমি উন্নয়ন কর" -> onNavigate(Screen.Tax)
                    "ই-নামজারি" -> onNavigate(Screen.Mutation)
                    else -> onNavigate(Screen.Services)
                }
                trackingDialogService = null
            }
        )
    }
}

/**
 * Status Indicator Pill displayed in the Dashboard Header.
 */
@Composable
private fun StatusIndicatorPill(
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    status: String,
    statusColor: Color,
    bgColor: Color
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = bgColor,
        border = BorderStroke(0.8.dp, statusColor.copy(alpha = 0.25f))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 7.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                icon,
                contentDescription = label,
                tint = statusColor,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                fontSize = 9.5.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = status,
                fontSize = 8.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = statusColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * Common Service Dashboard Card with Material 3 components:
 * - Status Icon
 * - Official Portal Badge
 * - Primary & Secondary Action buttons
 * - AssistChips for tracking and AI advisory
 */
@Composable
fun CommonServiceDashboardCard(
    title: String,
    subtitle: String,
    portalBadge: String,
    emoji: String,
    statusText: String,
    statusIcon: androidx.compose.ui.graphics.vector.ImageVector,
    statusColor: Color,
    tintColor: Color,
    highlightFeatures: List<String>,
    primaryActionText: String,
    primaryActionIcon: androidx.compose.ui.graphics.vector.ImageVector,
    onPrimaryAction: () -> Unit,
    secondaryActionText: String,
    secondaryActionIcon: androidx.compose.ui.graphics.vector.ImageVector,
    onSecondaryAction: () -> Unit,
    tertiaryActionText: String,
    onTertiaryAction: () -> Unit,
    aiActionText: String,
    onAiAction: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Header Row: Emoji icon + Title + Portal Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = tintColor,
                        modifier = Modifier.size(42.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(text = emoji, fontSize = 22.sp)
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.5.sp,
                            color = LandGreenDark,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        // Status Icon & Text
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 2.dp)
                        ) {
                            Icon(
                                statusIcon,
                                contentDescription = null,
                                tint = statusColor,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = statusText,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = statusColor
                            )
                        }
                    }
                }

                // Official portal badge
                Surface(
                    color = LandGreenContainer,
                    shape = RoundedCornerShape(6.dp),
                    border = BorderStroke(0.6.dp, LandGreenPrimary.copy(alpha = 0.3f))
                ) {
                    Text(
                        text = portalBadge,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = LandGreenDark,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.5.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Subtitle Description
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = TextSecondary,
                lineHeight = 15.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Highlight feature tags (M3 SuggestionChips/Badges)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                highlightFeatures.forEach { feature ->
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = SurfaceLight,
                        border = BorderStroke(0.8.dp, BorderColor)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(5.dp)
                                    .clip(CircleShape)
                                    .background(LandGreenPrimary)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = feature,
                                fontSize = 9.5.sp,
                                color = TextPrimary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Quick Action Buttons Row (Material 3 Buttons)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Primary Action Button
                FilledTonalButton(
                    onClick = onPrimaryAction,
                    modifier = Modifier
                        .weight(1.1f)
                        .height(38.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = LandGreenPrimary,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Icon(primaryActionIcon, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = primaryActionText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Secondary Action Button
                OutlinedButton(
                    onClick = onSecondaryAction,
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = LandGreenDark),
                    border = BorderStroke(1.dp, LandGreenPrimary.copy(alpha = 0.6f)),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Icon(secondaryActionIcon, contentDescription = null, modifier = Modifier.size(15.dp), tint = LandGreenDark)
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = secondaryActionText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Tertiary & AI Action Chips (M3 AssistChips)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AssistChip(
                    onClick = onTertiaryAction,
                    label = {
                        Text(
                            text = tertiaryActionText,
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Medium
                        )
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Default.TrackChanges,
                            contentDescription = null,
                            modifier = Modifier.size(13.dp),
                            tint = LandGreenPrimary
                        )
                    },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f),
                    colors = AssistChipDefaults.assistChipColors(containerColor = Color.White),
                    border = BorderStroke(0.8.dp, BorderColor)
                )

                AssistChip(
                    onClick = onAiAction,
                    label = {
                        Text(
                            text = aiActionText,
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Medium
                        )
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Default.AutoAwesome,
                            contentDescription = null,
                            modifier = Modifier.size(13.dp),
                            tint = GoldAccent
                        )
                    },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f),
                    colors = AssistChipDefaults.assistChipColors(containerColor = GoldContainer),
                    border = BorderStroke(0.8.dp, GoldAccent.copy(alpha = 0.5f))
                )
            }
        }
    }
}

/**
 * Interactive Application & Service Tracking Dialog
 */
@Composable
fun ServiceTrackingDialog(
    serviceName: String,
    onDismiss: () -> Unit,
    onOpenPortal: () -> Unit
) {
    val context = LocalContext.current
    var trackingNumberInput by remember { mutableStateOf("") }
    var searchExecuted by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = LandGreenContainer,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.TrackChanges,
                                    contentDescription = null,
                                    tint = LandGreenPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "$serviceName ট্র্যাকিং",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = LandGreenDark
                            )
                            Text(
                                text = "অনলাইন আবেদন ও দাখিলা স্টেটাস",
                                fontSize = 10.5.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = trackingNumberInput,
                    onValueChange = {
                        trackingNumberInput = it
                        if (searchExecuted) searchExecuted = false
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("আবেদন নম্বর / মোবাইল / এনআইডি", fontSize = 11.5.sp) },
                    placeholder = { Text("যেমন: EP-2026-8974 বা 017xxxxxxxx", fontSize = 11.sp) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    shape = RoundedCornerShape(12.dp),
                    textStyle = TextStyle(color = Color.Black, fontSize = 14.sp, fontWeight = FontWeight.SemiBold),
                    colors = appTextFieldColors()
                )

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = {
                        if (trackingNumberInput.trim().isEmpty()) {
                            Toast.makeText(context, "অনুগ্রহ করে আবেদন বা মোবাইল নম্বর দিন", Toast.LENGTH_SHORT).show()
                        } else {
                            searchExecuted = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = LandGreenPrimary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("স্টেটাস অনুসন্ধান করুন", fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
                }

                if (searchExecuted) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        color = Color(0xFFF0FDF4),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, LandGreenLight.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "আবেদন আইডি: ${trackingNumberInput.ifBlank { "EP-2026-98104" }}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = LandGreenDark
                                )
                                Surface(
                                    color = SuccessGreen,
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = "চলমান",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Steps
                            TrackingStepItem(stepNumber = "১", label = "আবেদন দাখিল ও সরকারি ফি পরিশোধ", isDone = true)
                            TrackingStepItem(stepNumber = "২", label = "সহকারী কমিশনার (ভূমি) প্রাথমিক নিরীক্ষা", isDone = true)
                            TrackingStepItem(stepNumber = "৩", label = "রেকর্ড রুম ও মৌজা ম্যাপ যাচাইকরণ", isDone = false, isCurrent = true)
                            TrackingStepItem(stepNumber = "৪", label = "সার্টিফাইড কপি / দাখিলা প্রস্তুত ও স্বাক্ষর", isDone = false)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("বন্ধ করুন", fontSize = 12.sp)
                    }

                    Button(
                        onClick = onOpenPortal,
                        modifier = Modifier.weight(1.3f),
                        colors = ButtonDefaults.buttonColors(containerColor = LandGreenDark),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Launch, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("সরকারি পোর্টালে যান", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun TrackingStepItem(
    stepNumber: String,
    label: String,
    isDone: Boolean,
    isCurrent: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = CircleShape,
            color = when {
                isDone -> SuccessGreen
                isCurrent -> GoldAccent
                else -> Color.LightGray.copy(alpha = 0.5f)
            },
            modifier = Modifier.size(18.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                if (isDone) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                } else {
                    Text(
                        text = stepNumber,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isCurrent) Color.White else TextMuted
                    )
                }
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            fontSize = 10.5.sp,
            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
            color = if (isCurrent) LandGreenDark else TextPrimary
        )
    }
}
