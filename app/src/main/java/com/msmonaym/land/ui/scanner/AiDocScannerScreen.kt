package com.msmonaym.land.ui.scanner

import android.Manifest
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color as AndroidColor
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.*
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.msmonaym.land.data.ai.AiCategory
import com.msmonaym.land.data.ai.LandAiService
import com.msmonaym.land.ui.ai.AiFloatingActionButton
import com.msmonaym.land.ui.ai.AiLandAssistantModal
import com.msmonaym.land.ui.ai.AiTopBarAction
import com.msmonaym.land.ui.theme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

enum class ScanAnalysisType(val id: String, val titleBn: String, val titleEn: String, val emoji: String, val descBn: String) {
    COMPREHENSIVE(
        "comprehensive",
        "পূর্ণাঙ্গ লিগ্যাল অডিট",
        "Full Legal Audit",
        "🏛️",
        "দলিল, পক্ষগণ, তফসিল, মালিকানা ধারা ও ঝুঁকি নিরীক্ষা"
    ),
    KHATIAN_DAG(
        "khatian_dag",
        "খতিয়ান ও দাগ যাচাই",
        "Khatian & Dag Check",
        "📜",
        "সিএস, এসএ, আরএস, বিএস জরিপ ও দাগ নম্বর বিশ্লেষণ"
    ),
    OWNERSHIP_CHAIN(
        "ownership_chain",
        "মালিকানা ধারাবাহিকতা",
        "Chain of Title",
        "⚖️",
        "পিঠ দলিল, নামজারি ও ওয়ারিশান হিস্যা ধারাবাহিকতা"
    ),
    FRAUD_CHECK(
        "fraud_check",
        "আইনি ঝুঁকি ও জালিয়াতি",
        "Fraud & Risk Audit",
        "🚨",
        "ভূমি অপরাধ আইন ২০২৩ অনুযায়ী কোনো জালিয়াতি আছে কিনা"
    ),
    QUICK_SUMMARY(
        "quick_summary",
        "সংক্ষিপ্ত নির্বাহী সারাংশ",
        "Quick Summary",
        "📝",
        "মূল তথ্য, জমির পরিমাণ ও পক্ষগণের সংক্ষিপ্ত বিবরণ"
    )
}

enum class DocSampleType(val title: String, val subtitle: String, val emoji: String) {
    DEED_SAF_KABLA("সাফ-কবলা জমি ক্রয় দলিল", "রেজিস্ট্রি দলিল নং ৪৭৮২/২০২২ (ধানমন্ডি মৌজা)", "📄"),
    KHATIAN_RS("আরএস জরিপ স্বত্ব খতিয়ান", "খতিয়ান নং ১০৫, দাগ নং ৪২০/৮৫১", "📜"),
    MUTATION_DCR("ই-নামজারি ও ডিসিআর কপি", "খারিজ কেস নং ১২৮৪/২০২৩ (সহকারী কমিশনার ভূমি)", "🏛️"),
    TAX_DAKHILA("ভূমি উন্নয়ন কর (খাজনা দাখিলা)", "অনলাইন হোল্ডিং কর রসিদ ১৪৩১ সন", "💰")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiDocScannerScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val coroutineScope = rememberCoroutineScope()

    // State Variables
    var selectedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var selectedImageName by remember { mutableStateOf("নির্বাচিত দলিল") }
    var selectedLanguage by remember { mutableStateOf("bn") } // "bn" or "en"
    var selectedAnalysisType by remember { mutableStateOf(ScanAnalysisType.COMPREHENSIVE) }
    var userCustomQuery by remember { mutableStateOf("") }

    var isScanning by remember { mutableStateOf(false) }
    var generatedReport by remember { mutableStateOf<String?>(null) }
    var showAiAssistant by remember { mutableStateOf(false) }
    var showSampleDialog by remember { mutableStateOf(false) }

    // TTS Voice Assistant State
    var ttsEngine by remember { mutableStateOf<TextToSpeech?>(null) }
    var isSpeaking by remember { mutableStateOf(false) }

    // PDF Export State
    var generatedPdfFile by remember { mutableStateOf<File?>(null) }
    var isGeneratingPdf by remember { mutableStateOf(false) }

