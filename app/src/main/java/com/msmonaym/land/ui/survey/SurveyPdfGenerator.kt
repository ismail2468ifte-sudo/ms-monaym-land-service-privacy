package com.msmonaym.land.ui.survey

import android.content.Context
import android.graphics.*
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Environment
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintManager
import android.widget.Toast
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.DecimalFormat

object SurveyPdfGenerator {

    private val df = DecimalFormat("#,##0.00")

    /**
     * Creates an Official High-Quality A4 Land Survey Report PDF
     */
    fun generateSurveyPdf(context: Context, data: SurveyReportData): File? {
        return try {
            val pdfDocument = PdfDocument()
            val pageWidth = 595
            val pageHeight = 842 // Standard A4 size

            // Page 1
            val pageInfo1 = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
            val page1 = pdfDocument.startPage(pageInfo1)
            val canvas1 = page1.canvas

            drawPageContent(canvas1, data, pageWidth, pageHeight)

            pdfDocument.finishPage(page1)

            // Save PDF to App Documents folder
            val fileName = "Land_Survey_Report_${data.reportId.replace("-", "_")}.pdf"
            val outputDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) ?: context.filesDir
            val pdfFile = File(outputDir, fileName)
            
            val outputStream = FileOutputStream(pdfFile)
            pdfDocument.writeTo(outputStream)
            outputStream.flush()
            outputStream.close()
            pdfDocument.close()

            // Also attempt to copy to public Downloads folder if accessible
            try {
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                if (downloadsDir != null && downloadsDir.exists() && downloadsDir.canWrite()) {
                    val publicFile = File(downloadsDir, fileName)
                    val inStream = FileInputStream(pdfFile)
                    val outStream = FileOutputStream(publicFile)
                    inStream.copyTo(outStream)
                    inStream.close()
                    outStream.close()
                }
            } catch (ignored: Exception) {}

            pdfFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun drawPageContent(canvas: Canvas, data: SurveyReportData, width: Int, height: Int) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        // 1. Page Background
        paint.color = Color.WHITE
        paint.style = Paint.Style.FILL
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)

        // 2. Double Security Border (Green & Gold)
        paint.style = Paint.Style.STROKE
        paint.color = Color.rgb(11, 102, 35) // #0B6623
        paint.strokeWidth = 3f
        canvas.drawRect(20f, 20f, (width - 20).toFloat(), (height - 20).toFloat(), paint)

        paint.color = Color.rgb(212, 175, 55) // #D4AF37
        paint.strokeWidth = 1f
        canvas.drawRect(24f, 24f, (width - 24).toFloat(), (height - 24).toFloat(), paint)

        // 3. Top Banner & Organization Title
        paint.style = Paint.Style.FILL
        paint.color = Color.rgb(11, 102, 35)
        canvas.drawRoundRect(30f, 30f, (width - 30).toFloat(), 85f, 8f, 8f, paint)

        paint.color = Color.WHITE
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 15f
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText(data.organizationName, width / 2f, 54f, paint)

        paint.textSize = 10f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        paint.color = Color.rgb(254, 240, 138) // Gold Light
        canvas.drawText("ডিজিタル সীমানা পরিমাপ, অংশ বণ্টন ও পেশাদার সার্ভেয়ার সনদ", width / 2f, 72f, paint)

        // 4. Report Meta Bar
        var currentY = 104f
        paint.color = Color.rgb(241, 245, 249) // Light gray bg
        paint.style = Paint.Style.FILL
        canvas.drawRect(30f, currentY - 14f, (width - 30).toFloat(), currentY + 18f, paint)

        paint.color = Color.rgb(15, 23, 42)
        paint.textAlign = Paint.Align.LEFT
        paint.textSize = 9f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("স্মারক/রিপোর্ট আইডি: ${data.reportId}", 36f, currentY + 2f, paint)

        paint.textAlign = Paint.Align.RIGHT
        canvas.drawText("তারিখ: ${data.date} | সার্ভেয়ার: ${data.surveyorName}", (width - 36).toFloat(), currentY + 2f, paint)

        // 5. Section 1: Client & Land Particulars Box
        currentY += 32f
        drawSectionHeader(canvas, paint, "১. জমির সাধারণ পরিচিতি ও অবস্থান", 30f, currentY, width - 60f)

        currentY += 16f
        val infoTableY = currentY
        val col1 = 36f
        val col2 = 180f
        val col3 = 310f
        val col4 = 430f

