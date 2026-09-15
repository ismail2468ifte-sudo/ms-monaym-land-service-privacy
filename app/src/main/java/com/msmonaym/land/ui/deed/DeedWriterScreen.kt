package com.msmonaym.land.ui.deed

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.msmonaym.land.data.ai.AiCategory
import com.msmonaym.land.ui.ai.AiFloatingActionButton
import com.msmonaym.land.ui.ai.AiLandAssistantModal
import com.msmonaym.land.ui.ai.AiTopBarAction
import com.msmonaym.land.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeedWriterScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current

    // State
    var currentTab by remember { mutableIntStateOf(0) } // 0: Word Editor, 1: Form Wizard, 2: Rules & Fees
    var selectedCategory by remember { mutableStateOf(DeedCategory.SAF_KABALA) }
    var formData by remember { mutableStateOf(DeedFormData(deedCategory = DeedCategory.SAF_KABALA)) }
    var editableDeedText by remember { mutableStateOf(DeedTemplates.generateDeedText(formData)) }

    // Word Editor Controls
    var hasStampMargin by remember { mutableStateOf(false) }
    var fontSizeSp by remember { mutableIntStateOf(14) }
    var isBold by remember { mutableStateOf(false) }
    var isItalic by remember { mutableStateOf(false) }
    var isUnderline by remember { mutableStateOf(false) }
    var textAlign by remember { mutableStateOf(TextAlign.Start) }

    // Dialog States
    var showDraftsDialog by remember { mutableStateOf(false) }
    var showAiAssistant by remember { mutableStateOf(false) }
    var showClauseInsertDialog by remember { mutableStateOf(false) }
    var savedDrafts by remember { mutableStateOf(DeedExportHelper.getSavedDrafts(context)) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "বাংলা ডিজিটাল দলিল রাইটার",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = Color.White.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "Word Format",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "সাফ-কবলা, হেবা, বন্টন ও সকল দলিলের আদর্শ বয়ান",
                            fontSize = 10.5.sp,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    // Save Draft Action
                    IconButton(
                        onClick = {
                            DeedExportHelper.saveDraftLocally(
                                context,
                                "${selectedCategory.titleBangla} (${formData.mouzaName})",
                                editableDeedText
                            )
                            savedDrafts = DeedExportHelper.getSavedDrafts(context)
                        }
                    ) {
                        Icon(Icons.Default.Save, contentDescription = "Save Draft", tint = Color.White)
                    }

                    // Drafts Folder Action
                    IconButton(
                        onClick = {
                            savedDrafts = DeedExportHelper.getSavedDrafts(context)
                            showDraftsDialog = true
                        }
                    ) {
                        Icon(Icons.Default.FolderOpen, contentDescription = "Drafts", tint = Color.White)
                    }

                    // AI Assistant Action
                    AiTopBarAction { showAiAssistant = true }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = LandGreenDark)
            )
        },
        floatingActionButton = {
            AiFloatingActionButton(AiCategory.REGISTRATION) {
                showAiAssistant = true
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF1F5F9))
        ) {
            // 1. Deed Category Selector (Horizontal Scroll Chips)
            DeedCategorySelectorRow(
                selectedCategory = selectedCategory,
                onSelectCategory = { newCategory ->
                    selectedCategory = newCategory
                    formData = formData.copy(deedCategory = newCategory)
                    editableDeedText = DeedTemplates.generateDeedText(formData)
                }
            )

            // 2. Tab Navigation Row (Word Editor / Form Wizard / Rules)
            TabRow(
                selectedTabIndex = currentTab,
                containerColor = Color.White,
                contentColor = LandGreenPrimary
            ) {
                Tab(
                    selected = currentTab == 0,
                    onClick = { currentTab = 0 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(5.dp))
                            Text("ওয়ার্ড এডিটর", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                )
                Tab(
                    selected = currentTab == 1,
                    onClick = { currentTab = 1 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AutoFixHigh, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(5.dp))
                            Text("অটো-ফিল ফর্ম", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                )
                Tab(
                    selected = currentTab == 2,
                    onClick = { currentTab = 2 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(5.dp))
                            Text("স্ট্যাম্প ও নিয়ম", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                )
            }

            // 3. Tab Body
            Box(modifier = Modifier.weight(1f)) {
                when (currentTab) {
                    0 -> WordDocumentEditorTab(
                        deedTitle = selectedCategory.titleBangla,
                        deedText = editableDeedText,
                        onTextChange = { editableDeedText = it },
                        hasStampMargin = hasStampMargin,
                        onToggleStampMargin = { hasStampMargin = it },
                        fontSizeSp = fontSizeSp,
                        onFontSizeChange = { fontSizeSp = it },
                        isBold = isBold,
                        onToggleBold = { isBold = !isBold },
                        isItalic = isItalic,
                        onToggleItalic = { isItalic = !isItalic },
                        isUnderline = isUnderline,
                        onToggleUnderline = { isUnderline = !isUnderline },
                        textAlign = textAlign,
                        onTextAlignChange = { textAlign = it },
                        onInsertClauseClick = { showClauseInsertDialog = true },
                        onResetFromForm = {
                            editableDeedText = DeedTemplates.generateDeedText(formData)
                        },
                        onShareWordDoc = {
                            DeedExportHelper.shareAsWordDocument(
                                context,
                                selectedCategory.titleBangla,
                                editableDeedText,
                                hasStampMargin
                            )
                        },
                        onPrint = {
                            DeedExportHelper.printDeed(
                                context,
                                selectedCategory.titleBangla,
                                editableDeedText,
                                hasStampMargin
                            )
                        },
                        onCopy = {
                            DeedExportHelper.copyToClipboard(
                                context,
                                editableDeedText,
                                selectedCategory.titleBangla
                            )
                        }
                    )

                    1 -> DeedFormWizardTab(
                        formData = formData,
                        onFormChange = { updated ->
                            formData = updated
                        },
                        onGenerateDeed = {
                            editableDeedText = DeedTemplates.generateDeedText(formData)
                            currentTab = 0 // Switch to Word Editor
                        }
                    )

                    2 -> DeedRulesAndStampFeesTab(
                        selectedCategory = selectedCategory,
                        onOpenAiConsultant = { showAiAssistant = true }
                    )
                }
            }
        }
    }

    // Insert Legal Clause Dialog
    if (showClauseInsertDialog) {
        InsertClauseDialog(
            onDismiss = { showClauseInsertDialog = false },
            onInsertClause = { clause ->
                editableDeedText = editableDeedText + "\n\n" + clause
                showClauseInsertDialog = false
            }
        )
    }

    // Saved Drafts Dialog
    if (showDraftsDialog) {
        SavedDraftsDialog(
            drafts = savedDrafts,
            onDismiss = { showDraftsDialog = false },
            onSelectDraft = { draft ->
                editableDeedText = draft.textContent
                showDraftsDialog = false
            },
            onDeleteDraft = { draftId ->
                DeedExportHelper.deleteDraft(context, draftId)
                savedDrafts = DeedExportHelper.getSavedDrafts(context)
            }
        )
    }

    // AI Land Assistant Modal
    if (showAiAssistant) {
        AiLandAssistantModal(
            initialCategory = AiCategory.REGISTRATION,
            onDismiss = { showAiAssistant = false }
        )
    }
}

/**
 * Horizontal Deed Category Selector
 */
@Composable
private fun DeedCategorySelectorRow(
    selectedCategory: DeedCategory,
    onSelectCategory: (DeedCategory) -> Unit
) {
    Surface(
        color = Color.White,
        border = BorderStroke(0.5.dp, BorderColor),
        modifier = Modifier.fillMaxWidth()
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(DeedCategory.values()) { category ->
                val isSelected = category == selectedCategory
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isSelected) LandGreenPrimary else SurfaceLight,
                    border = BorderStroke(
                        width = 1.dp,
                        color = if (isSelected) LandGreenDark else BorderColor
                    ),
                    modifier = Modifier.clickable { onSelectCategory(category) }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = category.emoji, fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = category.shortCode,
                            fontSize = 11.5.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else TextPrimary
                        )
                    }
                }
            }
        }
    }
}

