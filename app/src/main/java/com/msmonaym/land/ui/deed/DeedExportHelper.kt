package com.msmonaym.land.ui.deed

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.print.PrintAttributes
import android.print.PrintManager
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

object DeedExportHelper {

    fun copyToClipboard(context: Context, text: String, label: String = "দলিলের খসড়া") {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard?.setPrimaryClip(clip)
        Toast.makeText(context, "দলিলের বয়ান সফলভাবে কপি করা হয়েছে!", Toast.LENGTH_SHORT).show()
    }

    /**
     * Exports the deed text formatted as an HTML Word Document (.doc)
     * which opens seamlessly in Microsoft Word, Google Docs, and WPS Office.
     */
    fun shareAsWordDocument(
        context: Context,
        title: String,
        deedText: String,
        hasStampMargin: Boolean
    ) {
        try {
            val fileName = "দলিল_${System.currentTimeMillis()}.doc"
            val file = File(context.cacheDir, fileName)

            val stampTopMarginHtml = if (hasStampMargin) {
                """
                <div style="height: 3.5in; border-bottom: 2px dashed #999; margin-bottom: 20px; text-align: center; color: #888; font-size: 12pt; padding-top: 50px;">
                    [ এই ৩.৫ ইঞ্চি ফাঁকা স্থানটিতে নন-জুডিশিয়াল স্ট্যাম্প সংযোজন বা প্রিন্টের জন্য নির্ধারিত ]
                </div>
                """.trimIndent()
            } else ""

            val htmlContent = """
                <!DOCTYPE html>
                <html xmlns:o='urn:schemas-microsoft-com:office:office' xmlns:w='urn:schemas-microsoft-com:office:word' xmlns='http://www.w3.org/TR/REC-html40'>
                <head>
                    <meta charset="utf-8">
                    <title>$title</title>
                    <!--[if gte mso 9]>
                    <xml>
                    <w:WordDocument>
                    <w:View>Print</w:View>
                    <w:Zoom>100</w:Zoom>
                    <w:DoNotOptimizeForBrowser/>
                    </w:WordDocument>
                    </xml>
                    <![endif]-->
                    <style>
                        @page {
                            size: A4;
                            margin: 1in 0.8in 1in 0.8in;
                            mso-header-margin: 0.5in;
                            mso-footer-margin: 0.5in;
                        }
                        body {
                            font-family: 'SolaimanLipi', 'Nikosh', 'Kalpurush', 'SutonnyMJ', 'Arial', sans-serif;
                            font-size: 13pt;
                            line-height: 1.6;
                            color: #111111;
                            background-color: #ffffff;
                            text-align: justify;
                        }
                        h1, h2 {
                            text-align: center;
                            font-weight: bold;
                            color: #064E3B;
                        }
                        pre {
                            font-family: 'SolaimanLipi', 'Nikosh', 'Kalpurush', sans-serif;
                            white-space: pre-wrap;
                            word-wrap: break-word;
                            font-size: 12.5pt;
                            line-height: 1.65;
                        }
                        .footer {
                            margin-top: 40px;
                            border-top: 1px solid #ddd;
                            font-size: 10pt;
                            text-align: center;
                            color: #666;
                        }
                    </style>
                </head>
                <body>
                    $stampTopMarginHtml
                    <pre>$deedText</pre>
                    <div class="footer">
                        ডিজিটাল বাংলা দলিল লেখক ও ওয়ার্ড এডিটর | ভূমি সেবা প্ল্যাটফর্ম
                    </div>
                </body>
                </html>
            """.trimIndent()

            FileOutputStream(file).use { out ->
                out.write(htmlContent.toByteArray(Charsets.UTF_8))
            }

            val uri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/msword"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, title)
                putExtra(Intent.EXTRA_TEXT, "বাংলা ডিজিটাল দলিল ফরম্যাট ফাইল (.doc - Microsoft Word / Google Docs উপযোগী)")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            context.startActivity(Intent.createChooser(shareIntent, "Word ফাইল (.doc) হিসেবে শেয়ার / ওপেন করুন"))
        } catch (e: Exception) {
            e.printStackTrace()
            // Fallback to plain text share
            val textShareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, title)
                putExtra(Intent.EXTRA_TEXT, deedText)
            }
            context.startActivity(Intent.createChooser(textShareIntent, "দলিলের খসড়া শেয়ার করুন"))
        }
    }

    /**
     * Prints the deed text directly using Android PrintManager.
     * Can save as PDF or print on connected/wireless printers.
     */
    fun printDeed(context: Context, title: String, deedText: String, hasStampMargin: Boolean) {
        val webView = WebView(context)
        val stampTopMargin = if (hasStampMargin) "margin-top: 180px;" else ""

        val htmlContent = """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="utf-8">
                <style>
                    @page { size: A4; margin: 20mm 15mm 20mm 15mm; }
                    body {
                        font-family: sans-serif;
                        font-size: 13.5pt;
                        line-height: 1.6;
                        color: #000;
                        text-align: justify;
                        $stampTopMargin
                    }
                    pre {
                        font-family: inherit;
                        white-space: pre-wrap;
                        word-wrap: break-word;
                    }
                </style>
            </head>
            <body>
                <pre>$deedText</pre>
            </body>
            </html>
        """.trimIndent()

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                val printManager = context.getSystemService(Context.PRINT_SERVICE) as? PrintManager
                val printAdapter = webView.createPrintDocumentAdapter("দলিল_${System.currentTimeMillis()}")
                val printAttributes = PrintAttributes.Builder()
                    .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                    .setResolution(PrintAttributes.Resolution("res1", "default", 300, 300))
                    .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
                    .build()
                printManager?.print(title, printAdapter, printAttributes)
            }
        }

        webView.loadDataWithBaseURL(null, htmlContent, "text/html", "utf-8", null)
    }

    /**
     * Local storage persistence for deed drafts using SharedPreferences.
     */
    fun saveDraftLocally(context: Context, draftTitle: String, content: String): Boolean {
        return try {
            val prefs = context.getSharedPreferences("deed_writer_drafts", Context.MODE_PRIVATE)
            val id = "draft_${System.currentTimeMillis()}"
            val listString = prefs.getString("saved_draft_ids", "") ?: ""
            val newIds = if (listString.isEmpty()) id else "$id,$listString"
            prefs.edit()
                .putString("saved_draft_ids", newIds)
                .putString("draft_title_$id", draftTitle)
                .putString("draft_content_$id", content)
                .putLong("draft_time_$id", System.currentTimeMillis())
                .apply()
            Toast.makeText(context, "খসড়া সফলভাবে সংরক্ষিত হয়েছে!", Toast.LENGTH_SHORT).show()
            true
        } catch (e: Exception) {
            false
        }
    }

    fun getSavedDrafts(context: Context): List<SavedDeedDraft> {
        val prefs = context.getSharedPreferences("deed_writer_drafts", Context.MODE_PRIVATE)
        val listString = prefs.getString("saved_draft_ids", "") ?: ""
        if (listString.isEmpty()) return emptyList()

        val ids = listString.split(",").filter { it.isNotBlank() }
        val drafts = mutableListOf<SavedDeedDraft>()
        for (id in ids) {
            val title = prefs.getString("draft_title_$id", "অজ্ঞাত দলিল") ?: "অজ্ঞাত দলিল"
            val content = prefs.getString("draft_content_$id", "") ?: ""
            val time = prefs.getLong("draft_time_$id", System.currentTimeMillis())
            drafts.add(
                SavedDeedDraft(
                    id = id,
                    title = title,
                    categoryId = "",
                    lastUpdated = time,
                    textContent = content
                )
            )
        }
        return drafts
    }

    fun deleteDraft(context: Context, draftId: String) {
        val prefs = context.getSharedPreferences("deed_writer_drafts", Context.MODE_PRIVATE)
        val listString = prefs.getString("saved_draft_ids", "") ?: ""
        val remaining = listString.split(",").filter { it.isNotBlank() && it != draftId }
        prefs.edit()
            .putString("saved_draft_ids", remaining.joinToString(","))
            .remove("draft_title_$draftId")
            .remove("draft_content_$draftId")
            .remove("draft_time_$draftId")
            .apply()
        Toast.makeText(context, "খসড়া মুছে ফেলা হয়েছে", Toast.LENGTH_SHORT).show()
    }
}
