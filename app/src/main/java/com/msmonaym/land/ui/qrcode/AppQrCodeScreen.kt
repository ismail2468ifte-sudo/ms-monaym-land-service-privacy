package com.msmonaym.land.ui.qrcode

import android.content.ClipData
import android.content.ClipboardManager
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.msmonaym.land.data.AdminManager
import com.msmonaym.land.ui.theme.*
import com.msmonaym.land.util.QrCodeGenerator
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppQrCodeScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val adminManager = remember { AdminManager(context) }
    val appUrl = remember { adminManager.getAppLaunchUrl() }

    val qrBitmap = remember(appUrl) {
        QrCodeGenerator.generateQrBitmap(appUrl, width = 700, height = 700)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "অ্যাপ কিউআর কোড (Direct Scan)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color.White
                        )
                        Text(
                            "স্ক্যান করলেই সরাসরি অ্যাপ চালু হবে",
                            fontSize = 10.sp,
                            color = GoldAccent
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            shareAppQrAndLink(context, appUrl)
                        }
                    ) {
                        Icon(Icons.Default.Share, contentDescription = "Share", tint = Color.White)
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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Main QR Code Poster Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Official Branding Header
                        Surface(
                            color = LandGreenPrimary.copy(alpha = 0.08f),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    "M.S MONAYM ENT.",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 17.sp,
                                    color = LandGreenDark
                                )
                                Text(
                                    "ডিজিটাল ভূমি সেবা ও সার্ভে ক্যালকুলেটর",
                                    fontSize = 11.sp,
                                    color = Color(0xFF0F766E),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // QR Code Render Box
                        Surface(
                            color = Color.White,
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(2.dp, LandGreenPrimary),
                            shadowElevation = 2.dp,
                            modifier = Modifier
                                .size(260.dp)
                                .padding(4.dp)
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                if (qrBitmap != null) {
                                    Image(
                                        bitmap = qrBitmap.asImageBitmap(),
                                        contentDescription = "App Access QR Code",
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(12.dp)
                                    )
                                } else {
                                    CircularProgressIndicator(color = LandGreenPrimary)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Surface(
                            color = Color(0xFFFEF3C7),
                            shape = RoundedCornerShape(20.dp),
                            border = BorderStroke(1.dp, GoldAccent)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("📸 ", fontSize = 13.sp)
                                Text(
                                    "যেকোনো ক্যামেরা দিয়ে স্ক্যান করুন",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = Color(0xFF92400E)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // URL Display Box
                        Surface(
                            color = Color(0xFFF8FAFC),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    copyToClipboard(context, appUrl)
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Link,
                                    contentDescription = null,
                                    tint = LandGreenPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = appUrl,
                                    fontSize = 10.5.sp,
                                    color = Color(0xFF334155),
                                    maxLines = 1,
                                    modifier = Modifier.weight(1f)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    Icons.Default.ContentCopy,
                                    contentDescription = "Copy",
                                    tint = LandGreenPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Quick Action Buttons
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Open in Browser Button
                    Button(
                        onClick = {
                            try {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(appUrl))
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "ব্রাউজারে ওপেন করা যায়নি", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = LandGreenPrimary)
                    ) {
                        Icon(Icons.Default.OpenInBrowser, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("অ্যাপে যান", fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
                    }

                    // Save / Download Image Button
                    Button(
                        onClick = {
                            if (qrBitmap != null) {
                                saveQrBitmapToGallery(context, qrBitmap)
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F766E))
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("QR সেভ করুন", fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
                    }
                }
            }

            // Secondary Actions (Share & Print)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            shareAppQrAndLink(context, appUrl)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.2.dp, LandGreenPrimary)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, tint = LandGreenPrimary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("শেয়ার করুন", color = LandGreenPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    OutlinedButton(
                        onClick = {
                            printQrPoster(context, appUrl)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.2.dp, Color(0xFF0F766E))
                    ) {
                        Icon(Icons.Default.Print, contentDescription = null, tint = Color(0xFF0F766E), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("প্রিন্ট করুন", color = Color(0xFF0F766E), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }

            // How to Scan Guide Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.QrCodeScanner, contentDescription = null, tint = LandGreenPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("কিউআর কোড স্ক্যান করার সহজ নিয়ম", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = LandGreenDark)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        val scanSteps = listOf(
                            "১. আপনার স্মার্টফোনের ক্যামেরা (Camera) অথবা Google Lens অ্যাপটি চালু করুন।",
                            "২. ক্যামেরাটি ওপরের কিউআর কোডটির সামনে ধরুন।",
                            "৩. স্ক্রিনে ভেসে ওঠা লিংকে (URL) একবার ট্যাপ করুন।",
                            "৪. সরাসরি M.S MONAYM ENT. ডিজিটাল ভূমি সেবা অ্যাপটি ওপেন হয়ে যাবে।"
                        )

                        scanSteps.forEach { step ->
                            Row(
                                modifier = Modifier.padding(vertical = 3.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Text(
                                    text = step,
                                    fontSize = 11.5.sp,
                                    color = Color(0xFF334155),
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }
            }

            // Printable Poster / Shop Banner Note
            item {
                Surface(
                    color = Color(0xFFF0FDF4),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, Color(0xFF86EFAC)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("💡", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "অফিস বা দোকানে প্রদর্শনের জন্য",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = Color(0xFF166534)
                            )
                            Text(
                                "আপনি 'প্রিন্ট করুন' বাটনে চাপ দিয়ে এই কিউআর কোডটি A4 সাইজে প্রিন্ট করে দোকানে বা সার্ভেয়ারের টেবিলে ঝুলিয়ে রাখতে পারেন। যে কেউ স্ক্যান করে সাথে সাথে সেবা নিতে পারবে।",
                                fontSize = 10.5.sp,
                                color = Color(0xFF15803D),
                                lineHeight = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun copyToClipboard(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("App URL", text)
    clipboard.setPrimaryClip(clip)
    Toast.makeText(context, "অ্যাপ লিংক কপি করা হয়েছে!", Toast.LENGTH_SHORT).show()
}

private fun shareAppQrAndLink(context: Context, appUrl: String) {
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "M.S MONAYM ENT. ডিজিটাল ভূমি সেবা অ্যাপ")
        putExtra(
            Intent.EXTRA_TEXT,
            "🏛️ M.S MONAYM ENT. ডিজিটাল ভূমি সেবা ও সার্ভে ক্যালকুলেটর অ্যাপ:\n" +
                    "খতিয়ান যাচাই, দাগের তথ্য, জমি পরিমাপ, সার্ভে রিপোর্ট ও রেজিস্ট্রেশন ফি জানতে নিচের লিংকে প্রবেশ করুন বা কিউআর কোড স্ক্যান করুন:\n\n" +
                    appUrl
        )
    }
    context.startActivity(Intent.createChooser(shareIntent, "অ্যাপ লিংক শেয়ার করুন"))
}

private fun saveQrBitmapToGallery(context: Context, bitmap: Bitmap) {
    try {
        val filename = "MS_Monaym_Land_App_QRCode_${System.currentTimeMillis()}.png"
        var fos: OutputStream? = null

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val resolver = context.contentResolver
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/LandServices")
            }
            val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            if (imageUri != null) {
                fos = resolver.openOutputStream(imageUri)
            }
        } else {
            val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/LandServices"
            val file = File(imagesDir)
            if (!file.exists()) file.mkdirs()
            val image = File(file, filename)
            fos = FileOutputStream(image)
        }

        fos?.use {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
            Toast.makeText(context, "✅ কিউআর কোড গ্যালারি/ছবিতে সেভ হয়েছে!", Toast.LENGTH_LONG).show()
        } ?: run {
            Toast.makeText(context, "সেভ করতে সমস্যা হয়েছে", Toast.LENGTH_SHORT).show()
        }
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "সেভ ব্যর্থ: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}

private fun printQrPoster(context: Context, appUrl: String) {
    try {
        val htmlContent = """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>M.S MONAYM ENT. অ্যাপ কিউআর কোড</title>
                <style>
                    body {
                        font-family: 'Segoe UI', Arial, sans-serif;
                        text-align: center;
                        padding: 30px;
                        margin: 0;
                        background: #ffffff;
                    }
                    .poster-card {
                        border: 4px double #1B4D3E;
                        border-radius: 20px;
                        padding: 30px;
                        max-width: 500px;
                        margin: 0 auto;
                        box-shadow: 0 4px 15px rgba(0,0,0,0.1);
                    }
                    .header {
                        color: #1B4D3E;
                        font-size: 26px;
                        font-weight: 800;
                        margin-bottom: 5px;
                    }
                    .sub-header {
                        color: #D4AF37;
                        font-size: 16px;
                        font-weight: bold;
                        margin-bottom: 20px;
                    }
                    .qr-container {
                        margin: 20px 0;
                        padding: 15px;
                        display: inline-block;
                        border: 2px solid #1B4D3E;
                        border-radius: 15px;
                    }
                    .qr-img {
                        width: 250px;
                        height: 250px;
                    }
                    .instruction {
                        font-size: 16px;
                        font-weight: bold;
                        color: #1E293B;
                        margin-top: 15px;
                    }
                    .url-box {
                        margin-top: 15px;
                        padding: 8px 12px;
                        background: #f1f5f9;
                        border-radius: 8px;
                        font-size: 12px;
                        word-break: break-all;
                        color: #475569;
                    }
                    .footer {
                        margin-top: 25px;
                        font-size: 13px;
                        color: #64748b;
                        border-top: 1px solid #e2e8f0;
                        padding-top: 10px;
                    }
                </style>
            </head>
            <body>
                <div class="poster-card">
                    <div class="header">🏛️ M.S MONAYM ENT.</div>
                    <div class="sub-header">ডিজিটাল ভূমি সেবা ও সার্ভে ক্যালকুলেটর</div>
                    
                    <div class="qr-container">
                        <img class="qr-img" src="https://api.qrserver.com/v1/create-qr-code/?size=300x300&data=${Uri.encode(appUrl)}" alt="App QR Code" />
                    </div>
                    
                    <div class="instruction">📲 মোবাইল ক্যামেরা দিয়ে স্ক্যান করে সরাসরি অ্যাপে প্রবেশ করুন</div>
                    
                    <div class="url-box">${appUrl}</div>
                    
                    <div class="footer">
                        খতিয়ান • পর্চা • নামজারি • জমি পরিমাপ • সার্ভে রিপোর্ট • দলিল ফি
                    </div>
                </div>
            </body>
            </html>
        """.trimIndent()

        val webView = WebView(context)
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                val printManager = context.getSystemService(Context.PRINT_SERVICE) as? android.print.PrintManager
                val printAdapter = webView.createPrintDocumentAdapter("MS_Monaym_App_QR_Poster")
                val printAttributes = android.print.PrintAttributes.Builder()
                    .setMediaSize(android.print.PrintAttributes.MediaSize.ISO_A4)
                    .setResolution(android.print.PrintAttributes.Resolution("res1", "default", 300, 300))
                    .setMinMargins(android.print.PrintAttributes.Margins.NO_MARGINS)
                    .build()
                printManager?.print("MS Monaym App QR Poster", printAdapter, printAttributes)
            }
        }
        webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "প্রিন্ট সেবা চালু করা যায়নি: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}