/**
 * Tab 1: Word Document Editor Tab
 * Styled like Microsoft Word / Google Docs on an A4 Canvas.
 */
@Composable
private fun WordDocumentEditorTab(
    deedTitle: String,
    deedText: String,
    onTextChange: (String) -> Unit,
    hasStampMargin: Boolean,
    onToggleStampMargin: (Boolean) -> Unit,
    fontSizeSp: Int,
    onFontSizeChange: (Int) -> Unit,
    isBold: Boolean,
    onToggleBold: () -> Unit,
    isItalic: Boolean,
    onToggleItalic: () -> Unit,
    isUnderline: Boolean,
    onToggleUnderline: () -> Unit,
    textAlign: TextAlign,
    onTextAlignChange: (TextAlign) -> Unit,
    onInsertClauseClick: () -> Unit,
    onResetFromForm: () -> Unit,
    onShareWordDoc: () -> Unit,
    onPrint: () -> Unit,
    onCopy: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // 1. Word Formatting Toolbar
        Surface(
            color = Color(0xFFF8FAFC),
            border = BorderStroke(0.8.dp, BorderColor),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Font size controls
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color.White,
                            border = BorderStroke(0.8.dp, BorderColor),
                            modifier = Modifier.clickable {
                                if (fontSizeSp > 11) onFontSizeChange(fontSizeSp - 1)
                            }
                        ) {
                            Text(
                                text = " A- ",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(4.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${fontSizeSp}sp",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color.White,
                            border = BorderStroke(0.8.dp, BorderColor),
                            modifier = Modifier.clickable {
                                if (fontSizeSp < 22) onFontSizeChange(fontSizeSp + 1)
                            }
                        ) {
                            Text(
                                text = " A+ ",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(4.dp)
                            )
                        }
                    }

                    // Bold, Italic, Underline
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        FormatToggleButton(
                            label = "B",
                            isActive = isBold,
                            onClick = onToggleBold,
                            fontWeight = FontWeight.Black
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        FormatToggleButton(
                            label = "I",
                            isActive = isItalic,
                            onClick = onToggleItalic,
                            fontStyle = FontStyle.Italic
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        FormatToggleButton(
                            label = "U",
                            isActive = isUnderline,
                            onClick = onToggleUnderline,
                            textDecoration = TextDecoration.Underline
                        )
                    }

                    // Stamp margin toggle
                    FilterChip(
                        selected = hasStampMargin,
                        onClick = { onToggleStampMargin(!hasStampMargin) },
                        label = {
                            Text(
                                text = if (hasStampMargin) "৩.৫″ স্ট্যাম্প মার্জিন" else "স্ট্যাম্প স্পেস",
                                fontSize = 10.5.sp,
                                fontWeight = if (hasStampMargin) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.VerticalAlignTop,
                                contentDescription = null,
                                modifier = Modifier.size(13.dp)
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = GoldAccent,
                            selectedLabelColor = Color.White,
                            selectedLeadingIconColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Insert Clause & Actions Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    AssistChip(
                        onClick = onInsertClauseClick,
                        label = { Text("আইনি ক্লজ যুক্ত করুন", fontSize = 10.5.sp, fontWeight = FontWeight.Medium) },
                        leadingIcon = {
                            Icon(Icons.Default.AddCircleOutline, contentDescription = null, modifier = Modifier.size(14.dp), tint = LandGreenPrimary)
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = AssistChipDefaults.assistChipColors(containerColor = Color.White)
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        TextButton(
                            onClick = onResetFromForm,
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("ফর্ম থেকে রিফ্রেশ", fontSize = 10.5.sp)
                        }
                    }
                }
            }
        }

        // 2. A4 Word Paper Canvas (Scrollable Document Page)
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            item {
                // A4 Document Sheet Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, shape = RoundedCornerShape(4.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(4.dp),
                    border = BorderStroke(1.dp, Color(0xFFCBD5E1))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp, vertical = 20.dp)
                    ) {
                        // Stamp space placeholder if enabled
                        if (hasStampMargin) {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(130.dp)
                                    .padding(bottom = 16.dp),
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFFF8FAFC),
                                border = BorderStroke(1.dp, Color(0xFF94A3B8))
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.padding(12.dp)
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(
                                            Icons.Default.ConfirmationNumber,
                                            contentDescription = null,
                                            tint = LandGreenPrimary,
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "৩.৫ ইঞ্চি ফাঁকা স্থান (নন-জুডিশিয়াল স্ট্যাম্প সংযুক্ত করার জন্য নির্ধারিত)",
                                            fontSize = 10.sp,
                                            color = Color(0xFF64748B),
                                            textAlign = TextAlign.Center
                                        )
                                        Text(
                                            text = "[১০০ / ৩০০ / ১০০০ টাকার নন-জুডিশিয়াল স্ট্যাম্পে প্রিন্টের উপযোগি]",
                                            fontSize = 9.sp,
                                            color = Color(0xFF94A3B8),
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }

                        // Document Top Page Marker
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "পৃষ্ঠা নং - ০১",
                                fontSize = 9.5.sp,
                                color = TextMuted
                            )
                            Text(
                                text = "বাংলাদেশ ফরম নং ১৬০১ (দলিল বয়ান)",
                                fontSize = 9.5.sp,
                                color = TextMuted
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Live Editable Document Area
                        OutlinedTextField(
                            value = deedText,
                            onValueChange = onTextChange,
                            modifier = Modifier.fillMaxWidth(),
                            textStyle = TextStyle(
                                fontSize = fontSizeSp.sp,
                                fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
                                fontStyle = if (isItalic) FontStyle.Italic else FontStyle.Normal,
                                textDecoration = if (isUnderline) TextDecoration.Underline else TextDecoration.None,
                                textAlign = textAlign,
                                lineHeight = (fontSizeSp * 1.6).sp,
                                color = Color.Black
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black,
                                cursorColor = Color.Black,
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White
                            ),
                            placeholder = {
                                Text("এখানে দলিলের সম্পূর্ণ বয়ান টাইপ বা এডিট করুন...")
                            }
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Bottom Paper Watermark & Certification
                        Divider(color = Color(0xFFE2E8F0))
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "ডিজিটাল দলিল ড্রাফটার",
                                fontSize = 8.5.sp,
                                color = Color(0xFF94A3B8)
                            )
                            Text(
                                text = "A4 সাইজ ও ওয়ার্ড ফরম্যাট কমপ্লায়েন্ট",
                                fontSize = 8.5.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }
                }
            }
        }

        // 3. Word Export & Action Bottom Bar
        Surface(
            color = Color.White,
            shadowElevation = 8.dp,
            border = BorderStroke(0.8.dp, BorderColor),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Word Document (.doc) Export Button
                Button(
                    onClick = onShareWordDoc,
                    modifier = Modifier
                        .weight(1.3f)
                        .height(42.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)), // Word Blue
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 6.dp)
                ) {
                    Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Word (.doc) ফাইল", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                }

                // Print / PDF Button
                Button(
                    onClick = onPrint,
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = LandGreenPrimary),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 6.dp)
                ) {
                    Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("প্রিন্ট / PDF", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                }

                // Copy Button
                OutlinedButton(
                    onClick = onCopy,
                    modifier = Modifier
                        .weight(0.9f)
                        .height(42.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                    border = BorderStroke(1.dp, BorderColor),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 6.dp)
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(3.dp))
                    Text("কপি", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

/**
 * Format Toggle Button (B, I, U)
 */
@Composable
private fun FormatToggleButton(
    label: String,
    isActive: Boolean,
    onClick: () -> Unit,
    fontWeight: FontWeight = FontWeight.Normal,
    fontStyle: FontStyle = FontStyle.Normal,
    textDecoration: TextDecoration = TextDecoration.None
) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = if (isActive) LandGreenPrimary else Color.White,
        border = BorderStroke(0.8.dp, if (isActive) LandGreenDark else BorderColor),
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(30.dp)
        ) {
            Text(
                text = label,
                fontSize = 13.sp,
                fontWeight = fontWeight,
                fontStyle = fontStyle,
                textDecoration = textDecoration,
                color = if (isActive) Color.White else TextPrimary
            )
        }
    }
}

