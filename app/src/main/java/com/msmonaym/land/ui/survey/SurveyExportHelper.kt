package com.msmonaym.land.ui.survey

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.text.DecimalFormat

object SurveyExportHelper {

    private val df = DecimalFormat("#,##0.00")

    /**
     * Generate and save Excel CSV File
     */
    fun exportToExcelCsv(context: Context, data: SurveyReportData): File? {
        return try {
            val fileName = "Land_Survey_Excel_${data.reportId.replace("-", "_")}.csv"
            val file = File(context.cacheDir, fileName)
            val fos = FileOutputStream(file)
            
            // UTF-8 BOM for Microsoft Excel Bengali font support
            fos.write(byteArrayOf(0xEF.toByte(), 0xBB.toByte(), 0xBF.toByte()))
            
            val sb = StringBuilder()
            sb.append("=== ${data.organizationName} - জমি পরিমাপ ও সার্ভে এক্সেল স্প্রেডশিট ===\n")
            sb.append("রিপোর্ট আইডি,${data.reportId},তারিখ,${data.date}\n")
            sb.append("সার্ভেয়ার,${data.surveyorName},রেজিস্ট্রেশন,${data.surveyorRegNo}\n")
            sb.append("মালিকের নাম,${data.clientName},পিতা/স্বামী,${data.clientFather}\n")
            sb.append("জেলা,${data.district},উপজেলা,${data.upazila},মৌজা,${data.mouza},জেএল নং,${data.jlNo}\n")
            sb.append("খতিয়ান,${data.khatianType} ${data.khatianNo},দাগ নং,${data.dagNo},জমির শ্রেণি,${data.landType}\n\n")

            sb.append("--- প্লটভিত্তিক পরিমাপ ও ক্ষেত্রফল স্প্রেডশিট (Excel Plot Measurements) ---\n")
            sb.append("ক্রমিক,প্লটের বিবরণ,উত্তর বাহু (ফুট),দক্ষিণ বাহু (ফুট),পূর্ব বাহু (ফুট),পশ্চিম বাহু (ফুট),গড় দৈর্ঘ্য,গড় প্রস্থ,বর্গফুট,শতাংশ (শতক),কাঠা,বিঘা,মন্তব্য\n")
            
            data.plots.forEachIndexed { index, plot ->
                sb.append("${index + 1},")
                sb.append("\"${plot.plotName}\",")
                sb.append("${plot.northFt},")
                sb.append("${plot.southFt},")
                sb.append("${plot.eastFt},")
                sb.append("${plot.westFt},")
                sb.append("${df.format(plot.avgLength)},")
                sb.append("${df.format(plot.avgWidth)},")
                sb.append("${df.format(plot.squareFeet)},")
                sb.append("${df.format(plot.decimal)},")
                sb.append("${df.format(plot.katha)},")
                sb.append("${df.format(plot.bigha)},")
                sb.append("\"${plot.notes}\"\n")
            }

            sb.append("\nমোট জমি যোগফল,,,,,,,${df.format(data.totalSquareFeet)},${df.format(data.totalDecimal)},${df.format(data.totalKatha)},${df.format(data.totalBigha)},মোট একর: ${df.format(data.totalAcre)}\n\n")

            if (data.shareholders.isNotEmpty()) {
                sb.append("--- অংশীদার বা ওয়ারিশ বণ্টন ছক (Shareholder Distribution) ---\n")
                sb.append("ক্রমিক,অংশীদারের নাম,সম্পর্ক,অংশের হার (%),প্রাপ্য শতাংশ (শতক),প্রাপ্য কাঠা\n")
                data.shareholders.forEachIndexed { index, sh ->
                    val shDecimal = (data.totalDecimal * sh.sharePercentage) / 100.0
                    val shKatha = (data.totalKatha * sh.sharePercentage) / 100.0
                    sb.append("${index + 1},\"${sh.name}\",\"${sh.relation}\",${sh.sharePercentage}%,${df.format(shDecimal)},${df.format(shKatha)}\n")
                }
            }

            sb.append("\n--- সীমানা ও চতুর্সীমা ---\n")
            sb.append("উত্তরে,\"${data.northBoundary}\"\n")
            sb.append("দক্ষিণে,\"${data.southBoundary}\"\n")
            sb.append("পূর্বে,\"${data.eastBoundary}\"\n")
            sb.append(" পশ্চিমে,\"${data.westBoundary}\"\n\n")

            sb.append("--- সার্ভেয়ারের পেশাদার অভিমত ---\n")
            sb.append("\"${data.findingsRemarks}\"\n")

            fos.write(sb.toString().toByteArray(Charsets.UTF_8))
            fos.flush()
            fos.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Generate and save Word Document (.doc / HTML-based format)
     */
    fun exportToWordDoc(context: Context, data: SurveyReportData): File? {
        return try {
            val fileName = "Land_Survey_Word_${data.reportId.replace("-", "_")}.doc"
            val file = File(context.cacheDir, fileName)
            val fos = FileOutputStream(file)
            
            val sb = StringBuilder()
            sb.append("<html xmlns:o='urn:schemas-microsoft-com:office:office' xmlns:w='urn:schemas-microsoft-com:office:word' xmlns='http://www.w3.org/TR/REC-html40'>\n")
            sb.append("<head><meta charset='utf-8'><title>Land Survey Report</title>\n")
            sb.append("<style>\n")
            sb.append("body { font-family: 'Segoe UI', Arial, sans-serif; margin: 30px; color: #1e293b; }\n")
            sb.append("h1 { color: #0b6623; text-align: center; margin-bottom: 2px; font-size: 24px; }\n")
            sb.append("h2 { color: #047857; text-align: center; margin-top: 0; font-size: 16px; font-weight: normal; }\n")
            sb.append(".header-box { border: 2px solid #0b6623; padding: 15px; border-radius: 8px; margin-bottom: 20px; background: #f4fbf6; }\n")
            sb.append("table { width: 100%; border-collapse: collapse; margin-top: 10px; margin-bottom: 15px; }\n")
            sb.append("th, td { border: 1px solid #cbd5e1; padding: 8px 10px; text-align: left; font-size: 13px; }\n")
            sb.append("th { background-color: #0b6623; color: white; }\n")
            sb.append(".total-row { background-color: #e2f2e6; font-weight: bold; }\n")
            sb.append(".section-title { font-size: 15px; font-weight: bold; color: #0b6623; border-bottom: 2px solid #d4af37; padding-bottom: 4px; margin-top: 20px; }\n")
            sb.append(".signature-box { margin-top: 40px; width: 100%; }\n")
            sb.append("</style></head><body>\n")

            sb.append("<h1>${data.organizationName}</h1>\n")
            sb.append("<h2>জমির ডিজিটাল সীমানা পরিমাপ, অংশ বণ্টন ও সার্ভেয়ার রিপোর্ট</h2>\n")
            
            sb.append("<div class='header-box'>\n")
            sb.append("<strong>রিপোর্ট নং:</strong> ${data.reportId} &nbsp;|&nbsp; <strong>তারিখ:</strong> ${data.date}<br/>\n")
            sb.append("<strong>সার্ভেয়ার / আমিন:</strong> ${data.surveyorName} (${data.surveyorRegNo}) &nbsp;|&nbsp; <strong>মোবাইল:</strong> ${data.surveyorPhone}\n")
            sb.append("</div>\n")

            sb.append("<div class='section-title'>১. জমির পরিচিতি ও ক্লায়েন্ট বিবরণ</div>\n")
            sb.append("<table>\n")
            sb.append("<tr><td><strong>আবেদনকারীর নাম:</strong></td><td>${data.clientName}</td><td><strong>পিতা/স্বামীর নাম:</strong></td><td>${data.clientFather}</td></tr>\n")
            sb.append("<tr><td><strong>মোবাইল নম্বর:</strong></td><td>${data.clientPhone}</td><td><strong>ঠিকানা:</strong></td><td>${data.clientAddress}</td></tr>\n")
            sb.append("<tr><td><strong>জেলা:</strong></td><td>${data.district}</td><td><strong>উপজেলা/থানা:</strong></td><td>${data.upazila}</td></tr>\n")
            sb.append("<tr><td><strong>মৌজা ও জে.এল নং:</strong></td><td>${data.mouza} (JL: ${data.jlNo})</td><td><strong>জমির শ্রেণি:</strong></td><td>${data.landType}</td></tr>\n")
            sb.append("<tr><td><strong>খতিয়ান নম্বর:</strong></td><td>${data.khatianType} নং ${data.khatianNo}</td><td><strong>দাগ নম্বর:</strong></td><td>${data.dagNo}</td></tr>\n")
            sb.append("</table>\n")

            sb.append("<div class='section-title'>২. প্লটভিত্তিক দৈর্ঘ্য-প্রস্থ ও ক্ষেত্রফল হিসাব ছক (Microsoft Excel Grid)</div>\n")
            sb.append("<table>\n")
            sb.append("<tr><th>নং</th><th>প্লট / খণ্ড</th><th>উত্তর (ফুট)</th><th>দক্ষিণ (ফুট)</th><th>পূর্ব (ফুট)</th><th>পশ্চিম (ফুট)</th><th>মোট বর্গফুট</th><th>শতাংশ (শতক)</th><th>কাঠা</th></tr>\n")
            data.plots.forEachIndexed { i, p ->
                sb.append("<tr><td>${i+1}</td><td>${p.plotName}</td><td>${p.northFt}</td><td>${p.southFt}</td><td>${p.eastFt}</td><td>${p.westFt}</td><td>${df.format(p.squareFeet)}</td><td>${df.format(p.decimal)}</td><td>${df.format(p.katha)}</td></tr>\n")
            }
            sb.append("<tr class='total-row'><td colspan='6' align='right'>সর্বমোট পরিমাপকৃত জমির পরিমাণ:</td><td>${df.format(data.totalSquareFeet)}</td><td>${df.format(data.totalDecimal)}</td><td>${df.format(data.totalKatha)}</td></tr>\n")
            sb.append("</table>\n")
            sb.append("<p><strong>মোট সারাংশ:</strong> ${df.format(data.totalDecimal)} শতাংশ (বা ${df.format(data.totalKatha)} কাঠা / ${df.format(data.totalBigha)} বিঘা / ${df.format(data.totalAcre)} একর)</p>\n")

            if (data.shareholders.isNotEmpty()) {
                sb.append("<div class='section-title'>৩. অংশীদার / ওয়ারিশ বণ্টন বিবরণী</div>\n")
                sb.append("<table>\n")
                sb.append("<tr><th>নং</th><th>অংশীদারের নাম</th><th>সম্পর্ক</th><th>অংশ (%)</th><th>প্রাপ্য শতাংশ</th><th>প্রাপ্য কাঠা</th></tr>\n")
                data.shareholders.forEachIndexed { i, sh ->
                    val shDecimal = (data.totalDecimal * sh.sharePercentage) / 100.0
                    val shKatha = (data.totalKatha * sh.sharePercentage) / 100.0
                    sb.append("<tr><td>${i+1}</td><td>${sh.name}</td><td>${sh.relation}</td><td>${sh.sharePercentage}%</td><td>${df.format(shDecimal)}</td><td>${df.format(shKatha)}</td></tr>\n")
                }
                sb.append("</table>\n")
            }

            sb.append("<div class='section-title'>৪. সীমানা ও চতুর্সীমা বিবরণ</div>\n")
            sb.append("<table>\n")
            sb.append("<tr><td width='25%'><strong>উত্তরে:</strong></td><td>${data.northBoundary}</td></tr>\n")
            sb.append("<tr><td><strong>দক্ষিণে:</strong></td><td>${data.southBoundary}</td></tr>\n")
            sb.append("<tr><td><strong>পূর্বে:</strong></td><td>${data.eastBoundary}</td></tr>\n")
            sb.append("<tr><td><strong> পশ্চিমে:</strong></td><td>${data.westBoundary}</td></tr>\n")
            sb.append("</table>\n")

            sb.append("<div class='section-title'>৫. সার্ভেয়ারের পেশাদার অভিমত ও সিদ্ধান্ত</div>\n")
            sb.append("<p style='line-height:1.6; text-align: justify;'>${data.findingsRemarks}</p>\n")
            sb.append("<p><strong>আইনি ও নামজারি সুপারিশ:</strong> ${data.legalAdvice}</p>\n")

            sb.append("<table class='signature-box' style='border:none; margin-top:50px;'>\n")
            sb.append("<tr style='border:none;'>\n")
            sb.append("<td style='border:none; text-align:center; width:50%;'><br/><br/>_______________________<br/>জমির মালিক / আবেদনকারীর স্বাক্ষর</td>\n")
            sb.append("<td style='border:none; text-align:center; width:50%;'><br/><br/>_______________________<br/><strong>${data.surveyorName}</strong><br/>সনদপ্রাপ্ত ডিজিটাল ভূমি সার্ভেয়ার ও আমিন<br/>রেজি: ${data.surveyorRegNo}</td>\n")
            sb.append("</tr></table>\n")

            sb.append("</body></html>")

            fos.write(sb.toString().toByteArray(Charsets.UTF_8))
            fos.flush()
            fos.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Share file via Android Intent
     */
    fun shareFile(context: Context, file: File, mimeType: String, title: String) {
        try {
            val uri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = mimeType
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, title)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, "রিপোর্ট শেয়ার করুন"))
        } catch (e: Exception) {
            Toast.makeText(context, "ফাইল শেয়ার করতে সমস্যা: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Open file directly with installed apps (Word / Excel / PDF viewer)
     */
    fun openFile(context: Context, file: File, mimeType: String) {
        try {
            val uri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, mimeType)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "ফাইল ওপেন করতে সমস্যা: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