        paint.textAlign = Paint.Align.LEFT
        paint.textSize = 8.5f

        // Row 1
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("আবেদনকারী/মালিক:", col1, currentY, paint)
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText(data.clientName, col2, currentY, paint)

        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("পিতা/স্বামী:", col3, currentY, paint)
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText(data.clientFather, col4, currentY, paint)

        // Row 2
        currentY += 13f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("জেলা ও উপজেলা:", col1, currentY, paint)
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText("${data.district}, ${data.upazila}", col2, currentY, paint)

        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("মৌজা ও জে.এল নং:", col3, currentY, paint)
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText("${data.mouza} (JL: ${data.jlNo})", col4, currentY, paint)

        // Row 3
        currentY += 13f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("খতিয়ান ও দাগ নং:", col1, currentY, paint)
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText("${data.khatianType} নং ${data.khatianNo}, দাগ: ${data.dagNo}", col2, currentY, paint)

        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("জমির শ্রেণি ও ধরন:", col3, currentY, paint)
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText(data.landType, col4, currentY, paint)

        // Draw box around info
        paint.style = Paint.Style.STROKE
        paint.color = Color.rgb(203, 213, 225)
        paint.strokeWidth = 0.8f
        canvas.drawRoundRect(30f, infoTableY - 10f, (width - 30).toFloat(), currentY + 8f, 4f, 4f, paint)

        // 6. Section 2: Measurements Grid (Excel Table)
        currentY += 24f
        drawSectionHeader(canvas, paint, "২. প্লটভিত্তিক দৈর্ঘ্য-প্রস্থ ও ক্ষেত্রফল পরিমাপ ছক (Excel Grid)", 30f, currentY, width - 60f)

        currentY += 16f
        val tableStartX = 30f
        val tableEndX = (width - 30).toFloat()
        val tableHeaderY = currentY

        // Table Header
        paint.style = Paint.Style.FILL
        paint.color = Color.rgb(11, 102, 35)
        canvas.drawRect(tableStartX, tableHeaderY - 10f, tableEndX, tableHeaderY + 8f, paint)

        paint.color = Color.WHITE
        paint.textSize = 8f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textAlign = Paint.Align.LEFT
        canvas.drawText("নং", tableStartX + 4f, tableHeaderY + 2f, paint)
        canvas.drawText("প্লটের বিবরণ", tableStartX + 22f, tableHeaderY + 2f, paint)
        canvas.drawText("উত্তর(ft)", tableStartX + 140f, tableHeaderY + 2f, paint)
        canvas.drawText("দক্ষিণ(ft)", tableStartX + 190f, tableHeaderY + 2f, paint)
        canvas.drawText("পূর্ব(ft)", tableStartX + 240f, tableHeaderY + 2f, paint)
        canvas.drawText("পশ্চিম(ft)", tableStartX + 285f, tableHeaderY + 2f, paint)
        canvas.drawText("বর্গফুট", tableStartX + 340f, tableHeaderY + 2f, paint)
        canvas.drawText("শতাংশ", tableStartX + 400f, tableHeaderY + 2f, paint)
        canvas.drawText("কাঠা", tableStartX + 460f, tableHeaderY + 2f, paint)

        // Table Rows
        paint.color = Color.rgb(30, 41, 59)
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        currentY += 16f

        data.plots.forEachIndexed { idx, plot ->
            // alternating row bg
            if (idx % 2 == 0) {
                paint.style = Paint.Style.FILL
                paint.color = Color.rgb(248, 250, 252)
                canvas.drawRect(tableStartX, currentY - 10f, tableEndX, currentY + 6f, paint)
            }

            paint.color = Color.rgb(30, 41, 59)
            paint.textAlign = Paint.Align.LEFT
            canvas.drawText("${idx + 1}", tableStartX + 4f, currentY, paint)
            canvas.drawText(plot.plotName.take(18), tableStartX + 22f, currentY, paint)
            canvas.drawText("${plot.northFt}", tableStartX + 140f, currentY, paint)
            canvas.drawText("${plot.southFt}", tableStartX + 190f, currentY, paint)
            canvas.drawText("${plot.eastFt}", tableStartX + 240f, currentY, paint)
            canvas.drawText("${plot.westFt}", tableStartX + 285f, currentY, paint)
            canvas.drawText(df.format(plot.squareFeet), tableStartX + 340f, currentY, paint)
            canvas.drawText(df.format(plot.decimal), tableStartX + 400f, currentY, paint)
            canvas.drawText(df.format(plot.katha), tableStartX + 460f, currentY, paint)

            currentY += 14f
        }