/**
 * Reusable Deed Form Input Field with guaranteed solid black text color.
 */
@Composable
private fun DeedFormField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    singleLine: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = placeholder?.let { { Text(it) } },
        modifier = modifier,
        singleLine = singleLine,
        keyboardOptions = keyboardOptions,
        textStyle = TextStyle(
            color = Color.Black,
            fontSize = 13.5.sp,
            fontWeight = FontWeight.Medium
        ),
        colors = appTextFieldColors()
    )
}

/**
 * Tab 2: Smart Deed Form Wizard Tab
 */
@Composable
private fun DeedFormWizardTab(
    formData: DeedFormData,
    onFormChange: (DeedFormData) -> Unit,
    onGenerateDeed: () -> Unit
) {
    val isFlatDeed = formData.deedCategory == DeedCategory.FLAT_BAYNAMA || formData.deedCategory == DeedCategory.FLAT_SAF_KABALA

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            // Header Info Banner
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = LandGreenContainer,
                border = BorderStroke(1.dp, LandGreenPrimary.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "✨", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "স্মার্ট অটো-ফিল দলিল উইজার্ড: ${formData.deedCategory.titleBangla}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = LandGreenDark
                            )
                            Text(
                                text = "নিচের তথ্যগুলো লিখুন (সকল লেখার রং স্পষ্ট কালো)। বাটনে চাপ দিলে স্বয়ংক্রিয়ভাবে পূর্ণাঙ্গ দলিলের বয়ান প্রস্তুত হবে।",
                                fontSize = 10.5.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Quick Deed Form Switcher
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (formData.deedCategory == DeedCategory.FLAT_BAYNAMA) LandGreenPrimary else Color.White,
                            border = BorderStroke(1.dp, if (formData.deedCategory == DeedCategory.FLAT_BAYNAMA) LandGreenDark else BorderColor),
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    onFormChange(formData.copy(deedCategory = DeedCategory.FLAT_BAYNAMA))
                                }
                        ) {
                            Text(
                                text = "🏢 ফ্লাট বায়না পত্র",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (formData.deedCategory == DeedCategory.FLAT_BAYNAMA) Color.White else TextPrimary,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 7.dp, horizontal = 4.dp)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (formData.deedCategory == DeedCategory.FLAT_SAF_KABALA) LandGreenPrimary else Color.White,
                            border = BorderStroke(1.dp, if (formData.deedCategory == DeedCategory.FLAT_SAF_KABALA) LandGreenDark else BorderColor),
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    onFormChange(formData.copy(deedCategory = DeedCategory.FLAT_SAF_KABALA))
                                }
                        ) {
                            Text(
                                text = "🏙️ ফ্লাট সাব-কাওলা",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (formData.deedCategory == DeedCategory.FLAT_SAF_KABALA) Color.White else TextPrimary,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 7.dp, horizontal = 4.dp)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (formData.deedCategory == DeedCategory.SAF_KABALA) LandGreenPrimary else Color.White,
                            border = BorderStroke(1.dp, if (formData.deedCategory == DeedCategory.SAF_KABALA) LandGreenDark else BorderColor),
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    onFormChange(formData.copy(deedCategory = DeedCategory.SAF_KABALA))
                                }
                        ) {
                            Text(
                                text = "📜 জমি সাফ-কবলা",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (formData.deedCategory == DeedCategory.SAF_KABALA) Color.White else TextPrimary,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 7.dp, horizontal = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        // Section 1: সাব-রেজিস্ট্রি অফিস ও এলাকা
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(0.8.dp, BorderColor)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    FormSectionHeader(icon = Icons.Default.AccountBalance, title = "১. সাব-রেজিস্ট্রি অফিস ও এলাকা")
                    Spacer(modifier = Modifier.height(10.dp))

                    DeedFormField(
                        value = formData.subRegistryOffice,
                        onValueChange = { onFormChange(formData.copy(subRegistryOffice = it)) },
                        label = "সাব-রেজিস্ট্রি অফিসের নাম",
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        DeedFormField(
                            value = formData.upazilaOrThana,
                            onValueChange = { onFormChange(formData.copy(upazilaOrThana = it)) },
                            label = "থানা / উপজেলা",
                            modifier = Modifier.weight(1f)
                        )
                        DeedFormField(
                            value = formData.district,
                            onValueChange = { onFormChange(formData.copy(district = it)) },
                            label = "জেলা",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Section 2: ১ম পক্ষ (দলিল দাতা / বিক্রেতা / ডেভেলপার)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(0.8.dp, BorderColor)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    FormSectionHeader(
                        icon = Icons.Default.Person,
                        title = if (isFlatDeed) "২. ১ম পক্ষ (ফ্ল্যাট বিক্রেতা / জমির মালিক / ডেভেলপার)" else "২. ১ম পক্ষ (দলিল দাতা / বিক্রেতা তথ্য)"
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    DeedFormField(
                        value = formData.firstPartyName,
                        onValueChange = { onFormChange(formData.copy(firstPartyName = it)) },
                        label = if (isFlatDeed) "বিক্রেতা / ডেভেলপার বা মালিকের নাম" else "দাতার পূর্ণ নাম",
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        DeedFormField(
                            value = formData.firstPartyFatherOrHusband,
                            onValueChange = { onFormChange(formData.copy(firstPartyFatherOrHusband = it)) },
                            label = "পিতা / স্বামীর নাম",
                            modifier = Modifier.weight(1f)
                        )
                        DeedFormField(
                            value = formData.firstPartyMother,
                            onValueChange = { onFormChange(formData.copy(firstPartyMother = it)) },
                            label = "মাতার নাম",
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        DeedFormField(
                            value = formData.firstPartyNid,
                            onValueChange = { onFormChange(formData.copy(firstPartyNid = it)) },
                            label = "NID নম্বর",
                            modifier = Modifier.weight(1.2f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        DeedFormField(
                            value = formData.firstPartyMobile,
                            onValueChange = { onFormChange(formData.copy(firstPartyMobile = it)) },
                            label = "মোবাইল",
                            modifier = Modifier.weight(0.8f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    DeedFormField(
                        value = formData.firstPartyAddress,
                        onValueChange = { onFormChange(formData.copy(firstPartyAddress = it)) },
                        label = "বর্তমান ও স্থায়ী ঠিকানা",
                        singleLine = false,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Section 3: ২য় পক্ষ (দলিল গ্রহীতা / ফ্ল্যাট ক্রেতা)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(0.8.dp, BorderColor)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    FormSectionHeader(
                        icon = Icons.Default.Group,
                        title = if (isFlatDeed) "৩. ২য় পক্ষ (ফ্ল্যাট ক্রেতা / বরাদ্দগ্রহীতা)" else "৩. ২য় পক্ষ (দলিল গ্রহীতা / ক্রেতা তথ্য)"
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    DeedFormField(
                        value = formData.secondPartyName,
                        onValueChange = { onFormChange(formData.copy(secondPartyName = it)) },
                        label = "গ্রহীতা / ক্রেতার পূর্ণ নাম",
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        DeedFormField(
                            value = formData.secondPartyFatherOrHusband,
                            onValueChange = { onFormChange(formData.copy(secondPartyFatherOrHusband = it)) },
                            label = "পিতা / স্বামীর নাম",
                            modifier = Modifier.weight(1f)
                        )
                        DeedFormField(
                            value = formData.secondPartyMother,
                            onValueChange = { onFormChange(formData.copy(secondPartyMother = it)) },
                            label = "মাতার নাম",
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        DeedFormField(
                            value = formData.secondPartyNid,
                            onValueChange = { onFormChange(formData.copy(secondPartyNid = it)) },
                            label = "NID নম্বর",
                            modifier = Modifier.weight(1.2f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        DeedFormField(
                            value = formData.secondPartyMobile,
                            onValueChange = { onFormChange(formData.copy(secondPartyMobile = it)) },
                            label = "মোবাইল",
                            modifier = Modifier.weight(0.8f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    DeedFormField(
                        value = formData.secondPartyAddress,
                        onValueChange = { onFormChange(formData.copy(secondPartyAddress = it)) },
                        label = "বর্তমান ও স্থায়ী ঠিকানা",
                        singleLine = false,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Section 4: আর্থিক তথ্য / দলিল মূল্য ও বায়না
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(0.8.dp, BorderColor)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    FormSectionHeader(icon = Icons.Default.Payments, title = "৪. দলিল মূল্য ও আর্থিক পরিশোধ")
                    Spacer(modifier = Modifier.height(10.dp))

                    DeedFormField(
                        value = formData.deedValueTaka,
                        onValueChange = { onFormChange(formData.copy(deedValueTaka = it)) },
                        label = if (isFlatDeed) "ফ্ল্যাটের মোট বিক্রয় মূল্য / পণমূল্য (টাকা)" else "সর্বমোট বিক্রয় মূল্য / পণমূল্য (টাকা)",
                        placeholder = "যেমন: ১২,৫০,০০০/-",
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    DeedFormField(
                        value = formData.deedValueInWords,
                        onValueChange = { onFormChange(formData.copy(deedValueInWords = it)) },
                        label = "মূল্য কথায়",
                        placeholder = "যেমন: বারো লক্ষ পঞ্চাশ হাজার টাকা মাত্র",
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (formData.deedCategory == DeedCategory.BAYNAMA || formData.deedCategory == DeedCategory.FLAT_BAYNAMA) {
                        Spacer(modifier = Modifier.height(8.dp))
                        DeedFormField(
                            value = formData.advancePaidTaka,
                            onValueChange = { onFormChange(formData.copy(advancePaidTaka = it)) },
                            label = "বায়না বাবদ নগদ/অগ্রিম গৃহীত টাকা",
                            placeholder = "যেমন: ২,০০,০০০/-",
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        DeedFormField(
                            value = formData.installmentPaymentDetails,
                            onValueChange = { onFormChange(formData.copy(installmentPaymentDetails = it)) },
                            label = "বায়না পরিশোধের মাধ্যম (পে-অর্ডার/ব্যাংক/নগদ)",
                            placeholder = "যেমন: পে-অর্ডার নং- 4829105, ব্যাংক এশিয়া",
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        // Section: ফ্ল্যাট ও অ্যাপার্টমেন্ট অটো-ফিল বিবরণ (বাংলাদেশের বর্তমান আইন অনুযায়ী)
        if (isFlatDeed) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.2.dp, LandGreenPrimary)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Apartment, contentDescription = null, tint = LandGreenPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "🏢 ফ্ল্যাট ও অ্যাপার্টমেন্ট অটো-ফিল তথ্য (আইনসম্মত বিবরণ)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.5.sp,
                                color = LandGreenDark
                            )
                        }
                        Text(
                            text = "রিয়েল এস্টেট উন্নয়ন ও ব্যবস্থাপনা আইন ২০১০ মোতাবেক ভবনের অনুমোদিত প্ল্যান, শেয়ার ও বিবরণ",
                            fontSize = 10.5.sp,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        DeedFormField(
                            value = formData.buildingProjectName,
                            onValueChange = { onFormChange(formData.copy(buildingProjectName = it)) },
                            label = "ভবন বা প্রজেক্টের নাম / ডেভেলপার প্রতিষ্ঠান",
                            placeholder = "যেমন: গ্রীন ভ্যালি হাইটস / এবিসি ডেভেলপার্স",
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            DeedFormField(
                                value = formData.flatNumberAndFloor,
                                onValueChange = { onFormChange(formData.copy(flatNumberAndFloor = it)) },
                                label = "ফ্ল্যাট নম্বর ও তলা",
                                placeholder = "যেমন: ফ্ল্যাট ৫/বি (৬ষ্ঠ তলা)",
                                modifier = Modifier.weight(1f)
                            )
                            DeedFormField(
                                value = formData.flatSizeSqFt,
                                onValueChange = { onFormChange(formData.copy(flatSizeSqFt = it)) },
                                label = "ফ্ল্যাটের সাইজ (বর্গফুট)",
                                placeholder = "যেমন: ১৩৫০ বর্গফুট",
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))

                        DeedFormField(
                            value = formData.carParkingSpace,
                            onValueChange = { onFormChange(formData.copy(carParkingSpace = it)) },
                            label = "কার পার্কিং স্পেস বিবরণ",
                            placeholder = "যেমন: নিচতলায় ১টি নির্দিষ্ট কার পার্কিং (স্পেস নং- CP-05)",
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        DeedFormField(
                            value = formData.undividedLandShare,
                            onValueChange = { onFormChange(formData.copy(undividedLandShare = it)) },
                            label = "অবিভক্ত ও অচিহ্নিত জমির শেয়ার (Undivided Share)",
                            placeholder = "যেমন: ০.৮৫ অযুতাংশ / ডেসিমেল জমি শেয়ার",
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        DeedFormField(
                            value = formData.approvalPlanMemoAndDate,
                            onValueChange = { onFormChange(formData.copy(approvalPlanMemoAndDate = it)) },
                            label = "রাজউক/সিডিএ/পৌরসভা অনুমোদিত প্ল্যান স্মারক ও তারিখ",
                            placeholder = "যেমন: রাজউক স্মারক নং- ২৫.৩৯... তারিখ: ১২/০২/২০২২",
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        if (formData.deedCategory == DeedCategory.FLAT_BAYNAMA) {
                            DeedFormField(
                                value = formData.flatHandoverDate,
                                onValueChange = { onFormChange(formData.copy(flatHandoverDate = it)) },
                                label = "ফ্ল্যাট হস্তান্তর ও বাকি টাকা পরিশোধের শেষ মেয়াদ",
                                placeholder = "যেমন: আগামী ৩১শে ডিসেম্বর ২০২৬ খ্রিষ্টাব্দ",
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        DeedFormField(
                            value = formData.utilityDetails,
                            onValueChange = { onFormChange(formData.copy(utilityDetails = it)) },
                            label = "ইউটিলিটি ও কমন সুবিধাদি (বিদ্যুৎ, লিফট, জেনারেটর)",
                            placeholder = "যেমন: বিদ্যুৎ মিটার, সাব-মার্সিবল পাম্প ও লিফট ব্যবহারের যৌথ সুবিধা",
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        // Section 5: হস্তান্তরিত সম্পত্তির মূল তফসিল
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(0.8.dp, BorderColor)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    FormSectionHeader(
                        icon = Icons.Default.Map,
                        title = if (isFlatDeed) "৫. ফ্ল্যাট নির্মিত ভূমির মূল তফসিল" else "৫. হস্তান্তরিত সম্পত্তির পূর্ণ তফসিল"
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        DeedFormField(
                            value = formData.mouzaName,
                            onValueChange = { onFormChange(formData.copy(mouzaName = it)) },
                            label = "মৌজার নাম",
                            modifier = Modifier.weight(1.3f)
                        )
                        DeedFormField(
                            value = formData.jlNumber,
                            onValueChange = { onFormChange(formData.copy(jlNumber = it)) },
                            label = "জে.এল. নং",
                            modifier = Modifier.weight(0.7f)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    DeedFormField(
                        value = formData.khatianTypes,
                        onValueChange = { onFormChange(formData.copy(khatianTypes = it)) },
                        label = "খতিয়ান নম্বরসমূহ (CS/SA/RS/BS)",
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    DeedFormField(
                        value = formData.dagNumbers,
                        onValueChange = { onFormChange(formData.copy(dagNumbers = it)) },
                        label = "দাগ নম্বর (সাবেক ও হাল)",
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        DeedFormField(
                            value = formData.landClass,
                            onValueChange = { onFormChange(formData.copy(landClass = it)) },
                            label = "জমির শ্রেণি",
                            modifier = Modifier.weight(1f)
                        )
                        DeedFormField(
                            value = formData.transferredLandAmount,
                            onValueChange = { onFormChange(formData.copy(transferredLandAmount = it)) },
                            label = if (isFlatDeed) "বিক্রিত ফ্ল্যাট/জমির শেয়ার" else "বিক্রিত পরিমাণ",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Section 6: জমির চারদিকের চৌহদ্দি
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(0.8.dp, BorderColor)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    FormSectionHeader(icon = Icons.Default.Explore, title = "৬. জমির চারদিকের চৌহদ্দি")
                    Spacer(modifier = Modifier.height(10.dp))

                    DeedFormField(
                        value = formData.chouhaddiNorth,
                        onValueChange = { onFormChange(formData.copy(chouhaddiNorth = it)) },
                        label = "উত্তরে সীমানা",
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    DeedFormField(
                        value = formData.chouhaddiSouth,
                        onValueChange = { onFormChange(formData.copy(chouhaddiSouth = it)) },
                        label = "দক্ষিণে সীমানা",
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    DeedFormField(
                        value = formData.chouhaddiEast,
                        onValueChange = { onFormChange(formData.copy(chouhaddiEast = it)) },
                        label = "পূর্বে সীমানা",
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    DeedFormField(
                        value = formData.chouhaddiWest,
                        onValueChange = { onFormChange(formData.copy(chouhaddiWest = it)) },
                        label = "পশ্চিমে সীমানা",
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Section 7: শনাক্তকারী ও লেখক
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(0.8.dp, BorderColor)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    FormSectionHeader(icon = Icons.Default.Badge, title = "৭. শনাক্তকারী ও দলিল লেখক")
                    Spacer(modifier = Modifier.height(10.dp))

                    DeedFormField(
                        value = formData.identifierName,
                        onValueChange = { onFormChange(formData.copy(identifierName = it)) },
                        label = "শনাক্তকারীর নাম ও পরিচিতি",
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    DeedFormField(
                        value = formData.deedWriterInfo,
                        onValueChange = { onFormChange(formData.copy(deedWriterInfo = it)) },
                        label = "দলিল প্রস্তুতকারকের নাম ও লাইসেন্স",
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Big Generate Button
        item {
            Button(
                onClick = onGenerateDeed,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = LandGreenPrimary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "দলিল প্রস্তুত করুন ও ওয়ার্ডে ওপেন করুন",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun FormSectionHeader(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = LandGreenPrimary, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = title, fontWeight = FontWeight.Bold, fontSize = 13.5.sp, color = LandGreenDark)
    }
}

/**
 * Tab 3: Deed Rules & Stamp Fees Guide
 */
@Composable
private fun DeedRulesAndStampFeesTab(
    selectedCategory: DeedCategory,
    onOpenAiConsultant: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            // Official Registry Rules Banner
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = EmeraldTint,
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(selectedCategory.emoji, fontSize = 18.sp)
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = selectedCategory.titleBangla,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = LandGreenDark
                            )
                            Text(
                                text = selectedCategory.subtitleBangla,
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Divider(color = BorderColor)
                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "স্ট্যাম্প শুল্ক কাঠামো: ${selectedCategory.standardStampFee}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = LandGreenPrimary
                    )
                }
            }
        }

        // Section: প্রয়োজনীয় কাগজপত্র চেকলিস্ট
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(0.8.dp, BorderColor)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "📋 সাব-রেজিস্ট্রির জন্য আবশ্যকীয় কাগজপত্র:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = LandGreenDark
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    DeedRequirementItem("১. হাল সনের ভূমি উন্নয়ন কর (খাজনা) দাখিলা রশিদ")
                    DeedRequirementItem("২. সিএস, এসএ, আরএস ও বিএস খতিয়ানের মূল কপি বা সার্টিফাইড নকল")
                    DeedRequirementItem("৩. নামজারি খতিয়ান (মিউটেশন পর্চা) ও প্রস্তাবিত ডিসিআর")
                    DeedRequirementItem("৪. ওয়ারিশি সম্পত্তির ক্ষেত্রে চেয়ারম্যান কর্তৃক বৈধ ওয়ারিশান সনদপত্র")
                    DeedRequirementItem("৫. দাতা ও গ্রহীতার মূল জাতীয় পরিচয়পত্র (NID) ও পাসপোর্ট সাইজ ছবি")
                    DeedRequirementItem("৬. বায়া দলিল (পূর্ববর্তী ক্রয়/হস্তান্তর দলিলের সার্টিফাইড কপি)")
                }
            }
        }

        // Section: ভূমি অপরাধ প্রতিরোধ আইন ২০২৩ এর সতর্কতা
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
                border = BorderStroke(1.dp, Color(0xFFFCA5A5))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Gavel, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "ভূমি অপরাধ প্রতিরোধ আইন ২০২৩ এর সতর্কতা",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color(0xFF991B1B)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "• মালিকানা ব্যতীত অন্যের জমি বিক্রয় বা দলিল সম্পাদনে ৭ বছর পর্যন্ত কারাদণ্ড হতে পারে।\n• জমির দাগ, খতিয়ান বা চৌহদ্দি জালিয়াতি ফৌজদারি অপরাধ।\n• একই জমি একাধিক ব্যক্তির কাছে বায়না বা হস্তান্তর সম্পূর্ণ শাস্তিযোগ্য।",
                        fontSize = 11.5.sp,
                        color = Color(0xFF7F1D1D),
                        lineHeight = 17.sp
                    )
                }
            }
        }

        // Section: AI Assistance CTA Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenAiConsultant() },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = LandGreenDark)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(26.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "দলিল আইন ও শর্ত যাচাই এআই সহকারী",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color.White
                        )
                        Text(
                            text = "আপনার দলিলের কোনো বিশেষ শর্ত যোগ বা আইনি পরামর্শ জানতে ক্লিক করুন।",
                            fontSize = 10.5.sp,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DeedRequirementItem(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            Icons.Default.CheckCircle,
            contentDescription = null,
            tint = SuccessGreen,
            modifier = Modifier
                .size(14.dp)
                .padding(top = 2.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text, fontSize = 11.5.sp, color = TextPrimary, lineHeight = 16.sp)
    }
}

/**
 * Insert Legal Clause Dialog
 */
@Composable
private fun InsertClauseDialog(
    onDismiss: () -> Unit,
    onInsertClause: (String) -> Unit
) {
    val sampleClauses = remember {
        listOf(
            "রাস্তা ব্যবহারের চিরস্থায়ী অধিকার ক্লজ" to "তপশীলোক্ত সম্পত্তিতে যাতায়াতের জন্য সংলগ্ন দক্ষিণের ১২ ফুট প্রশস্ত পাকা রাস্তা ব্যবহারের অবাধ, চিরস্থায়ী ও নিরঙ্কুশ অধিকার অদ্য হইতে ২য় পক্ষ ও তাহার ওয়ারিশগণ ভোগ করিবেন। ইহাতে কেহ কোনো প্রকার প্রতিবন্ধকতা সৃষ্টি করিতে পারিবে না।",
            "দখল ও সীমানা নির্ধারণ ক্লজ" to "অদ্য হইতে তপশীলোক্ত সম্পত্তির সীমানা চিহ্নিত করিয়া প্রত্যক্ষ ভৌগোলিক দখল ২য় পক্ষকে বুঝাইয়া দেওয়া হইল। ভবিষ্যতে সীমানা লইয়া কোনো বিরোধ দেখা দিলে ১ম পক্ষ নিজ ব্যয়ে তাহা স্থানীয় সার্ভেয়ার দ্বারা সমাধান করিয়া দিবেন।",
            "বিদ্যুৎ ও গ্যাস সংযোগ স্বত্ব ক্লজ" to "তপশীলোক্ত সম্পত্তিতে বিদ্যমান অথবা ভবিষ্যৎ বিদ্যুৎ, গ্যাস, ওয়াসা ও অন্যান্য ইউটিলিটি সংযোগ গ্রহণ ও ব্যবহারের যাবতীয় আইনগত অধিকার ২য় পক্ষের বরাবরে ন্যস্ত করা হইল।",
            "পূর্ব দায়মুক্ত ও নিরঙ্কুশ হলফনামা ক্লজ" to "হলফপূর্বক ঘোষণা করিতেছি যে, তপশীলোক্ত সম্পত্তি কোনো আদালত কর্তৃক ক্রোক নহে, কোনো আর্থিক প্রতিষ্ঠানে দায়বদ্ধ নহে এবং ইহা সম্পূর্ণ ভেজালমুক্ত ও নির্ভেজাল।"
        )
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "আইনি বিশেষ শর্ত (Clause) ইনসার্ট",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = LandGreenDark
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = null, tint = TextMuted)
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))

                LazyColumn(
                    modifier = Modifier.heightIn(max = 350.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(sampleClauses) { (title, content) ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = SurfaceLight,
                            border = BorderStroke(0.8.dp, BorderColor),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onInsertClause(content) }
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(text = title, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = LandGreenPrimary)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = content, fontSize = 10.5.sp, color = TextSecondary, maxLines = 2, overflow = TextOverflow.Ellipsis)
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Saved Drafts Dialog
 */
@Composable
private fun SavedDraftsDialog(
    drafts: List<SavedDeedDraft>,
    onDismiss: () -> Unit,
    onSelectDraft: (SavedDeedDraft) -> Unit,
    onDeleteDraft: (String) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "সংরক্ষিত দলিল খসড়া (Saved Drafts)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = LandGreenDark
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = null, tint = TextMuted)
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))

                if (drafts.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "কোনো সংরক্ষিত খসড়া পাওয়া যায়নি।\nউপরে সেভ (Save) আইকনে ক্লিক করে খসড়া সংরক্ষণ করুন।",
                            fontSize = 11.5.sp,
                            color = TextSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.heightIn(max = 350.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(drafts) { draft ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = SurfaceLight,
                                border = BorderStroke(0.8.dp, BorderColor),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onSelectDraft(draft) }
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = draft.title,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.5.sp,
                                            color = LandGreenDark
                                        )
                                        Text(
                                            text = "দৈর্ঘ্য: ${draft.textContent.length} অক্ষর",
                                            fontSize = 10.sp,
                                            color = TextSecondary
                                        )
                                    }
                                    IconButton(
                                        onClick = { onDeleteDraft(draft.id) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red.copy(alpha = 0.7f), modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
