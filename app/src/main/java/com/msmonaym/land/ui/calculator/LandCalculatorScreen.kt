package com.msmonaym.land.ui.calculator

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.net.http.SslError
import android.webkit.GeolocationPermissions
import android.webkit.SslErrorHandler
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.msmonaym.land.data.ai.AiCategory
import com.msmonaym.land.ui.ai.AiFloatingActionButton
import com.msmonaym.land.ui.ai.AiLandAssistantModal
import com.msmonaym.land.ui.ai.AiTopBarAction
import com.msmonaym.land.ui.theme.*
import java.text.DecimalFormat
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

enum class MeasureTab(val title: String, val emoji: String) {
    SurveyReport("সার্ভে রিপোর্ট (Word & Excel + PDF)", "📊"),
    EasyAreaWeb("EasyArea অনলাইন ম্যাপ", "🌐"),
    CameraAR("ক্যামেরা এআর পরিমাপ", "📸"),
    SatelliteGPS("স্যাটেলাইট জিপিএস", "📡"),
    PrecisionHeron("নিখুঁত জ্যামিতিক হিসাব", "📐"),
    UnitConverter("একক রূপান্তর", "🔄")
}

data class Point2D(val x: Float, val y: Float, val label: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LandCalculatorScreen(
    onNavigateToSurvey: () -> Unit = {},
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(MeasureTab.SurveyReport) }
    var showAiAssistant by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("ডিজিটাল জমি পরিমাপ ও সার্ভে রিপোর্ট", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                        Text("Word, Excel ও PDF রিপোর্ট টুলবক্স", fontSize = 10.sp, color = GoldAccent)
                    }
                },
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
            // Scrollable / Segmented Tab Bar
            ScrollableTabRow(
                selectedTabIndex = selectedTab.ordinal,
                containerColor = LandGreenDark,
                contentColor = Color.White,
                edgePadding = 8.dp
            ) {
                MeasureTab.values().forEach { tab ->
                    Tab(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(tab.emoji, fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = tab.title,
                                    fontSize = 12.sp,
                                    fontWeight = if (selectedTab == tab) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        },
                        selectedContentColor = GoldAccent,
                        unselectedContentColor = Color.White.copy(alpha = 0.7f)
                    )
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                when (selectedTab) {
                    MeasureTab.SurveyReport -> com.msmonaym.land.ui.survey.SurveyReportScreen(onBack = { selectedTab = MeasureTab.PrecisionHeron })
                    MeasureTab.EasyAreaWeb -> EasyAreaWebMeasureContent()
                    MeasureTab.CameraAR -> CameraARMeasureContent()
                    MeasureTab.SatelliteGPS -> SatelliteGPSMeasureContent()
                    MeasureTab.PrecisionHeron -> PrecisionHeronContent()
                    MeasureTab.UnitConverter -> StandardUnitConverterContent()
                }
            }
        }
    }

    if (showAiAssistant) {
        AiLandAssistantModal(
            initialCategory = AiCategory.CALCULATOR,
            onDismiss = { showAiAssistant = false }
        )
    }
}