        // Table Total Row
        paint.style = Paint.Style.FILL
        paint.color = Color.rgb(226, 242, 230) // light green
        canvas.drawRect(tableStartX, currentY - 10f, tableEndX, currentY + 8f, paint)

        paint.color = Color.rgb(11, 102, 35)
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("সর্বমোট পরিমাপকৃত জমি:", tableStartX + 22f, currentY + 2f, paint)
        canvas.drawText(df.format(data.totalSquareFeet), tableStartX + 340f, currentY + 2f, paint)
        canvas.drawText(df.format(data.totalDecimal), tableStartX + 400f, currentY + 2f, paint)
        canvas.drawText(df.format(data.totalKatha), tableStartX + 460f, currentY + 2f, paint)

        // Draw table border
        paint.style = Paint.Style.STROKE
        paint.color = Color.rgb(203, 213, 225)
        paint.strokeWidth = 0.8f
        canvas.drawRect(tableStartX, tableHeaderY - 10f, tableEndX, currentY + 8f, paint)

        // Summary Ribbon
        currentY += 24f
        paint.style = Paint.Style.FILL
        paint.color = Color.rgb(254, 249, 195) // Gold tint ribbon
        canvas.drawRoundRect(30f, currentY - 8f, (width - 30).toFloat(), currentY + 20f, 6f, 6f, paint)

        paint.color = Color.rgb(133, 77, 14)
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 9.5f
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText(
            "জমির পরিমাণ: ${df.format(data.totalDecimal)} শতাংশ (শতক) | ${df.format(data.totalKatha)} কাঠা | ${df.format(data.totalBigha)} বিঘা | ${df.format(data.totalAcre)} একর",
            width / 2f,
            currentY + 8f,
            paint
        )

        // 7. Section 3: Boundaries (চতুর্সীমা)
        currentY += 34f
        drawSectionHeader(canvas, paint, "৩. চতুর্সীমা ও সীমানা বিবরণ", 30f, currentY, width - 60f)

        currentY += 16f
        val boundBoxY = currentY
        paint.textAlign = Paint.Align.LEFT
        paint.textSize = 8.5f

        // North & South
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("উত্তরে:", col1, currentY, paint)
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText(data.northBoundary, col1 + 45f, currentY, paint)

        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("দক্ষিণে:", col3, currentY, paint)
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText(data.southBoundary, col3 + 45f, currentY, paint)

        // East & West
        currentY += 13f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("পূর্বে:", col1, currentY, paint)
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText(data.eastBoundary, col1 + 45f, currentY, paint)

        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("পশ্চিমে:", col3, currentY, paint)
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText(data.westBoundary, col3 + 45f, currentY, paint)

        paint.style = Paint.Style.STROKE
        paint.color = Color.rgb(203, 213, 225)
        paint.strokeWidth = 0.8f
        canvas.drawRoundRect(30f, boundBoxY - 10f, (width - 30).toFloat(), currentY + 8f, 4f, 4f, paint)

        // 8. Section 4: Surveyor Remarks (Word section)
        currentY += 24f
        drawSectionHeader(canvas, paint, "৪. সার্ভেয়ারের পেশাদার অভিমত ও সুপারিশ", 30f, currentY, width - 60f)

        currentY += 14f
        paint.style = Paint.Style.FILL
        paint.color = Color.rgb(30, 41, 59)
        paint.textSize = 8f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        paint.textAlign = Paint.Align.LEFT

        // Simple text wrap for remarks
        val wrappedLines = wrapText(data.findingsRemarks, 95)
        wrappedLines.forEach { line ->
            canvas.drawText(line, 36f, currentY, paint)
            currentY += 11f
        }

        if (data.legalAdvice.isNotBlank()) {
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText("সুপারিশ: ${data.legalAdvice.take(90)}", 36f, currentY, paint)
            currentY += 12f
        }

        // 9. Section 5: Signatures & Certification
        val signY = (height - 95).toFloat()

        // Left: Client Signature
        paint.style = Paint.Style.STROKE
        paint.color = Color.rgb(100, 116, 139)
        paint.strokeWidth = 1f
        canvas.drawLine(50f, signY, 200f, signY, paint)

