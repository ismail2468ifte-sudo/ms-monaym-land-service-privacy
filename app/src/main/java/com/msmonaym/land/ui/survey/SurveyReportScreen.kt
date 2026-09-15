package com.msmonaym.land.ui.survey

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.msmonaym.land.data.ai.AiCategory
import com.msmonaym.land.ui.ai.AiFloatingActionButton
import com.msmonaym.land.ui.ai.AiLandAssistantModal
import com.msmonaym.land.ui.ai.AiTopBarAction
import com.msmonaym.land.ui.theme.*
import java.io.File
import java.text.DecimalFormat

enum class SurveyToolboxTab(val title: String, val emoji: String, val subtitle: String) {
    EXCEL_GRID("Excel স্প্রেডশিট", "📊", "প্লট পরিমাপ ও ফর্মুলা"),
    WORD_BUILDER("Word রিপোর্ট", "📝", "অফিসিয়াল ডকুমেন্ট"),
    PREVIEW("লাইভ প্রিভিউ", "👁️", "A4 প্রিন্ট ভিউ"),
    EXPORT_PDF("PDF ডাউনলোড", "🖨️", "ডাউনলোড ও শেয়ার")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SurveyReportScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(SurveyToolboxTab.EXCEL_GRID) }
    var reportData by remember { mutableStateOf(SurveyReportData()) }
    var showAiAssistant by remember { mutableStateOf(false) }

    // PDF / Export State
    var generatedPdfFile by remember { mutableStateOf<File?>(null) }
    var isGenerating by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf("") }
    var successFile by remember { mutableStateOf<File?>(null) }
    var successFileType by remember { mutableStateOf("application/pdf") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "সার্ভে রিপোর্ট ও পরিমাপ টুলবক্স",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color.White
                        )
                        Text(
                            "Microsoft Word & Excel + PDF জেনারেটর",
                            fontSize = 10.sp,
                            color = GoldAccent
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
                actions = {
                    AiTopBarAction { showAiAssistant = true }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = LandGreenPrimary)
            )
        },
        floatingActionButton = {
            AiFloatingActionButton(AiCategory.CALCULATOR) {
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
            // Internal Toolbox Tabs (Excel, Word, Preview, PDF)
            TabRow(
                selectedTabIndex = selectedTab.ordinal,
                containerColor = LandGreenDark,
                contentColor = Color.White,
                divider = {}
            ) {
                SurveyToolboxTab.values().forEach { tab ->
                    Tab(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        text = {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(vertical = 4.dp)
                            ) {
                                Text(tab.emoji, fontSize = 16.sp)
                                Text(
                                    text = tab.title,
                                    fontSize = 11.sp,
                                    fontWeight = if (selectedTab == tab) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedTab == tab) GoldAccent else Color.White.copy(alpha = 0.8f)
                                )
                            }
                        }
                    )
                }
            }

            // Tab Content
            Box(modifier = Modifier.fillMaxSize()) {
                when (selectedTab) {
                    SurveyToolboxTab.EXCEL_GRID -> {
                        ExcelSpreadsheetTab(
                            data = reportData,
                            onDataChanged = { reportData = it },
                            onSwitchToWord = { selectedTab = SurveyToolboxTab.WORD_BUILDER },
                            onSwitchToPdf = {
                                isGenerating = true
                                val file = SurveyPdfGenerator.generateSurveyPdf(context, reportData)
                                isGenerating = false
                                if (file != null) {
                                    generatedPdfFile = file
                                    selectedTab = SurveyToolboxTab.EXPORT_PDF
                                } else {
                                    Toast.makeText(context, "PDF তৈরিতে সমস্যা হয়েছে", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                    }
                    SurveyToolboxTab.WORD_BUILDER -> {
                        WordDocumentBuilderTab(
                            data = reportData,
                            onDataChanged = { reportData = it },
                            onSwitchToPreview = { selectedTab = SurveyToolboxTab.PREVIEW },
                            onSwitchToPdf = {
                                isGenerating = true
                                val file = SurveyPdfGenerator.generateSurveyPdf(context, reportData)
                                isGenerating = false
                                if (file != null) {
                                    generatedPdfFile = file
                                    selectedTab = SurveyToolboxTab.EXPORT_PDF
                                } else {
                                    Toast.makeText(context, "PDF তৈরিতে সমস্যা হয়েছে", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                    }
                    SurveyToolboxTab.PREVIEW -> {
                        SurveyLivePreviewTab(
                            data = reportData,
                            onGeneratePdf = {
                                isGenerating = true
                                val file = SurveyPdfGenerator.generateSurveyPdf(context, reportData)
                                isGenerating = false
                                if (file != null) {
                                    generatedPdfFile = file
                                    selectedTab = SurveyToolboxTab.EXPORT_PDF
                                    Toast.makeText(context, "PDF তৈরি সম্পন্ন!", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "PDF তৈরিতে সমস্যা হয়েছে", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                    }
                    SurveyToolboxTab.EXPORT_PDF -> {
                        PdfExportAndPrintTab(
                            data = reportData,
                            pdfFile = generatedPdfFile,
                            isGenerating = isGenerating,
                            onGeneratePdf = {
                                isGenerating = true
                                val file = SurveyPdfGenerator.generateSurveyPdf(context, reportData)
                                isGenerating = false
                                if (file != null) {
                                    generatedPdfFile = file
                                    successFile = file
                                    successFileType = "application/pdf"
                                    successMessage = "অফিসিয়াল A4 সাইজ ল্যান্ড সার্ভে রিপোর্ট PDF সফলভাবে তৈরি ও ডাউনলোড হয়েছে।"
                                    showSuccessDialog = true
                                } else {
                                    Toast.makeText(context, "PDF তৈরিতে ব্যর্থ", Toast.LENGTH_SHORT).show()
                                }
                            },
                            onExportWord = {
                                val file = SurveyExportHelper.exportToWordDoc(context, reportData)
                                if (file != null) {
                                    successFile = file
                                    successFileType = "application/msword"
                                    successMessage = "Microsoft Word (.doc) ফরম্যাটে সার্ভে রিপোর্ট ফাইল প্রস্তুত হয়েছে।"
                                    showSuccessDialog = true
                                } else {
                                    Toast.makeText(context, "Word তৈরিতে ব্যর্থ", Toast.LENGTH_SHORT).show()
                                }
                            },
                            onExportExcel = {
                                val file = SurveyExportHelper.exportToExcelCsv(context, reportData)
                                if (file != null) {
                                    successFile = file
                                    successFileType = "text/csv"
                                    successMessage = "Microsoft Excel (.csv) স্প্রেডশিট ফাইল প্রস্তুত হয়েছে।"
                                    showSuccessDialog = true
                                } else {
                                    Toast.makeText(context, "Excel তৈরিতে ব্যর্থ", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    // Success & Open Dialog
    if (showSuccessDialog && successFile != null) {
        AlertDialog(
            onDismissRequest = { showSuccessDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("✅ ফাইল প্রস্তুত সম্পন্ন", fontWeight = FontWeight.Bold, color = LandGreenDark, fontSize = 16.sp)
                }
            },
            text = {
                Column {
                    Text(successMessage, fontSize = 13.sp, color = TextPrimary)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("ফাইল: ${successFile?.name}", fontSize = 11.sp, color = Color.Gray)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        successFile?.let { SurveyExportHelper.openFile(context, it, successFileType) }
                        showSuccessDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = LandGreenPrimary)
                ) {
                    Text("সরাসরি ওপেন করুন", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        successFile?.let { SurveyExportHelper.shareFile(context, it, successFileType, "Land Survey Report") }
                        showSuccessDialog = false
                    }
                ) {
                    Text("শেয়ার করুন")
                }
            }
        )
    }

    if (showAiAssistant) {
        AiLandAssistantModal(
            initialCategory = AiCategory.CALCULATOR,
            onDismiss = { showAiAssistant = false }
        )
    }
}

// ----------------------------------------------------------------------------
// 1. EXCEL SPREADSHEET TAB
// ----------------------------------------------------------------------------
@Composable
fun ExcelSpreadsheetTab(
    data: SurveyReportData,
    onDataChanged: (SurveyReportData) -> Unit,
    onSwitchToWord: () -> Unit,
    onSwitchToPdf: () -> Unit
) {
    val df = remember { DecimalFormat("#,##0.00") }
    var plotList by remember { mutableStateOf(data.plots) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Excel Header Ribbon
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F766E)), // Teal / Excel aesthetic
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("📊", fontSize = 28.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "অভ্যন্তরীণ এক্সেল স্প্রেডশিট গ্রিড",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color.White
                        )
                        Text(
                            "দৈর্ঘ্য-প্রস্থ লিখে সরাসরি শতাংশ, কাঠা, বিঘা ও একর হিসাব করুন।",
                            fontSize = 11.sp,
                            color = Color(0xFFCCFBF1)
                        )
                    }
                }
            }
        }

        // Live Total Summary Ribbon
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = LandGreenContainer),
                border = BorderStroke(1.dp, LandGreenPrimary.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "📐 সর্বমোট জমির পরিমাপ ফলাফল (Live Summary)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.5.sp,
                        color = LandGreenDark
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        SummaryPill("মোট শতাংশ", "${df.format(data.totalDecimal)} শতক", LandGreenPrimary)
                        SummaryPill("মোট কাঠা", "${df.format(data.totalKatha)} কাঠা", Color(0xFFB45309))
                        SummaryPill("মোট বিঘা", "${df.format(data.totalBigha)} বিঘা", Color(0xFF4338CA))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        SummaryPill("মোট বর্গফুট", "${df.format(data.totalSquareFeet)} sq.ft", Color(0xFF0369A1))
                        SummaryPill("মোট একর", "${df.format(data.totalAcre)} একর", Color(0xFF6D28D9))
                    }
                }
            }
        }

        // Plots Table Header & Add Button
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "প্লট / খণ্ডের তালিকা (${plotList.size}টি প্লট)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = LandGreenDark
                )
                Button(
                    onClick = {
                        val newPlots = plotList.toMutableList().apply {
                            add(SurveyPlotEntry(plotName = "প্লট-${size + 1}", northFt = 50.0, southFt = 50.0, eastFt = 40.0, westFt = 40.0))
                        }
                        plotList = newPlots
                        data.plots = newPlots
                        onDataChanged(data)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = LandGreenPrimary),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Plot", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("নতুন প্লট যোগ", fontSize = 11.5.sp)
                }
            }
        }

        // Plots Cards (Excel Rows)
        itemsIndexed(plotList) { index, plot ->
            var name by remember(plot.id) { mutableStateOf(plot.plotName) }
            var north by remember(plot.id) { mutableStateOf(plot.northFt.toString()) }
            var south by remember(plot.id) { mutableStateOf(plot.southFt.toString()) }
            var east by remember(plot.id) { mutableStateOf(plot.eastFt.toString()) }
            var west by remember(plot.id) { mutableStateOf(plot.westFt.toString()) }
            var diagonal by remember(plot.id) { mutableStateOf(if (plot.diagonalFt > 0) plot.diagonalFt.toString() else "") }
            var notes by remember(plot.id) { mutableStateOf(plot.notes) }

            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, BorderColor),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                color = LandGreenPrimary,
                                shape = CircleShape,
                                modifier = Modifier.size(24.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("${index + 1}", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            OutlinedTextField(
                                value = name,
                                onValueChange = {
                                    name = it
                                    plot.plotName = it
                                    onDataChanged(data)
                                },
                                modifier = Modifier.width(180.dp),
                                textStyle = TextStyle(color = Color.Black, fontSize = 13.5.sp, fontWeight = FontWeight.Bold),
                                colors = appTextFieldColors(),
                                singleLine = true
                            )
                        }

                        if (plotList.size > 1) {
                            IconButton(
                                onClick = {
                                    val newPlots = plotList.toMutableList().apply { removeAt(index) }
                                    plotList = newPlots
                                    data.plots = newPlots
                                    onDataChanged(data)
                                }
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red.copy(alpha = 0.7f))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text("চার বাহুর দৈর্ঘ্য ও প্রস্থ (ফুট হিসেবে):", fontSize = 11.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        DimensionInput("উত্তর বাহু", north, Modifier.weight(1f)) {
                            north = it
                            plot.northFt = it.toDoubleOrNull() ?: 0.0
                            onDataChanged(data)
                        }
                        DimensionInput("দক্ষিণ বাহু", south, Modifier.weight(1f)) {
                            south = it
                            plot.southFt = it.toDoubleOrNull() ?: 0.0
                            onDataChanged(data)
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        DimensionInput("পূর্ব বাহু", east, Modifier.weight(1f)) {
                            east = it
                            plot.eastFt = it.toDoubleOrNull() ?: 0.0
                            onDataChanged(data)
                        }
                        DimensionInput("পশ্চিম বাহু", west, Modifier.weight(1f)) {
                            west = it
                            plot.westFt = it.toDoubleOrNull() ?: 0.0
                            onDataChanged(data)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        DimensionInput("কর্ণ (অপশনাল)", diagonal, Modifier.weight(1f)) {
                            diagonal = it
                            plot.diagonalFt = it.toDoubleOrNull() ?: 0.0
                            onDataChanged(data)
                        }
                        OutlinedTextField(
                            value = notes,
                            onValueChange = {
                                notes = it
                                plot.notes = it
                                onDataChanged(data)
                            },
                            label = { Text("মন্তব্য", fontSize = 10.sp) },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            textStyle = TextStyle(color = Color.Black, fontSize = 12.5.sp, fontWeight = FontWeight.Medium),
                            colors = appTextFieldColors()
                        )
                    }

                    // Auto Calculation Breakdown for this plot
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        color = Color(0xFFF1F5F9),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("বর্গফুট: ${df.format(plot.squareFeet)}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            Text("শতাংশ: ${df.format(plot.decimal)}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = LandGreenPrimary)
                            Text("কাঠা: ${df.format(plot.katha)}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }

        // Navigation Actions
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onSwitchToWord,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("📝 Word রিপোর্ট প্রস্তুত", fontSize = 12.sp)
                }
                Button(
                    onClick = onSwitchToPdf,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = LandGreenPrimary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("🖨️ PDF তৈরি ও প্রিন্ট", fontSize = 12.sp, color = Color.White)
                }
            }
        }
    }
}

@Composable
fun SummaryPill(label: String, value: String, color: Color) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(0.8.dp, color.copy(alpha = 0.3f)),
        modifier = Modifier.padding(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label, fontSize = 9.5.sp, color = Color.Gray)
            Text(value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = color)
        }
    }
}

@Composable
fun DimensionInput(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontSize = 10.sp) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        singleLine = true,
        modifier = modifier,
        textStyle = TextStyle(color = Color.Black, fontSize = 13.sp, fontWeight = FontWeight.SemiBold),
        colors = appTextFieldColors()
    )
}