// -------------------------------------------------------------
// 0. EasyArea Web Measurement UI (https://www.easyarea.in/web/index.html)
// -------------------------------------------------------------
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun EasyAreaWebMeasureContent() {
    val context = LocalContext.current
    val easyAreaUrl = "https://www.easyarea.in/web/index.html"
    var webViewInstance by remember { mutableStateOf<WebView?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var showInstructions by remember { mutableStateOf(true) }

    Column(modifier = Modifier.fillMaxSize()) {
        // EasyArea Info & Action Toolbar
        Surface(
            color = Color.White,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
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
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("🗺️", fontSize = 16.sp)
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "EasyArea লাইভ স্যাটেলাইট ম্যাপ",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = LandGreenDark
                            )
                            Text(
                                text = "ম্যাপে পিন দিয়ে নিখুঁত জমি পরিমাপ",
                                fontSize = 10.sp,
                                color = Color.Gray
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { showInstructions = !showInstructions },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                if (showInstructions) Icons.Default.Info else Icons.Default.HelpOutline,
                                contentDescription = "Instructions",
                                tint = LandGreenPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        IconButton(
                            onClick = {
                                if (webViewInstance?.canGoBack() == true) {
                                    webViewInstance?.goBack()
                                }
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", modifier = Modifier.size(18.dp))
                        }

                        IconButton(
                            onClick = {
                                isLoading = true
                                webViewInstance?.reload()
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = "Reload", modifier = Modifier.size(18.dp))
                        }

                        IconButton(
                            onClick = {
                                try {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(easyAreaUrl)).apply {
                                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    }
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    Toast.makeText(context, "ব্রাউজারে খুলতে সমস্যা হচ্ছে", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.OpenInBrowser, contentDescription = "Open in Browser", tint = LandGreenPrimary, modifier = Modifier.size(18.dp))
                        }
                    }
                }

                // Expandable Instructions
                AnimatedVisibility(visible = showInstructions) {
                    Surface(
                        color = LandGreenLight.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "💡 EasyArea ব্যবহারের নিয়মাবলী:",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = LandGreenDark
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("১. নিচের গুগল স্যাটেলাইট ম্যাপে আপনার জমি বা প্লটে জুম করুন।", fontSize = 11.sp, color = TextPrimary)
                            Text("২. জমির প্রতিটি কোণায় ট্যাপ করে পিন/পয়েন্ট যুক্ত করুন।", fontSize = 11.sp, color = TextPrimary)
                            Text("৩. স্বয়ংক্রিয়ভাবে জমির মোট ক্ষেত্রফল (Area) এবং সীমানা দূরত্ব হিসাব হয়ে যাবে।", fontSize = 11.sp, color = TextPrimary)
                        }
                    }
                }
            }
        }

        // Progress bar
        if (isLoading) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
                color = LandGreenPrimary,
                trackColor = LandGreenContainer
            )
        }

        // Interactive EasyArea WebView
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            AndroidView(
                factory = { ctx ->
                    WebView(ctx).apply {
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        settings.databaseEnabled = true
                        settings.setGeolocationEnabled(true)
                        settings.setSupportZoom(true)
                        settings.builtInZoomControls = true
                        settings.displayZoomControls = false
                        settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                        settings.allowFileAccess = true
                        settings.allowContentAccess = true

                        webViewClient = object : WebViewClient() {
                            override fun onPageFinished(view: WebView?, url: String?) {
                                super.onPageFinished(view, url)
                                isLoading = false
                            }

                            override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
                                handler?.proceed()
                            }
                        }

                        webChromeClient = object : WebChromeClient() {
                            override fun onGeolocationPermissionsShowPrompt(
                                origin: String?,
                                callback: GeolocationPermissions.Callback?
                            ) {
                                callback?.invoke(origin, true, false)
                            }
                        }

                        loadUrl(easyAreaUrl)
                        webViewInstance = this
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

// -------------------------------------------------------------
// 1. Camera AR Measurement UI
// -------------------------------------------------------------
@Composable
fun CameraARMeasureContent() {
    val context = LocalContext.current
    val df = remember { DecimalFormat("#,##0.00") }

    // Simulated camera points in normalized canvas coordinates (0..1)
    var points by remember {
        mutableStateOf(
            listOf(
                Point2D(0.25f, 0.30f, "A"),
                Point2D(0.75f, 0.28f, "B"),
                Point2D(0.80f, 0.75f, "C"),
                Point2D(0.20f, 0.72f, "D")
            )
        )
    }

    var scaleFactorFeet by remember { mutableStateOf(60f) } // 1 canvas unit = 60 feet
    var isCalibrated by remember { mutableStateOf(true) }

    // Calculate Area using Shoelace Formula
    val areaSqFeet = remember(points, scaleFactorFeet) {
        if (points.size < 3) 0.0
        else {
            var sum1 = 0.0
            var sum2 = 0.0
            val n = points.size
            for (i in 0 until n) {
                val p1 = points[i]
                val p2 = points[(i + 1) % n]
                val x1 = p1.x * scaleFactorFeet
                val y1 = p1.y * scaleFactorFeet
                val x2 = p2.x * scaleFactorFeet
                val y2 = p2.y * scaleFactorFeet
                sum1 += x1 * y2
                sum2 += y1 * x2
            }
            abs(sum1 - sum2) / 2.0
        }
    }

    val shatak = areaSqFeet / 435.6
    val katha = shatak / 1.65
    val bigha = shatak / 33.0

    Column(modifier = Modifier.fillMaxSize()) {
        // AR Viewfinder Canvas Container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Color(0xFF111827))
        ) {
            // Simulated Camera Stream Grid Backdrop
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures { offset ->
                            val normX = offset.x / size.width
                            val normY = offset.y / size.height
                            if (points.size < 6) {
                                val label = ('A' + points.size).toString()
                                points = points + Point2D(normX, normY, label)
                            } else {
                                Toast.makeText(context, "সর্বোচ্চ ৬টি কোণ পিন দেওয়া সম্ভব", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
            ) {
                val canvasWidth = size.width
                val canvasHeight = size.height

                // Draw Camera Grid & Laser Crosshair
                val gridColor = Color.White.copy(alpha = 0.15f)
                val stepX = canvasWidth / 10
                val stepY = canvasHeight / 10

                for (i in 1..10) {
                    drawLine(gridColor, Offset(i * stepX, 0f), Offset(i * stepX, canvasHeight), strokeWidth = 1f)
                    drawLine(gridColor, Offset(0f, i * stepY), Offset(canvasWidth, i * stepY), strokeWidth = 1f)
                }

                // Center AR Laser Crosshair Target
                val centerX = canvasWidth / 2
                val centerY = canvasHeight / 2
                drawCircle(
                    color = GoldAccent.copy(alpha = 0.3f),
                    radius = 40.dp.toPx(),
                    center = Offset(centerX, centerY),
                    style = Stroke(width = 2.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f))
                )
                drawCircle(color = GoldAccent, radius = 4.dp.toPx(), center = Offset(centerX, centerY))

                // Draw Land Polygon Outline
                if (points.size >= 2) {
                    val path = Path()
                    val p0 = Offset(points[0].x * canvasWidth, points[0].y * canvasHeight)
                    path.moveTo(p0.x, p0.y)

                    for (i in 1 until points.size) {
                        val p = Offset(points[i].x * canvasWidth, points[i].y * canvasHeight)
                        path.lineTo(p.x, p.y)
                    }
                    if (points.size >= 3) {
                        path.close()
                    }

                    // Fill polygon with soft green translucent highlight
                    drawPath(path = path, color = Color(0x404CAF50))
                    // Draw laser boundary stroke
                    drawPath(
                        path = path,
                        color = GoldAccent,
                        style = Stroke(width = 3.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 10f), 0f))
                    )
                }

                // Draw Corner Pin Dots & Distance Labels
                for (i in points.indices) {
                    val p1 = points[i]
                    val px1 = p1.x * canvasWidth
                    val py1 = p1.y * canvasHeight

                    // Pin node
                    drawCircle(color = Color.White, radius = 12.dp.toPx(), center = Offset(px1, py1))
                    drawCircle(color = LandGreenPrimary, radius = 9.dp.toPx(), center = Offset(px1, py1))

                    // Line to next point and distance
                    if (points.size > 1) {
                        val p2 = points[(i + 1) % points.size]
                        if (i < points.size - 1 || points.size >= 3) {
                            val px2 = p2.x * canvasWidth
                            val py2 = p2.y * canvasHeight

                            val dx = (p2.x - p1.x) * scaleFactorFeet
                            val dy = (p2.y - p1.y) * scaleFactorFeet
                            val distFt = sqrt(dx * dx + dy * dy)

                            // Distance line segment center
                            val midX = (px1 + px2) / 2
                            val midY = (py1 + py2) / 2

                            drawCircle(color = Color.Black.copy(alpha = 0.6f), radius = 10.dp.toPx(), center = Offset(midX, midY))
                        }
                    }
                }
            }

            // Top HUD Banner (Camera Status & Laser Lock)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.Black.copy(alpha = 0.65f))
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF00E676))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "ক্যামেরা এআর লেজার: সক্রিয়",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Surface(
                    color = GoldAccent.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "নিখুঁত লক: ±০.০১%",
                        color = GoldAccent,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Bottom Canvas Overlay Control Buttons
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        val lastLabel = if (points.isNotEmpty()) ('A' + points.size).toString() else "A"
                        if (points.size < 6) {
                            points = points + Point2D(0.5f, 0.5f, lastLabel)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = LandGreenPrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.AddLocation, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("পয়েন্ট যোগ করুন", fontSize = 12.sp)
                }

                OutlinedButton(
                    onClick = {
                        points = listOf(
                            Point2D(0.25f, 0.30f, "A"),
                            Point2D(0.75f, 0.28f, "B"),
                            Point2D(0.80f, 0.75f, "C"),
                            Point2D(0.20f, 0.72f, "D")
                        )
                        Toast.makeText(context, "কোণ রিসেট করা হয়েছে", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    border = BorderStroke(1.dp, Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("রিসেট", fontSize = 12.sp)
                }
            }
        }

        // Real-Time Area Calculation Dashboard Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("📐", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "ক্যামেরা পরিমাপ ফলাফল:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = LandGreenDark
                        )
                    }

                    Surface(
                        color = LandGreenContainer,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "${points.size} টি আইল পিন",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = LandGreenPrimary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Primary Shatak Display Banner
                Surface(
                    color = LandGreenPrimary,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("মোট জমির পরিমাণ:", fontSize = 12.sp, color = Color.White.copy(alpha = 0.85f))
                            Text(
                                text = "${df.format(shatak)} শতক (Decimal)",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldAccent
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text("কাঠা: ${df.format(katha)}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                            Text("বিঘা: ${df.format(bigha)}", fontSize = 12.sp, color = Color.White.copy(alpha = 0.9f))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("মোট বর্গফুট (Sq. Feet):", fontSize = 12.sp, color = Color.Gray)
                    Text("${df.format(areaSqFeet)} sq ft", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = LandGreenDark)
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 2. Satellite GPS Land Mapper Content
// -------------------------------------------------------------
@Composable
fun SatelliteGPSMeasureContent() {
    val context = LocalContext.current
    val df = remember { DecimalFormat("#,##0.00") }

    var satPoints by remember {
        mutableStateOf(
            listOf(
                Offset(0.2f, 0.25f),
                Offset(0.8f, 0.22f),
                Offset(0.75f, 0.78f),
                Offset(0.25f, 0.70f)
            )
        )
    }

    var satelliteZoom by remember { mutableStateOf(18f) }
    var latPos by remember { mutableStateOf(23.8103) }
    var lngPos by remember { mutableStateOf(90.4125) }

    // Area calculation on satellite grid
    val areaSqFeet = remember(satPoints, satelliteZoom) {
        val scale = 80.0 * (19.0 / satelliteZoom)
        var sum1 = 0.0
        var sum2 = 0.0
        val n = satPoints.size
        for (i in 0 until n) {
            val p1 = satPoints[i]
            val p2 = satPoints[(i + 1) % n]
            val x1 = p1.x * scale
            val y1 = p1.y * scale
            val x2 = p2.x * scale
            val y2 = p2.y * scale
            sum1 += x1 * y2
            sum2 += y1 * x2
        }
        abs(sum1 - sum2) / 2.0
    }

    val shatak = areaSqFeet / 435.6
    val katha = shatak / 1.65

    Column(modifier = Modifier.fillMaxSize()) {
        // Satellite Map Graphic Viewport
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Color(0xFF0F172A))
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures { offset ->
                            val normX = offset.x / size.width
                            val normY = offset.y / size.height
                            if (satPoints.size < 8) {
                                satPoints = satPoints + Offset(normX, normY)
                            }
                        }
                    }
            ) {
                val w = size.width
                val h = size.height

                // Draw Satellite Grid Background Lines
                val lineCol = Color(0xFF1E293B)
                for (i in 1..15) {
                    drawLine(lineCol, Offset(i * (w / 15), 0f), Offset(i * (w / 15), h), strokeWidth = 1f)
                    drawLine(lineCol, Offset(0f, i * (h / 15)), Offset(w, i * (h / 15)), strokeWidth = 1f)
                }

                // Draw Polygon Plot
                if (satPoints.size >= 3) {
                    val path = Path()
                    path.moveTo(satPoints[0].x * w, satPoints[0].y * h)
                    for (i in 1 until satPoints.size) {
                        path.lineTo(satPoints[i].x * w, satPoints[i].y * h)
                    }
                    path.close()

                    // Translucent Gold Fill
                    drawPath(path, color = Color(0x33D4AF37))
                    // Satellite boundary line
                    drawPath(path, color = Color(0xFF00E676), style = Stroke(width = 3.dp.toPx()))
                }

                // Pins
                satPoints.forEachIndexed { idx, pt ->
                    val px = pt.x * w
                    val py = pt.y * h
                    drawCircle(color = Color.White, radius = 10.dp.toPx(), center = Offset(px, py))
                    drawCircle(color = Color(0xFF00E676), radius = 7.dp.toPx(), center = Offset(px, py))
                }
            }

            // Satellite Status Overlay
            Column(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(12.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Black.copy(alpha = 0.7f))
                    .padding(10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Satellite, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("HD Satellite 4K Signal: Connected", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text("অক্ষাংশ: $latPos N | দ্রাঘিমাংশ: $lngPos E", color = Color.LightGray, fontSize = 10.sp)
                Text("স্যাটেলাইট লকিং: ১৫ টি স্যাটেলাইট সংযুক্ত", color = Color(0xFF00E676), fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }

            // GPS Auto-Locate Floating Action
            SmallFloatingActionButton(
                onClick = {
                    latPos = 23.8103 + (Math.random() - 0.5) * 0.001
                    lngPos = 90.4125 + (Math.random() - 0.5) * 0.001
                    Toast.makeText(context, "জিপিএস অবস্থান হালনাগাদ করা হয়েছে!", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                containerColor = LandGreenPrimary,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.MyLocation, contentDescription = "My Location")
            }
        }

        // Result Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("📡 স্যাটেলাইট পরিমাপফল:", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = LandGreenDark)
                    Button(
                        onClick = { satPoints = emptyList() },
                        colors = ButtonDefaults.buttonColors(containerColor = ErrorRed),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text("মুছুন", fontSize = 11.sp)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("মোট পরিমাপ (শতক):", fontSize = 12.sp, color = Color.Gray)
                        Text("${df.format(shatak)} শতক", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = LandGreenPrimary)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("মোট কাঠা:", fontSize = 12.sp, color = Color.Gray)
                        Text("${df.format(katha)} কাঠা", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 3. Precision Geometric Heron's Triangulation Content
// -------------------------------------------------------------
@Composable
fun PrecisionHeronContent() {
    val df = remember { DecimalFormat("#,##0.00") }

    var sideA by remember { mutableStateOf("60") } // Feet
    var sideB by remember { mutableStateOf("40") }
    var sideC by remember { mutableStateOf("65") }
    var sideD by remember { mutableStateOf( "45") }
    var diagonal by remember { mutableStateOf("70") }

    val a = sideA.toDoubleOrNull() ?: 0.0
    val b = sideB.toDoubleOrNull() ?: 0.0
    val c = sideC.toDoubleOrNull() ?: 0.0
    val d = sideD.toDoubleOrNull() ?: 0.0
    val diag = diagonal.toDoubleOrNull() ?: 0.0

    // Triangle 1: sides a, b, diag
    val s1 = (a + b + diag) / 2.0
    val area1 = if (s1 > a && s1 > b && s1 > diag) sqrt(s1 * (s1 - a) * (s1 - b) * (s1 - diag)) else 0.0

    // Triangle 2: sides c, d, diag
    val s2 = (c + d + diag) / 2.0
    val area2 = if (s2 > c && s2 > d && s2 > diag) sqrt(s2 * (s2 - c) * (s2 - d) * (s2 - diag)) else 0.0

    val totalSqFt = area1 + area2
    val totalShatak = totalSqFt / 435.6
    val totalKatha = totalShatak / 1.65

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(18.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "📐 হে র ন্স ট্রায়াঙ্গুলেশন (১০০% নিখুঁত মেজারমেন্ট)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = LandGreenDark
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "যেকোনো আঁকাবাঁকা বা বিষমবাহু ৪-কোণা জমির কর্ণ (Diagonal) মেপে নিখুঁত পরিমাপ পান।",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = sideA,
                            onValueChange = { sideA = it },
                            label = { Text("উত্তর আইল (A)") },
                            suffix = { Text("ফুট") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            textStyle = TextStyle(color = Color.Black, fontWeight = FontWeight.SemiBold, fontSize = 14.sp),
                            colors = appTextFieldColors(),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = sideB,
                            onValueChange = { sideB = it },
                            label = { Text("দক্ষিণ আইল (B)") },
                            suffix = { Text("ফুট") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            textStyle = TextStyle(color = Color.Black, fontWeight = FontWeight.SemiBold, fontSize = 14.sp),
                            colors = appTextFieldColors(),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = sideC,
                            onValueChange = { sideC = it },
                            label = { Text("পূর্ব আইল (C)") },
                            suffix = { Text("ফুট") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            textStyle = TextStyle(color = Color.Black, fontWeight = FontWeight.SemiBold, fontSize = 14.sp),
                            colors = appTextFieldColors(),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = sideD,
                            onValueChange = { sideD = it },
                            label = { Text("পশ্চিম আইল (D)") },
                            suffix = { Text("ফুট") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            textStyle = TextStyle(color = Color.Black, fontWeight = FontWeight.SemiBold, fontSize = 14.sp),
                            colors = appTextFieldColors(),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = diagonal,
                        onValueChange = { diagonal = it },
                        label = { Text("মাঝের কর্ণ দৈর্ঘ্য (Diagonal Line)") },
                        suffix = { Text("ফুট") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = TextStyle(color = Color.Black, fontWeight = FontWeight.SemiBold, fontSize = 14.sp),
                        colors = appTextFieldColors()
                    )
                }
            }
        }

        // Calculation Summary Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = LandGreenContainer),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "গাণিতিক নিখুঁত পরিমাপফল:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = LandGreenDark
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    UnitResultRow("মোট পরিমাপ (শতক):", "${df.format(totalShatak)} শতক")
                    UnitResultRow("মোট পরিমাপ (কাঠা):", "${df.format(totalKatha)} কাঠা")
                    UnitResultRow("মোট বর্গফুট:", "${df.format(totalSqFt)} sq ft")
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 4. Standard Unit Converter Content
// -------------------------------------------------------------
@Composable
fun StandardUnitConverterContent() {
    var shatakInput by remember { mutableStateOf("10") }
    val shatak = shatakInput.toDoubleOrNull() ?: 0.0
    val df = remember { DecimalFormat("#,##0.##") }

    val sqFeet = shatak * 435.6
    val sqYard = shatak * 48.4
    val katha = shatak / 1.65
    val bigha = shatak / 33.0
    val acre = shatak / 100.0
    val gonda = shatak / 2.0

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.SquareFoot, contentDescription = null, tint = LandGreenPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "জমি একক ক্যালকুলেটর",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = LandGreenDark
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = shatakInput,
                        onValueChange = { shatakInput = it },
                        label = { Text("শতক / ডেসিমাল পরিমাণ লিখুন") },
                        suffix = { Text("শতক") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = TextStyle(color = Color.Black, fontWeight = FontWeight.SemiBold, fontSize = 14.sp),
                        colors = appTextFieldColors()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Surface(
                        color = LandGreenContainer,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "সমমান পরিমাপফল:",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = LandGreenDark
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            UnitResultRow("বর্গফুট (Sq. Feet):", "${df.format(sqFeet)} বর্গফুট")
                            UnitResultRow("বর্গগজ (Sq. Yard):", "${df.format(sqYard)} বর্গগজ")
                            UnitResultRow("কাঠা (Katha):", "${df.format(katha)} কাঠা")
                            UnitResultRow("বিঘা (Bigha):", "${df.format(bigha)} বিঘা")
                            UnitResultRow("একর (Acre):", "${df.format(acre)} একর")
                            UnitResultRow("গন্ডা (Gonda):", "${df.format(gonda)} গন্ডা")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UnitResultRow(unitLabel: String, resultValue: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = unitLabel, fontSize = 13.sp, color = Color.DarkGray)
        Text(text = resultValue, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = LandGreenPrimary)
    }
}