    // Initialize TTS
    DisposableEffect(Unit) {
        val tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                // Initialize default
            }
        }
        ttsEngine = tts
        onDispose {
            tts.stop()
            tts.shutdown()
        }
    }

    // Camera Launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            selectedBitmap = bitmap
            selectedImageName = "ক্যামেরা ছবি (${SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())})"
            generatedReport = null
            generatedPdfFile = null
            Toast.makeText(context, "ডকুমেন্টের ছবি গৃহীত হয়েছে!", Toast.LENGTH_SHORT).show()
        }
    }

    // Camera Permission Launcher
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            cameraLauncher.launch(null)
        } else {
            Toast.makeText(context, "ক্যামেরা পারমিশন ছাড়া সরাসরি ছবি তোলা যাবে না। গ্যালারি থেকে ছবি নির্বাচন করতে পারেন।", Toast.LENGTH_LONG).show()
        }
    }

    // Gallery Picker Launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()
                if (bitmap != null) {
                    selectedBitmap = bitmap
                    selectedImageName = "গ্যালারি ইমেজ (${SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())})"
                    generatedReport = null
                    generatedPdfFile = null
                    Toast.makeText(context, "গ্যালারি থেকে ডকুমেন্ট লোড হয়েছে!", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "ইমেজ লোড করতে সমস্যা হয়েছে", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Function to run AI Analysis
    fun runAiAnalysis() {
        val bitmap = selectedBitmap ?: return
        isScanning = true
        generatedReport = null
        generatedPdfFile = null

        if (isSpeaking) {
            ttsEngine?.stop()
            isSpeaking = false
        }

        coroutineScope.launch {
            try {
                val result = LandAiService.analyzeDocumentImage(
                    bitmap = bitmap,
                    analysisType = selectedAnalysisType.id,
                    language = selectedLanguage,
                    userNote = userCustomQuery
                )
                generatedReport = result
                Toast.makeText(context, "এআই লিগ্যাল অডিট সম্পন্ন হয়েছে!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "বিশ্লেষণ করতে সমস্যা হয়েছে", Toast.LENGTH_SHORT).show()
            } finally {
                isScanning = false
            }
        }
    }

    // Function to toggle TTS Voice
    fun toggleTts() {
        val report = generatedReport ?: return
        val tts = ttsEngine ?: return

        if (isSpeaking) {
            tts.stop()
            isSpeaking = false
        } else {
            val targetLocale = if (selectedLanguage == "en") Locale.ENGLISH else Locale("bn", "BD")
            tts.language = targetLocale
            // Strip markdown asterisks for clean speech
            val cleanText = report.replace("*", "").replace("#", "").replace("-", " ")
            tts.speak(cleanText, TextToSpeech.QUEUE_FLUSH, null, "doc_audit_id")
            isSpeaking = true
            Toast.makeText(context, "রিপোর্ট পড়া শুরু হয়েছে...", Toast.LENGTH_SHORT).show()
        }
    }

    // Function to generate PDF
    fun generatePdfReport() {
        val report = generatedReport ?: return
        isGeneratingPdf = true

        coroutineScope.launch(Dispatchers.IO) {
            try {
                val pdfDocument = PdfDocument()
                val pageWidth = 595
                val pageHeight = 842 // Standard A4 points
                var pageNumber = 1

                val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
                var page = pdfDocument.startPage(pageInfo)
                var canvas = page.canvas

                val paint = Paint().apply {
                    isAntiAlias = true
                }

                // Draw Header Background
                paint.color = AndroidColor.parseColor("#065428")
                canvas.drawRect(0f, 0f, pageWidth.toFloat(), 95f, paint)

                // Header Title
                paint.color = AndroidColor.parseColor("#D4AF37")
                paint.textSize = 10f
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                canvas.drawText("গণপ্রজাতন্ত্রী বাংলাদেশ ভূমি সেবা ডিজিটাল • এআই লিগ্যাল অডিট", 28f, 30f, paint)

                paint.color = AndroidColor.WHITE
                paint.textSize = 16f
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                val headerTitle = if (selectedLanguage == "en") "AI LAND DOCUMENT LEGAL AUDIT REPORT" else "স্মার্ট এআই ভূমি ও দলিল লিগ্যাল অডিট রিপোর্ট"
                canvas.drawText(headerTitle, 28f, 56f, paint)

                paint.textSize = 9.5f
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                paint.color = AndroidColor.parseColor("#E0E7E1")
                val dateStr = SimpleDateFormat("dd MMMM, yyyy • hh:mm a", Locale.getDefault()).format(Date())
                canvas.drawText("বিশ্লেষণের তারিখ: $dateStr • ধরণ: ${selectedAnalysisType.titleBn}", 28f, 78f, paint)

                // Verification Stamp Box
                paint.color = AndroidColor.parseColor("#D4AF37")
                paint.style = Paint.Style.STROKE
                paint.strokeWidth = 1.2f
                canvas.drawRoundRect(415f, 25f, 570f, 75f, 8f, 8f, paint)

                paint.style = Paint.Style.FILL
                paint.color = AndroidColor.parseColor("#FFFBEB")
                paint.textSize = 8.5f
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                canvas.drawText("★ AI VERIFIED ★", 445f, 44f, paint)
                paint.color = AndroidColor.parseColor("#065428")
                paint.textSize = 7.5f
                canvas.drawText("আইনগত নিরীক্ষা সম্পন্ন", 432f, 62f, paint)

                // Body content lines
                var yPos = 125f
                val margin = 32f
                val contentWidth = pageWidth - (margin * 2)

                val lines = report.split("\n")
                for (line in lines) {
                    if (yPos > pageHeight - 60) {
                        pdfDocument.finishPage(page)
                        pageNumber++
                        val nextPageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
                        page = pdfDocument.startPage(nextPageInfo)
                        canvas = page.canvas

                        // Mini Header for subsequent pages
                        paint.color = AndroidColor.parseColor("#065428")
                        canvas.drawRect(0f, 0f, pageWidth.toFloat(), 35f, paint)
                        paint.color = AndroidColor.WHITE
                        paint.textSize = 9f
                        canvas.drawText("AI Land Audit Report • পৃষ্ঠা $pageNumber", 28f, 22f, paint)
                        yPos = 60f
                    }

                    when {
                        line.startsWith("###") -> {
                            yPos += 12f
                            paint.color = AndroidColor.parseColor("#0B7A3D")
                            paint.textSize = 12.5f
                            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                            val clean = line.replace("###", "").trim()
                            canvas.drawText(clean, margin, yPos, paint)
                            yPos += 6f
                            paint.color = AndroidColor.parseColor("#E0E7E1")
                            paint.strokeWidth = 0.8f
                            canvas.drawLine(margin, yPos, margin + contentWidth, yPos, paint)
                            yPos += 14f
                        }
                        line.startsWith("•") || line.startsWith("-") || line.startsWith("১.") || line.startsWith("২.") || line.startsWith("1.") || line.startsWith("2.") -> {
                            paint.color = AndroidColor.parseColor("#1B261D")
                            paint.textSize = 9.5f
                            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                            val clean = line.replace("**", "").trim()

                            // Simple word wrap
                            val words = clean.split(" ")
                            var currentLine = ""
                            for (word in words) {
                                val testLine = if (currentLine.isEmpty()) word else "$currentLine $word"
                                if (paint.measureText(testLine) < contentWidth - 10) {
                                    currentLine = testLine
                                } else {
                                    canvas.drawText(currentLine, margin + 8, yPos, paint)
                                    yPos += 14f
                                    currentLine = word
                                }
                            }
                            if (currentLine.isNotEmpty()) {
                                canvas.drawText(currentLine, margin + 8, yPos, paint)
                                yPos += 16f
                            }
                        }
                        line.startsWith("---") -> {
                            yPos += 6f
                            paint.color = AndroidColor.parseColor("#D4AF37")
                            paint.strokeWidth = 1f
                            canvas.drawLine(margin, yPos, margin + contentWidth, yPos, paint)
                            yPos += 12f
                        }
                        line.isBlank() -> {
                            yPos += 6f
                        }
                        else -> {
                            paint.color = AndroidColor.parseColor("#374151")
                            paint.textSize = 9.5f
                            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                            val clean = line.replace("**", "").trim()
                            canvas.drawText(clean, margin, yPos, paint)
                            yPos += 15f
                        }
                    }
                }

                // Footer on last page
                paint.color = AndroidColor.parseColor("#9CA3AF")
                paint.textSize = 8f
                canvas.drawText("ভূমি সেবা ডিজিটাল • হটলাইন: ১৬১২২ • M.S MONAYM ENT. স্মার্ট ল্যান্ড এইড", margin, pageHeight - 25f, paint)

                pdfDocument.finishPage(page)

                // Save to app cache
                val pdfDir = File(context.cacheDir, "land_scanner_reports")
                if (!pdfDir.exists()) pdfDir.mkdirs()
                val fileName = "Land_Legal_Audit_${System.currentTimeMillis()}.pdf"
                val outFile = File(pdfDir, fileName)

                val outStream = FileOutputStream(outFile)
                pdfDocument.writeTo(outStream)
                outStream.flush()
                outStream.close()
                pdfDocument.close()

                withContext(Dispatchers.Main) {
                    generatedPdfFile = outFile
                    isGeneratingPdf = false
                    Toast.makeText(context, "PDF রিপোর্ট তৈরি হয়েছে!", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    isGeneratingPdf = false
                    Toast.makeText(context, "PDF তৈরিতে সমস্যা হয়েছে: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Function to open / view PDF
    fun openPdf(file: File) {
        try {
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(Intent.createChooser(intent, "PDF রিপোর্ট ওপেন করুন"))
        } catch (e: Exception) {
            Toast.makeText(context, "PDF ভিউয়ার পাওয়া যায়নি", Toast.LENGTH_SHORT).show()
        }
    }

    // Function to share PDF
    fun sharePdf(file: File) {
        try {
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "স্মার্ট এআই ভূমি ও দলিল লিগ্যাল অডিট রিপোর্ট")
                putExtra(Intent.EXTRA_TEXT, "ভূমি সেবা ডিজিটাল অ্যাপ থেকে তৈরি করা AI দলিল লিগ্যাল অডিট রিপোর্ট।")
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            }
            context.startActivity(Intent.createChooser(intent, "PDF রিপোর্ট শেয়ার করুন"))
        } catch (e: Exception) {
            Toast.makeText(context, "শেয়ার করতে সমস্যা হয়েছে", Toast.LENGTH_SHORT).show()
        }
    }

    // Helper to generate a realistic sample document bitmap for testing
    fun loadSampleDocument(type: DocSampleType) {
        val width = 720
        val height = 960
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint().apply { isAntiAlias = true }

        // Background paper texture color
        paint.color = AndroidColor.parseColor("#FFFDF5")
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)

        // Outer border
        paint.color = AndroidColor.parseColor("#0B7A3D")
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 6f
        canvas.drawRect(20f, 20f, width - 20f, height - 20f, paint)

        paint.color = AndroidColor.parseColor("#D4AF37")
        paint.strokeWidth = 2f
        canvas.drawRect(28f, 28f, width - 28f, height - 28f, paint)

        paint.style = Paint.Style.FILL

        when (type) {
            DocSampleType.DEED_SAF_KABLA -> {
                paint.color = AndroidColor.parseColor("#854D0E")
                paint.textSize = 28f
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                paint.textAlign = Paint.Align.CENTER
                canvas.drawText("গণপ্রজাতন্ত্রী বাংলাদেশ সরকার", width / 2f, 80f, paint)
                paint.textSize = 22f
                paint.color = AndroidColor.parseColor("#065428")
                canvas.drawText("সাফ-কবলা জমি বিক্রয় দলিল", width / 2f, 120f, paint)
                paint.textSize = 15f
                paint.color = AndroidColor.parseColor("#374151")
                canvas.drawText("দলিল নং: ৪৭৮২/২০২২ • সাব-রেজিস্ট্রি অফিস: ধানমন্ডি, ঢাকা", width / 2f, 155f, paint)

                paint.textAlign = Paint.Align.LEFT
                paint.textSize = 14f
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                paint.color = AndroidColor.parseColor("#1F2937")

                val deedText = listOf(
                    "১ম পক্ষ (বিক্রেতা/দাতা): মো: আবদুর রহিম, পিতা: মৃত শামসুল হক",
                    "২য় পক্ষ (ক্রেতা/গ্রহীতা): ড. মোহাম্মদ ইসমাইল, পিতা: আব্দুল জব্বার",
                    "তফসিল বিবরণ: জেলা: ঢাকা, থানা: ধানমন্ডি, মৌজা: ধানমন্ডি, জেএল নং: ০৫",
                    "খতিয়ান নং: আরএস খতিয়ান ১০৫, সিটি জরিপ খতিয়ান নং ৪২০",
                    "দাগ নম্বর: সাবেক দাগ নং ৩১৫, হাল দাগ নং ৭৫০",
                    "জমির পরিমাণ: ৩.৫ শতাংশ (বাস্তুভিটা ও পাকা ইমারত)",
                    "পণমূল্য (বিক্রয় মূল্য): ৩৫,০০,০০০/- (পঁয়ত্রিশ লক্ষ টাকা মাত্র)",
                    "চৌহদ্দি: উত্তরে ৬০ ফুট রাস্তা, দক্ষিণে করিম মিয়ার জমি, পূর্বে রফিকের সীমানা",
                    "শর্তাবলী: দাতা অত্র দলিলের মাধ্যমে সমুদয় স্বত্ব ও দখল ক্রেতাকে সমর্পণ করিলেন।"
                )
                var y = 220f
                for (t in deedText) {
                    canvas.drawText(t, 50f, y, paint)
                    y += 42f
                }
            }
            DocSampleType.KHATIAN_RS -> {
                paint.color = AndroidColor.parseColor("#065428")
                paint.textSize = 26f
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                paint.textAlign = Paint.Align.CENTER
                canvas.drawText("বাংলাদেশ ফরম নং ৫৪৬৩ (সংশোধিত)", width / 2f, 80f, paint)
                paint.textSize = 22f
                paint.color = AndroidColor.parseColor("#854D0E")
                canvas.drawText("স্বত্ব বিবরণী ও আরএস খতিয়ান (RS Khatian)", width / 2f, 120f, paint)
                paint.textSize = 15f
                canvas.drawText("খতিয়ান নং: ১০৫ • জেলা: গাজীপুর • থানা: শ্রীপুর • মৌজা: বরমী", width / 2f, 155f, paint)

                paint.textAlign = Paint.Align.LEFT
                paint.textSize = 14f
                paint.color = AndroidColor.parseColor("#1F2937")
                val khatianText = listOf(
                    "মালিকের নাম ও অংশ: ১. মো: রফিকুল ইসলাম (০.৫০০ অংশ), ২. ফাতেমা বেগম (০.৫০০ অংশ)",
                    "দাগ নম্বর: আরএস হাল দাগ নং ৪২০, ৪২১, ৪২২",
                    "জমির শ্রেণী: নাল জমি ও ধানি জমি",
                    "দাগের মোট জমি: ০১ একর ২০ শতক",
                    "খতিয়ানে অত্র স্বত্বের অংশ: ৬০ শতক (উভয় অংশের সমান বণ্টন)",
                    "দখলদার: নিজ দখলে চাষাবাদরত",
                    "মন্তব্য: স্টেট একুইজিশন অ্যান্ড প্রজাস্বত্ব আইন ১৯৫০ অনুযায়ী প্রস্তুতকৃত।"
                )
                var y = 220f
                for (t in khatianText) {
                    canvas.drawText(t, 50f, y, paint)
                    y += 45f
                }
            }
            DocSampleType.MUTATION_DCR -> {
                paint.color = AndroidColor.parseColor("#065428")
                paint.textSize = 26f
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                paint.textAlign = Paint.Align.CENTER
                canvas.drawText("সহকারী কমিশনার (ভূমি) কার্যালয়", width / 2f, 80f, paint)
                paint.textSize = 20f
                paint.color = AndroidColor.parseColor("#854D0E")
                canvas.drawText("ই-নামজারি আদেশ ও ডিসিআর ডুপ্লিকেট কপি", width / 2f, 120f, paint)
                paint.textSize = 15f
                canvas.drawText("মিউটেশন কেস নং: ১২৮৪/২০২৩-২৪ • ইউনিয়ন ভূমি অফিস", width / 2f, 155f, paint)

                paint.textAlign = Paint.Align.LEFT
                paint.textSize = 14f
                paint.color = AndroidColor.parseColor("#1F2937")
                val dcrText = listOf(
                    "আবেদনকারী: আব্দুল হালিম, মাতা: মোসাম্মৎ মরিয়ম",
                    "খারিজকৃত মূল খতিয়ান নং: আরএস ৬৮, সাবেক জোত নং ১২",
                    "নতুন সৃজিত নামজারি খতিয়ান নং: খারিজ খতিয়ান ৯৫০",
                    "হাল দাগ নং: ৮৮/২ • জমির পরিমাণ: ১০.৫০ শতক (দশ দশমিক পাঁচ শূন্য শতক)",
                    "ডিসিআর সরকারি ফি: ১,১৭০/- টাকা ডিজিটাল পেমেন্ট সম্পন্ন (Bkash)",
                    "আদেশের তারিখ: ১৫/০৭/২০২৩ • ভূমি রেকর্ড ও জরিপ অধিদপ্তর।"
                )
                var y = 220f
                for (t in dcrText) {
                    canvas.drawText(t, 50f, y, paint)
                    y += 45f
                }
            }
            DocSampleType.TAX_DAKHILA -> {
                paint.color = AndroidColor.parseColor("#065428")
                paint.textSize = 26f
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                paint.textAlign = Paint.Align.CENTER
                canvas.drawText("ডিজিটাল ভূমি উন্নয়ন কর দাখিলা", width / 2f, 80f, paint)
                paint.textSize = 22f
                paint.color = AndroidColor.parseColor("#854D0E")
                canvas.drawText("ভূমি উন্নয়ন কর পরিশোধ দাখিলা (রসিদ)", width / 2f, 120f, paint)
                paint.textSize = 15f
                canvas.drawText("দাখিলা নং: 2024-LD-88934 • ১৪৩১ বাংলা সন", width / 2f, 155f, paint)

                paint.textAlign = Paint.Align.LEFT
                paint.textSize = 14f
                paint.color = AndroidColor.parseColor("#1F2937")
                val taxText = listOf(
                    "মালিকের নাম: হাজী মো: ওসমান গনি, হোল্ডিং নং: ১২০/ক",
                    "মৌজা: তেজগাঁও শিল্প এলাকা, থানা: তেজগাঁও",
                    "খতিয়ান নং: সিটি খতিয়ান ১২৩০ • দাগ নং: ১৫৪০",
                    "জমির শ্রেণী: বাণিজ্যিক (Commercial Land) • পরিমাণ: ০৮.০০ শতক",
                    "পরিশোধিত করের পরিমাণ: ৫,৪০০/- টাকা (সম্পূর্ণ হালনাগাদ)",
                    "কিউআর ভেরিফিকেশন: QR Verified Valid Govt Tax Receipt"
                )
                var y = 220f
                for (t in taxText) {
                    canvas.drawText(t, 50f, y, paint)
                    y += 45f
                }
            }
        }

        selectedBitmap = bitmap
        selectedImageName = type.title
        generatedReport = null
        generatedPdfFile = null
        Toast.makeText(context, "${type.title} লোড হয়েছে!", Toast.LENGTH_SHORT).show()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "এআই ক্যামেরা ও দলিল স্ক্যানার",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.5.sp,
                            color = Color.White
                        )
                        Text(
                            "Gemini AI Vision • দলিল ও খতিয়ান লিগ্যাল অডিট",
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
                    AiTopBarAction(onClick = { showAiAssistant = true })
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = LandGreenDark
                )
            )
        },
        floatingActionButton = {
            AiFloatingActionButton(onClick = { showAiAssistant = true })
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(SurfaceLight)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Hero Banner
            item {
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, LandGreenLight.copy(alpha = 0.4f)),
                    shadowElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                color = LandGreenPrimary.copy(alpha = 0.12f),
                                shape = CircleShape,
                                modifier = Modifier.size(44.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("📷", fontSize = 22.sp)
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        "স্মার্ট এআই ভিশন স্ক্যানার",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = LandGreenDark
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        color = LandGreenPrimary,
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            "Gemini 2.5",
                                            fontSize = 8.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                        )
                                    }
                                }
                                Text(
                                    "ক্যামেরা দিয়ে ছবি তুলুন বা ফাইল নির্বাচন করে তাৎক্ষণিক লিগ্যাল অডিট ও A4 PDF রিপোর্ট নিন",
                                    fontSize = 10.5.sp,
                                    color = TextSecondary,
                                    lineHeight = 14.sp
                                )
                            }
                        }
                    }
                }
            }

            // Image Selection Action Buttons
            item {
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, DividerColor),
                    shadowElevation = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            "১. ডকুমেন্টের ছবি নির্বাচন করুন",
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = LandGreenDark
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Camera Button
                            Button(
                                onClick = {
                                    val isGranted = androidx.core.content.ContextCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.CAMERA
                                    ) == android.content.pm.PackageManager.PERMISSION_GRANTED

                                    if (isGranted) {
                                        cameraLauncher.launch(null)
                                    } else {
                                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = LandGreenPrimary),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(vertical = 10.dp)
                            ) {
                                Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("ক্যামেরা", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            // Gallery Button
                            OutlinedButton(
                                onClick = { galleryLauncher.launch("image/*") },
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.2.dp, LandGreenPrimary),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = LandGreenDark),
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(vertical = 10.dp)
                            ) {
                                Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("গ্যালারি", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            // Sample Button
                            OutlinedButton(
                                onClick = { showSampleDialog = true },
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.2.dp, GoldAccent),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF854D0E)),
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(vertical = 10.dp)
                            ) {
                                Text("📋 স্যাম্পল", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Image Preview Card (if image chosen)
            if (selectedBitmap != null) {
                item {
                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.2.dp, LandGreenLight),
                        shadowElevation = 2.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = LandGreenPrimary, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        selectedImageName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = LandGreenDark
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        selectedBitmap = null
                                        generatedReport = null
                                        generatedPdfFile = null
                                    },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "Remove", tint = ErrorRed, modifier = Modifier.size(18.dp))
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFF1E293B)),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    bitmap = selectedBitmap!!.asImageBitmap(),
                                    contentDescription = "Scanned Land Document",
                                    modifier = Modifier.fillMaxSize()
                                )

                                // Scanning Beam Indicator Overlay if scanning
                                if (isScanning) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(Color.Black.copy(alpha = 0.5f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            CircularProgressIndicator(color = GoldAccent, modifier = Modifier.size(36.dp))
                                            Spacer(modifier = Modifier.height(10.dp))
                                            Text(
                                                "Gemini AI দলিল ও খতিয়ান নিরীক্ষা করছে...",
                                                color = Color.White,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Options: Language & Analysis Focus
            item {
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, DividerColor),
                    shadowElevation = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            "২. ভাষা ও অডিট বিশ্লেষণের ধরণ",
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = LandGreenDark
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        // Language Toggle
                        Text("রিপোর্টের ভাষা:", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Surface(
                                color = if (selectedLanguage == "bn") LandGreenPrimary else SurfaceLight,
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, if (selectedLanguage == "bn") LandGreenPrimary else DividerColor),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { selectedLanguage = "bn" }
                            ) {
                                Row(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("🇧🇩 বাংলা (Bengali)", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = if (selectedLanguage == "bn") Color.White else TextPrimary)
                                }
                            }

                            Surface(
                                color = if (selectedLanguage == "en") LandGreenPrimary else SurfaceLight,
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, if (selectedLanguage == "en") LandGreenPrimary else DividerColor),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { selectedLanguage = "en" }
                            ) {
                                Row(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("🇬🇧 English (ইংরেজি)", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = if (selectedLanguage == "en") Color.White else TextPrimary)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Analysis Type Selector
                        Text("বিশ্লেষণের ফোকাস:", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                        Spacer(modifier = Modifier.height(6.dp))
                        ScanAnalysisType.values().forEach { type ->
                            val isSelected = selectedAnalysisType == type
                            Surface(
                                color = if (isSelected) LandGreenContainer else SurfaceLight,
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, if (isSelected) LandGreenPrimary else DividerColor),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp)
                                    .clickable { selectedAnalysisType = type }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(type.emoji, fontSize = 16.sp)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            if (selectedLanguage == "en") type.titleEn else type.titleBn,
                                            fontSize = 11.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) LandGreenDark else TextPrimary
                                        )
                                        Text(
                                            type.descBn,
                                            fontSize = 9.5.sp,
                                            color = TextSecondary
                                        )
                                    }
                                    if (isSelected) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = LandGreenPrimary, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Optional custom query
                        OutlinedTextField(
                            value = userCustomQuery,
                            onValueChange = { userCustomQuery = it },
                            placeholder = { Text("বিশেষ কোনো জিজ্ঞাসা বা নোট থাকলে লিখুন (ঐচ্ছিক)", fontSize = 11.5.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            textStyle = TextStyle(color = Color.Black, fontSize = 13.sp, fontWeight = FontWeight.Medium),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black,
                                cursorColor = Color.Black,
                                focusedBorderColor = LandGreenPrimary,
                                unfocusedBorderColor = DividerColor,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White
                            )
                        )
                    }
                }
            }

            // Start Scan Button
            item {
                Button(
                    onClick = { runAiAnalysis() },
                    enabled = selectedBitmap != null && !isScanning,
                    colors = ButtonDefaults.buttonColors(containerColor = LandGreenPrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    if (isScanning) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("এআই ভিশন স্ক্যান চলছে...", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    } else {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp), tint = GoldAccent)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("এআই দিয়ে স্ক্যান ও বিশ্লেষণ শুরু করুন", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Generated Report Display Card
            if (generatedReport != null) {
                item {
                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.5.dp, GoldAccent),
                        shadowElevation = 3.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            // Header of report card
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("🏛️", fontSize = 20.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            "আইনগত নিরীক্ষা রিপোর্ট",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.5.sp,
                                            color = LandGreenDark
                                        )
                                        Text(
                                            "AI Legal Audit • 100% Verified",
                                            fontSize = 9.5.sp,
                                            color = Color(0xFF854D0E)
                                        )
                                    }
                                }

                                Row {
                                    // Voice TTS Button
                                    IconButton(
                                        onClick = { toggleTts() },
                                        modifier = Modifier.size(34.dp)
                                    ) {
                                        Icon(
                                            if (isSpeaking) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                                            contentDescription = "Speak Report",
                                            tint = if (isSpeaking) ErrorRed else LandGreenPrimary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }

                                    // Copy Report Button
                                    IconButton(
                                        onClick = {
                                            clipboardManager.setText(AnnotatedString(generatedReport!!))
                                            Toast.makeText(context, "রিপোর্ট কপি করা হয়েছে!", Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier.size(34.dp)
                                    ) {
                                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = LandGreenPrimary, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }

                            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = DividerColor)

                            // Grounding Legal Reference Badges
                            Text("তথ্য ও আইনি রেফারেন্স সূত্র:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                listOf(
                                    "রেজিস্ট্রেশন আইন ১৯০৮",
                                    "ভূমি অপরাধ আইন ২০২৩",
                                    "প্রজাস্বত্ব আইন ১৯৫০",
                                    "খতিয়ান নির্দেশিকা",
                                    "ভূমি কর ও দাখিলা",
                                    "১৬১২২ হেল্পলাইন"
                                ).forEach { tag ->
                                    Surface(
                                        color = EmeraldTint,
                                        shape = RoundedCornerShape(6.dp),
                                        border = BorderStroke(0.8.dp, LandGreenLight.copy(alpha = 0.5f))
                                    ) {
                                        Text(
                                            "🌐 $tag",
                                            fontSize = 9.sp,
                                            color = LandGreenDark,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Markdown Report Text Box
                            Surface(
                                color = SurfaceLight,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = generatedReport!!,
                                    fontSize = 11.5.sp,
                                    color = TextPrimary,
                                    lineHeight = 17.sp,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // PDF Download & Action Bar
                            if (generatedPdfFile == null) {
                                Button(
                                    onClick = { generatePdfReport() },
                                    enabled = !isGeneratingPdf,
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F766E)),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    if (isGeneratingPdf) {
                                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("PDF তৈরি হচ্ছে...", fontSize = 12.sp)
                                    } else {
                                        Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(18.dp), tint = GoldAccent)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("A4 PDF অডিট রিপোর্ট ডাউনলোড করুন", fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            } else {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Surface(
                                        color = AmberTint,
                                        shape = RoundedCornerShape(10.dp),
                                        border = BorderStroke(1.dp, GoldAccent),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(10.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = LandGreenPrimary, modifier = Modifier.size(20.dp))
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text("A4 PDF অডিট প্রস্তুত সম্পন্ন!", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = LandGreenDark)
                                                Text(generatedPdfFile!!.name, fontSize = 9.5.sp, color = TextSecondary)
                                            }
                                        }
                                    }

                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Button(
                                            onClick = { openPdf(generatedPdfFile!!) },
                                            colors = ButtonDefaults.buttonColors(containerColor = LandGreenPrimary),
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("PDF ওপেন", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                                        }

                                        OutlinedButton(
                                            onClick = { sharePdf(generatedPdfFile!!) },
                                            shape = RoundedCornerShape(10.dp),
                                            border = BorderStroke(1.2.dp, LandGreenPrimary),
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = LandGreenDark),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("PDF শেয়ার", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Legal & Citizen Guidance Card
            item {
                Surface(
                    color = Color(0xFFF8FAFC),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, DividerColor),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            "💡 ভূমি দলিল স্ক্যানিং টিপস:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.5.sp,
                            color = LandGreenDark
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "• দলিলের আলো ও ফোকাস স্পষ্ট রাখুন যাতে খতিয়ান ও দাগ নম্বর সহজে পাঠযোগ্য হয়।\n• সিএস, এসএ, আরএস ও বিএস খতিয়ান বা নামজারি ডিসিআর এর পুরো পাতা স্ক্যান করুন।\n• আইনি বিরোধ থাকলে অ্যাপের এআই সহকারী অথবা সরকারি কল সেন্টার ১৬১২২ এ যোগাযোগ করুন।",
                            fontSize = 10.sp,
                            color = TextSecondary,
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }
    }

    // Sample Document Selection Dialog
    if (showSampleDialog) {
        AlertDialog(
            onDismissRequest = { showSampleDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("📋", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("পরীক্ষার জন্য স্যাম্পল দলিল বেছে নিন", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    DocSampleType.values().forEach { sample ->
                        Surface(
                            color = SurfaceLight,
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, DividerColor),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    showSampleDialog = false
                                    loadSampleDocument(sample)
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(sample.emoji, fontSize = 20.sp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(sample.title, fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = LandGreenDark)
                                    Text(sample.subtitle, fontSize = 9.5.sp, color = TextSecondary)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSampleDialog = false }) {
                    Text("বন্ধ করুন", color = LandGreenPrimary, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    // AI Assistant Modal
    if (showAiAssistant) {
        AiLandAssistantModal(
            initialCategory = AiCategory.SCANNER,
            onDismiss = { showAiAssistant = false }
        )
    }
}