@Composable
fun SurveyTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    maxLines: Int = 1
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontSize = 11.sp) },
        modifier = modifier,
        singleLine = singleLine,
        maxLines = maxLines,
        textStyle = TextStyle(color = Color.Black, fontSize = 13.5.sp, fontWeight = FontWeight.Medium),
        colors = appTextFieldColors()
    )
}

// ----------------------------------------------------------------------------
// 2. WORD DOCUMENT BUILDER TAB
// ----------------------------------------------------------------------------
@Composable
fun WordDocumentBuilderTab(
    data: SurveyReportData,
    onDataChanged: (SurveyReportData) -> Unit,
    onSwitchToPreview: () -> Unit,
    onSwitchToPdf: () -> Unit
) {
    var orgName by remember { mutableStateOf(data.organizationName) }
    var surveyorName by remember { mutableStateOf(data.surveyorName) }
    var surveyorReg by remember { mutableStateOf(data.surveyorRegNo) }
    var clientName by remember { mutableStateOf(data.clientName) }
    var clientFather by remember { mutableStateOf(data.clientFather) }
    var district by remember { mutableStateOf(data.district) }
    var upazila by remember { mutableStateOf(data.upazila) }
    var mouza by remember { mutableStateOf(data.mouza) }
    var jlNo by remember { mutableStateOf(data.jlNo) }
    var khatianType by remember { mutableStateOf(data.khatianType) }
    var khatianNo by remember { mutableStateOf(data.khatianNo) }
    var dagNo by remember { mutableStateOf(data.dagNo) }
    var landType by remember { mutableStateOf(data.landType) }

    var northBound by remember { mutableStateOf(data.northBoundary) }
    var southBound by remember { mutableStateOf(data.southBoundary) }
    var eastBound by remember { mutableStateOf(data.eastBoundary) }
    var westBound by remember { mutableStateOf(data.westBoundary) }

    var remarks by remember { mutableStateOf(data.findingsRemarks) }
    var legalAdvice by remember { mutableStateOf(data.legalAdvice) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Word Ribbon Header
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E3A8A)), // Word Navy Blue
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("📝", fontSize = 28.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Microsoft Word সার্ভে রিপোর্ট বিল্ডার",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color.White
                        )
                        Text(
                            "অফিসিয়াল প্রতিবেদন, খতিয়ান-দাগ ও আইনি মন্তব্য এডিট করুন।",
                            fontSize = 11.sp,
                            color = Color(0xFFDBEAFE)
                        )
                    }
                }
            }
        }

        // Section 1: Organization & Surveyor
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, BorderColor),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("🏛️ সার্ভেয়ার ও প্রতিষ্ঠানের তথ্য", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = LandGreenDark)
                    Spacer(modifier = Modifier.height(10.dp))
                    SurveyTextField(
                        value = orgName,
                        onValueChange = { orgName = it; data.organizationName = it; onDataChanged(data) },
                        label = "প্রতিষ্ঠান / ফার্মের নাম",
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        SurveyTextField(
                            value = surveyorName,
                            onValueChange = { surveyorName = it; data.surveyorName = it; onDataChanged(data) },
                            label = "সার্ভেয়ার / আমিনের নাম",
                            modifier = Modifier.weight(1f)
                        )
                        SurveyTextField(
                            value = surveyorReg,
                            onValueChange = { surveyorReg = it; data.surveyorRegNo = it; onDataChanged(data) },
                            label = "রেজিস্ট্রেশন নং",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Section 2: Land & Client Info
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, BorderColor),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("👤 জমির মালিক ও অবস্থান পরিচিতি", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = LandGreenDark)
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        SurveyTextField(
                            value = clientName,
                            onValueChange = { clientName = it; data.clientName = it; onDataChanged(data) },
                            label = "মালিকের নাম",
                            modifier = Modifier.weight(1f)
                        )
                        SurveyTextField(
                            value = clientFather,
                            onValueChange = { clientFather = it; data.clientFather = it; onDataChanged(data) },
                            label = "পিতা / স্বামীর নাম",
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        SurveyTextField(
                            value = district,
                            onValueChange = { district = it; data.district = it; onDataChanged(data) },
                            label = "জেলা",
                            modifier = Modifier.weight(1f)
                        )
                        SurveyTextField(
                            value = upazila,
                            onValueChange = { upazila = it; data.upazila = it; onDataChanged(data) },
                            label = "উপজেলা/থানা",
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        SurveyTextField(
                            value = mouza,
                            onValueChange = { mouza = it; data.mouza = it; onDataChanged(data) },
                            label = "মৌজা",
                            modifier = Modifier.weight(1.2f)
                        )
                        SurveyTextField(
                            value = jlNo,
                            onValueChange = { jlNo = it; data.jlNo = it; onDataChanged(data) },
                            label = "জে.এল নং",
                            modifier = Modifier.weight(0.8f)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        SurveyTextField(
                            value = khatianType,
                            onValueChange = { khatianType = it; data.khatianType = it; onDataChanged(data) },
                            label = "খতিয়ানের ধরন (RS/BS)",
                            modifier = Modifier.weight(1f)
                        )
                        SurveyTextField(
                            value = khatianNo,
                            onValueChange = { khatianNo = it; data.khatianNo = it; onDataChanged(data) },
                            label = "খতিয়ান নং",
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        SurveyTextField(
                            value = dagNo,
                            onValueChange = { dagNo = it; data.dagNo = it; onDataChanged(data) },
                            label = "দাগ নং (হাল ও সাবেক)",
                            modifier = Modifier.weight(1f)
                        )
                        SurveyTextField(
                            value = landType,
                            onValueChange = { landType = it; data.landType = it; onDataChanged(data) },
                            label = "জমির শ্রেণি",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Section 3: Boundaries (চতুর্সীমা)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, BorderColor),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("🧭 চতুর্সীমা ও সীমানা বিবরণ", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = LandGreenDark)
                    Spacer(modifier = Modifier.height(10.dp))
                    SurveyTextField(
                        value = northBound,
                        onValueChange = { northBound = it; data.northBoundary = it; onDataChanged(data) },
                        label = "উত্তরে কার জমি / সীমানা",
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    SurveyTextField(
                        value = southBound,
                        onValueChange = { southBound = it; data.southBoundary = it; onDataChanged(data) },
                        label = "দক্ষিণে কার জমি / সীমানা",
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    SurveyTextField(
                        value = eastBound,
                        onValueChange = { eastBound = it; data.eastBoundary = it; onDataChanged(data) },
                        label = "পূর্বে কার জমি / সীমানা",
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    SurveyTextField(
                        value = westBound,
                        onValueChange = { westBound = it; data.westBoundary = it; onDataChanged(data) },
                        label = "পশ্চিমে কার জমি / সীমানা",
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Section 4: Remarks & AI Drafter
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, BorderColor),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("⚖️ সার্ভেয়ারের পেশাদার অভিমত", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = LandGreenDark)
                        Surface(
                            color = LandGreenDark,
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.clickable {
                                val autoText = "অত্র মৌজার ${data.khatianType} খতিয়ানভুক্ত ${data.dagNo} দাগে সরেজমিনে উপস্থিত হয়ে ডিজিটাল ফিতা দ্বারা নিখুঁতভাবে সীমানা পরিমাপ করা হয়েছে। পরিমাপে মোট ${data.formattedTotalSummary()} জমি সম্পূর্ণ সঠিক সীমানায় পাওয়া গেছে।"
                                remarks = autoText
                                data.findingsRemarks = autoText
                                onDataChanged(data)
                            }
                        ) {
                            Text(
                                "✨ অটো ড্রাফট",
                                fontSize = 10.5.sp,
                                color = GoldAccent,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    SurveyTextField(
                        value = remarks,
                        onValueChange = { remarks = it; data.findingsRemarks = it; onDataChanged(data) },
                        label = "সার্ভেয়ারের মন্তব্য ও সিদ্ধান্ত",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp),
                        singleLine = false,
                        maxLines = 5
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    SurveyTextField(
                        value = legalAdvice,
                        onValueChange = { legalAdvice = it; data.legalAdvice = it; onDataChanged(data) },
                        label = "আইনি ও নামজারি সুপারিশ",
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Action Buttons
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onSwitchToPreview,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("👁️ লাইভ প্রিভিউ", fontSize = 12.sp)
                }
                Button(
                    onClick = onSwitchToPdf,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = LandGreenPrimary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("🖨️ PDF ডাউনলোড", fontSize = 12.sp, color = Color.White)
                }
            }
        }
    }
}

// ----------------------------------------------------------------------------
// 3. LIVE PREVIEW TAB
// ----------------------------------------------------------------------------
@Composable
fun SurveyLivePreviewTab(
    data: SurveyReportData,
    onGeneratePdf: () -> Unit
) {
    val df = remember { DecimalFormat("#,##0.00") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            // A4 Paper Mockup Card
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(2.dp, LandGreenDark),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .border(1.dp, GoldAccent.copy(alpha = 0.7f), RoundedCornerShape(4.dp))
                        .padding(12.dp)
                ) {
                    // Header
                    Surface(
                        color = LandGreenDark,
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(data.organizationName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text("জমির ডিজিটাল সীমানা পরিমাপ ও সার্ভেয়ার সনদপত্র", color = GoldLight, fontSize = 10.5.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("স্মারক নং: ${data.reportId}", fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                        Text("তারিখ: ${data.date}", fontSize = 9.5.sp)
                    }

                    Divider(modifier = Modifier.padding(vertical = 8.dp), color = BorderColor)

                    // 1. General Land Info
                    Text("১. জমির সাধারণ তথ্য:", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = LandGreenDark)
                    Text("মালিক: ${data.clientName} (পিতা/স্বামী: ${data.clientFather})", fontSize = 10.sp)
                    Text("জেলা: ${data.district}, উপজেলা: ${data.upazila}, মৌজা: ${data.mouza} (জেএল: ${data.jlNo})", fontSize = 10.sp)
                    Text("খতিয়ান: ${data.khatianType} নং ${data.khatianNo}, দাগ নং: ${data.dagNo}, শ্রেণি: ${data.landType}", fontSize = 10.sp)

                    Spacer(modifier = Modifier.height(8.dp))

                    // 2. Excel Table Breakdown
                    Text("২. প্লটভিত্তিক পরিমাপ ও ক্ষেত্রফল হিসাব ছক:", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = LandGreenDark)
                    Spacer(modifier = Modifier.height(4.dp))
                    data.plots.forEachIndexed { i, p ->
                        Surface(
                            color = if (i % 2 == 0) Color(0xFFF8FAFC) else Color.White,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 4.dp, horizontal = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("${i + 1}. ${p.plotName}", fontSize = 9.5.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                                Text("উ:${p.northFt} দ:${p.southFt} পূ:${p.eastFt} প:${p.westFt}", fontSize = 9.sp, color = Color.Gray)
                                Text("${df.format(p.decimal)} শতক", fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = LandGreenPrimary)
                            }
                        }
                    }

                    // Total ribbon
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        color = Color(0xFFFEF9C3),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "সর্বমোট: ${df.format(data.totalDecimal)} শতাংশ (${df.format(data.totalKatha)} কাঠা / ${df.format(data.totalSquareFeet)} sq.ft)",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF854D0E),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(6.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // 3. Boundaries
                    Text("৩. চতুর্সীমা:", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = LandGreenDark)
                    Text("উত্তরে: ${data.northBoundary}", fontSize = 9.5.sp)
                    Text("দক্ষিণে: ${data.southBoundary}", fontSize = 9.5.sp)
                    Text("পূর্বে: ${data.eastBoundary}", fontSize = 9.5.sp)
                    Text("পশ্চিমে: ${data.westBoundary}", fontSize = 9.5.sp)

                    Spacer(modifier = Modifier.height(8.dp))

                    // 4. Remarks
                    Text("৪. সার্ভেয়ারের সিদ্ধান্ত ও অভিমত:", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = LandGreenDark)
                    Text(data.findingsRemarks, fontSize = 9.5.sp, color = TextPrimary)

                    Spacer(modifier = Modifier.height(20.dp))

                    // 5. Signatures
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("_____________________", fontSize = 10.sp, color = Color.Gray)
                            Text("মালিকের স্বাক্ষর", fontSize = 9.sp)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("_____________________", fontSize = 10.sp, color = Color.Gray)
                            Text(data.surveyorName, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = LandGreenDark)
                            Text("সনদপ্রাপ্ত ডিজিটাল সার্ভেয়ার", fontSize = 8.sp, color = Color.Gray)
                        }
                    }
                }
            }
        }

        item {
            Button(
                onClick = onGeneratePdf,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = LandGreenPrimary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("অফিসিয়াল A4 PDF ডাউনলোড ও প্রিন্ট করুন", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

// ----------------------------------------------------------------------------
// 4. PDF EXPORT & PRINT TAB
// ----------------------------------------------------------------------------
@Composable
fun PdfExportAndPrintTab(
    data: SurveyReportData,
    pdfFile: File?,
    isGenerating: Boolean,
    onGeneratePdf: () -> Unit,
    onExportWord: () -> Unit,
    onExportExcel: () -> Unit
) {
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = LandGreenDark),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🖨️", fontSize = 32.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                "ডাউনলোড ও প্রিন্ট কন্ট্রোল সেন্টার",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color.White
                            )
                            Text(
                                "PDF, Word এবং Excel ফরম্যাটে এক্সপোর্ট করুন।",
                                fontSize = 11.sp,
                                color = GoldLight
                            )
                        }
                    }
                }
            }
        }

        // 1. PDF Download & Print Actions Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, BorderColor),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = LandGreenContainer,
                            shape = CircleShape,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("📄", fontSize = 18.sp)
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("অফিসিয়াল সার্ভে রিপোর্ট PDF", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = LandGreenDark)
                            Text("উন্নত এ৪ সাইজ ডাবল বর্ডার ও স্বাক্ষর সম্বলিত", fontSize = 11.sp, color = Color.Gray)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = onGeneratePdf,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = LandGreenPrimary),
                        shape = RoundedCornerShape(10.dp),
                        enabled = !isGenerating
                    ) {
                        if (isGenerating) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("PDF প্রস্তুত হচ্ছে...", color = Color.White)
                        } else {
                            Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("📥 PDF ডাউনলোড করুন", fontSize = 13.5.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }

                    if (pdfFile != null && pdfFile.exists()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = {
                                    SurveyPdfGenerator.printSurveyPdf(context, pdfFile)
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("সরাসরি প্রিন্ট", fontSize = 12.sp)
                            }

                            Button(
                                onClick = {
                                    SurveyExportHelper.shareFile(context, pdfFile, "application/pdf", "Land Survey PDF")
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("শেয়ার করুন", fontSize = 12.sp, color = Color.White)
                            }
                        }
                    }
                }
            }
        }

        // 2. Microsoft Word & Excel Export Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, BorderColor),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "💼 মাইক্রোসফট অফিস ফরম্যাট এক্সপোর্ট",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = LandGreenDark
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Word Export Button
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFEFF6FF))
                            .clickable { onExportWord() }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("📝", fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Microsoft Word (.doc) এক্সপোর্ট", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF1E3A8A))
                            Text("ওয়ার্ড ফাইলে প্রতিবেদন এডিট ও কাস্টমাইজ করতে", fontSize = 10.5.sp, color = Color.Gray)
                        }
                        Text("ডাউনলোড >", color = Color(0xFF1E3A8A), fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Excel Export Button
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFF0FDF4))
                            .clickable { onExportExcel() }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("📊", fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Microsoft Excel (.csv) এক্সপোর্ট", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF15803D))
                            Text("স্প্রেডশিটে প্লটের গানিতিক তথ্য ও অংশ বণ্টন চার্ট", fontSize = 10.5.sp, color = Color.Gray)
                        }
                        Text("ডাউনলোড >", color = Color(0xFF15803D), fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                    }
                }
            }
        }
    }
}