        paint.style = Paint.Style.FILL
        paint.color = Color.rgb(30, 41, 59)
        paint.textSize = 8.5f
        paint.textAlign = Paint.Align.CENTER
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText("জমির মালিক / আবেদনকারীর স্বাক্ষর", 125f, signY + 14f, paint)

        // Right: Certified Surveyor Signature & Stamp Box
        canvas.drawLine(width - 220f, signY, width - 50f, signY, paint)
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.color = Color.rgb(11, 102, 35)
        canvas.drawText(data.surveyorName, width - 135f, signY + 14f, paint)

        paint.textSize = 7.5f
        paint.color = Color.rgb(71, 85, 105)
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText("সনদপ্রাপ্ত ডিজিটাল ভূমি সার্ভেয়ার ও আমিন", width - 135f, signY + 26f, paint)
        canvas.drawText("রেজিস্ট্রেশন: ${data.surveyorRegNo} | মোবা: ${data.surveyorPhone}", width - 135f, signY + 37f, paint)

        // Footer Note
        paint.textSize = 7f
        paint.color = Color.rgb(148, 163, 184)
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText("Generated via M.S Monaym Land Intelligent System • Verified Digital Survey Report", width / 2f, (height - 26).toFloat(), paint)
    }

    private fun drawSectionHeader(canvas: Canvas, paint: Paint, title: String, x: Float, y: Float, width: Float) {
        paint.style = Paint.Style.FILL
        paint.color = Color.rgb(241, 245, 249)
        canvas.drawRoundRect(x, y - 10f, x + width, y + 6f, 3f, 3f, paint)

        paint.color = Color.rgb(11, 102, 35)
        paint.textSize = 9.5f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textAlign = Paint.Align.LEFT
        canvas.drawText(title, x + 6f, y, paint)
    }

    private fun wrapText(text: String, maxCharsPerLine: Int): List<String> {
        val words = text.split(" ")
        val lines = mutableListOf<String>()
        var currentLine = StringBuilder()

        for (word in words) {
            if (currentLine.length + word.length + 1 <= maxCharsPerLine) {
                if (currentLine.isNotEmpty()) currentLine.append(" ")
                currentLine.append(word)
            } else {
                if (currentLine.isNotEmpty()) lines.add(currentLine.toString())
                currentLine = StringBuilder(word)
            }
        }
        if (currentLine.isNotEmpty()) lines.add(currentLine.toString())
        return if (lines.isEmpty()) listOf(text) else lines
    }

    /**
     * Print PDF document directly using Android PrintManager
     */
    fun printSurveyPdf(context: Context, pdfFile: File) {
        try {
            val printManager = context.getSystemService(Context.PRINT_SERVICE) as? PrintManager
            if (printManager == null) {
                Toast.makeText(context, "প্রিন্ট সেবা পাওয়া যায়নি", Toast.LENGTH_SHORT).show()
                return
            }

            val printAdapter = object : PrintDocumentAdapter() {
                override fun onLayout(
                    oldAttributes: PrintAttributes?,
                    newAttributes: PrintAttributes?,
                    cancellationSignal: android.os.CancellationSignal?,
                    callback: LayoutResultCallback?,
                    extras: android.os.Bundle?
                ) {
                    if (cancellationSignal?.isCanceled == true) {
                        callback?.onLayoutCancelled()
                        return
                    }
                    val info = android.print.PrintDocumentInfo.Builder(pdfFile.name)
                        .setContentType(android.print.PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                        .setPageCount(1)
                        .build()
                    callback?.onLayoutFinished(info, true)
                }

                override fun onWrite(
                    pages: Array<out android.print.PageRange>?,
                    destination: android.os.ParcelFileDescriptor?,
                    cancellationSignal: android.os.CancellationSignal?,
                    callback: WriteResultCallback?
                ) {
                    try {
                        val input = FileInputStream(pdfFile)
                        val output = FileOutputStream(destination?.fileDescriptor)
                        input.copyTo(output)
                        input.close()
                        output.close()
                        callback?.onWriteFinished(arrayOf(android.print.PageRange.ALL_PAGES))
                    } catch (e: Exception) {
                        callback?.onWriteFailed(e.message)
                    }
                }
            }

            val printAttributes = PrintAttributes.Builder()
                .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                .setColorMode(PrintAttributes.COLOR_MODE_COLOR)
                .build()

            printManager.print("Land_Survey_Report", printAdapter, printAttributes)
        } catch (e: Exception) {
            Toast.makeText(context, "প্রিন্ট করতে সমস্যা: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
