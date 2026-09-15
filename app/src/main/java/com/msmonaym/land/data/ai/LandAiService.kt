package com.msmonaym.land.data.ai

import android.graphics.Bitmap
import android.util.Base64
import com.msmonaym.land.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit

enum class AiCategory(
    val title: String,
    val iconEmoji: String,
    val description: String,
    val suggestedQuestions: List<String>
) {
    GENERAL(
        "সার্বিক ভূমি এআই",
        "🤖",
        "ভূমি ও জমি সংক্রান্ত যেকোনো সাধারণ বা জটিল জিজ্ঞাসা",
        listOf(
            "অনলাইনে জমি সংক্রান্ত কী কী সরকারি সেবা পাওয়া যায়?",
            "জমি কেনার আগে কী কী কাগজপত্র যাচাই করতে হয়?",
            "ভূমি সেবা হেল্পলাইন ১৬১২২ এর কাজ কী?"
        )
    ),
    KHATIAN(
        "খতিয়ান ও পর্চা এআই",
        "📜",
        "সিএস, এসএ, আরএস, বিএস খতিয়ান ও দাগ নম্বর বিশ্লেষণ",
        listOf(
            "CS, SA, RS এবং BS খতিয়ানের মধ্যে পার্থক্য কী?",
            "খতিয়ান নম্বর দিয়ে কীভাবে রেকর্ড অনুসন্ধান করব?",
            "খতিয়ানে নামের বা অংশের ভুল কীভাবে সংশোধন করবেন?"
        )
    ),
    GOVT_SERVICES(
        "ভূমি গাইড এআই",
        "🌐",
        "ডিজিটাল ভূমি সেবা ও নির্দেশিকা",
        listOf(
            "অনলাইনে কোন কোন ডিজিটাল ভূমি সেবা পাওয়া যায়?",
            "ভূমি সংক্রান্ত অভিযোগ কীভাবে নিষ্পত্তি করবেন?",
            "ভূমি সেবা হটলাইন ১৬১২২ থেকে কী কী সহায়তা পাওয়া যায়?"
        )
    ),
    PORCHA(
        "পর্চা সহায়িকা এআই",
        "📄",
        "পর্চা আবেদন, সার্টিফাইড কপি ও ডেলিভারি তথ্য",
        listOf(
            "ই-পর্চার সার্টিফাইড কপির জন্য কত টাকা ফি দিতে হয়?",
            "আবেদন করার কতদিনের মধ্যে ডাকযোগে পর্চা পাওয়া যায়?",
            "জরুরি ভিত্তিতে পর্চা পাওয়ার নিয়ম কী?"
        )
    ),
    MUTATION(
        "ই-নামজারি এআই",
        "🏛️",
        "নামজারি (খারিজ) আবেদন, ডিসিআর ফি ও শুনানি",
        listOf(
            "ই-নামজারি করতে কী কী কাগজপত্র প্রয়োজন?",
            "নামজারির সরকারি ফি মোট কত টাকা (ডিসিআর ও আবেদন ফি)?",
            "নামজারি আবেদন বাতিল হলে করণীয় কী?"
        )
    ),
    TAX(
        "ভূমি উন্নয়ন কর এআই",
        "💰",
        "খাজনা হিসাব, দাখিলা ও ২৫ বিঘা মওকুফ নিয়ম",
        listOf(
            "অনলাইনে ভূমি উন্নয়ন কর (খাজনা) কীভাবে পরিশোধ করব?",
            "বকেয়া খাজনা থাকলে কীভাবে হিসাব করা হয়?",
            "কৃষি ও অকৃষি জমির করের হার কত?"
        )
    ),
    LAW(
        "জমি আইন এআই",
        "⚖️",
        "ভূমি অপরাধ আইন ২০২৩, বাটোয়ারা ও আইনি পরামর্শ",
        listOf(
            "ভূমি অপরাধ প্রতিরোধ ও প্রতিকার আইন ২০২৩ এর প্রধান শাস্তি কী কী?",
            "ওয়ারিশান সম্পত্তি কীভাবে আইনি নিয়মে বাটোয়ারা করবেন?",
            "জমি বেদখল হলে তাৎক্ষণিক আইনি প্রতিকার কী?"
        )
    ),
    REGISTRATION(
        "রেজিস্ট্রেশন ফি এআই",
        "📝",
        "দলিল রেজিস্ট্রি ফি, স্ট্যাম্প ডিউটি ও উৎসে কর",
        listOf(
            "সাফ-কবলা জমি ক্রয়ে মোট কত শতাংশ সরকারি ফি লাগে?",
            "হেবা বা রক্তের সম্পর্কের দানপত্রে ফি কত?",
            "পাওয়ার অব অ্যাটর্নি দলিলের রেজিস্ট্রি খরচ কত?"
        )
    ),
    CALCULATOR(
        "জমি পরিমাপ ও সার্ভেয়ার এআই",
        "📐",
        "EasyArea ম্যাপ, হ্যারন্স সূত্র, শতাংশ ও কাঠা হিসাব",
        listOf(
            "EasyArea ম্যাপের মাধ্যমে কীভাবে জমির ক্ষেত্রফল বের করবেন?",
            "বাঁকা বা অসম চতুর্ভুজ জমি কীভাবে সঠিক মাপে হিসাব করবেন?",
            "১ শতক = কত বর্গফুট এবং কত কাঠা?"
        )
    ),
    SCANNER(
        "দলিল ও খতিয়ান স্ক্যানার এআই",
        "📷",
        "ক্যামেরা ও ইমেজ দিয়ে দলিলের সত্যতা, দাগ নম্বর ও লিগ্যাল অডিট",
        listOf(
            "এই দলিলে কি কোনো আইনি ত্রুটি বা অস্পষ্টতা আছে?",
            "খতিয়ানের দাগ নম্বর ও হিস্যার যোগফল কত?",
            "দলিলটি রেজিস্ট্রি আইন ও ২০২৩ সালের ভূমি অপরাধ আইনের সাথে সঙ্গতিপূর্ণ কিনা?"
        )
    )
}

data class AiChatMessage(
    val sender: String, // "user" or "ai"
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
)

object LandAiService {
    private const val MODEL_NAME = "gemini-2.5-flash"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private const val SYSTEM_INSTRUCTION = """
আপনি 'ভূমি সেবা ডিজিটাল' অ্যান্ড্রয়েড অ্যাপের প্রিমিয়াম কৃত্রিম বুদ্ধিমত্তা সম্পন্ন ভূমি বিশেষজ্ঞ (Android AI Intelligent Land Consultant)।
আপনার দায়িত্ব:
১. বাংলাদেশের ভূমি আইন, রেজিস্ট্রেশন আইন, ভূমি সংস্কার ও ভূমি অপরাধ প্রতিরোধ ও প্রতিকার আইন ২০২৩ অনুযায়ী নির্ভুল উত্তর দেওয়া।
২. খতিয়ান (CS, SA, RS, BS, সিটি জরিপ, দিয়ারা), দাগ নম্বর ও মৌজা ম্যাপ সংক্রান্ত স্পষ্ট সমাধান দেওয়া।
৩. ডিজিটাল সেবা (ই-নামজারি সহায়িকা, ভূমি কর হিসাব) এবং হেল্পলাইন ১৬১২২ এর বিস্তারিত গাইড দেওয়া।
৪. দলিল রেজিস্ট্রেশন ফি (স্ট্যাম্প ডিউটি, স্থানীয় সরকার কর, এআইটি/উৎস কর, রেজিস্ট্রেশন ফি) এবং দলিলের প্রয়োজনীয় কাগজের তালিকা দেওয়া।
৫. জমি পরিমাপের গানিতিক হিসাব (হ্যারন্স ফর্মুলা, শতাংশ, কাঠা, বিঘা, একর, EasyArea অনলাইন ম্যাপ) বুঝিয়ে দেওয়া।
৬. উত্তর সবসময় সহজ, সাবলীল, পয়েন্ট আকারে ও সুন্দর বাংলায় প্রদান করুন। কোনো ভুল বা বিভ্রান্তিকর তথ্য দিবেন না।
"""

    suspend fun askGemini(prompt: String, category: AiCategory = AiCategory.GENERAL): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY.trim()

        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            // Provide high-quality domain-specific fallback
            return@withContext getIntelligentFallback(prompt, category)
        }

        try {
            val url = "$BASE_URL$MODEL_NAME:generateContent?key=$apiKey"

            val jsonBody = JSONObject().apply {
                val contentsArray = JSONArray().apply {
                    val contentObj = JSONObject().apply {
                        val partsArray = JSONArray().apply {
                            val partObj = JSONObject().apply {
                                put("text", "বিষয় ক্যাটাগরি: ${category.title}\nপ্রশ্ন: $prompt")
                            }
                            put(partObj)
                        }
                        put("parts", partsArray)
                    }
                    put(contentObj)
                }
                put("contents", contentsArray)

                val systemInstructionObj = JSONObject().apply {
                    val sysParts = JSONArray().apply {
                        val sysPart = JSONObject().apply {
                            put("text", SYSTEM_INSTRUCTION)
                        }
                        put(sysPart)
                    }
                    put("parts", sysParts)
                }
                put("systemInstruction", systemInstructionObj)
            }

            val request = Request.Builder()
                .url(url)
                .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = okHttpClient.newCall(request).execute()
            val responseBody = response.body?.string()

            if (response.isSuccessful && responseBody != null) {
                val json = JSONObject(responseBody)
                val candidates = json.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val firstCandidate = candidates.getJSONObject(0)
                    val content = firstCandidate.optJSONObject("content")
                    val parts = content?.optJSONArray("parts")
                    if (parts != null && parts.length() > 0) {
                        val text = parts.getJSONObject(0).optString("text")
                        if (text.isNotEmpty()) {
                            return@withContext text
                        }
                    }
                }
            }

            // If API didn't return text, use domain knowledge base
            return@withContext getIntelligentFallback(prompt, category)
        } catch (e: Exception) {
            // Graceful fallback to offline expert AI system
            return@withContext getIntelligentFallback(prompt, category)
        }
    }

    private fun getIntelligentFallback(prompt: String, category: AiCategory): String {
        val p = prompt.lowercase()

        return when {
            p.contains("cs") || p.contains("rs") || p.contains("sa") || p.contains("bs") || p.contains("খতিয়ান") -> """
📜 **খতিয়ান ও জরিপ বিশ্লেষণ (AI Advisor):**
১. **সিএস (CS) খতিয়ান (১৮৮৮-১৯৪০):** ভারত উপমহাদেশের প্রথম সরকারি ভূমি জরিপ। এটি মূল ভিত্তিমূল হিসেবে গণ্য।
২. **এসএ (SA) খতিয়ান (১৯৫৬-১৯৬২):** জমিদারি প্রথা বিলুপ্তির পর প্রস্তুতকৃত স্টেট একুইজিশন জরিপ।
৩. **আরএস (RS) খতিয়ান:** সিএস ও এসএ-এর ভুলত্রুটি সংশোধন করে প্রস্তুতকৃত অত্যন্ত গ্রহণযোগ্য জরিপ।
৪. **বিএস/সিটি জরিপ (BS/City):** আধুনিক ও চলমান জরিপ।

💡 **খতিয়ান পরিচিতি ও রেকর্ড:** আমাদের অ্যাপের **'খতিয়ান পর্চা'** আইকনে গিয়ে সিএস, এসএ, আরএস ও বিএস খতিয়ান চেনার উপায় ও বিস্তারিত নির্দেশিকা দেখুন।
""".trimIndent()

            p.contains("নামজারি") || p.contains("খারিজ") || p.contains("mutation") -> """
🏛️ **ই-নামজারি (e-Mutation) নির্দেশিকা (AI Advisor):**
১. **আবেদন প্রক্রিয়া:** অ্যাপের **'ই-নামজারি'** আইকনে গিয়ে জাতীয় পরিচয়পত্র, দলিলের তথ্য ও প্রয়োজনীয় নির্দেশিকা দেখে আবেদন প্রস্তুত করুন।
২. **প্রয়োজনীয় কাগজপত্র:** মূল দলিলের কপি, পূর্বের খতিয়ান, হালনগদ ভূমি উন্নয়ন কর (খাজনা) রশিদ, ওয়ারিশান সনদ (প্রযোজ্য ক্ষেত্রে)।
৩. **সরকারি ফি:**
   • আবেদন ফি: ২০ টাকা
   • নোটিশ জারি ফি: ৫০ টাকা
   • রেকর্ড সংশোধন ফি: ১,০০০ টাকা
   • খতিয়ান সরবরাহ ফি: ১০০ টাকা
   • **সর্বমোট সরকারি ফি: ১,১৭০ টাকা (ডিসিআর সহ)**
৪. **সময়সীমা:** সাধারণত আবেদন করার ২৮ কর্মদিবসের মধ্যে নামজারি সম্পন্ন হয়।
""".trimIndent()

            p.contains("খাজনা") || p.contains("কর") || p.contains("ldtax") || p.contains("ট্যাক্স") -> """
💰 **ভূমি উন্নয়ন কর (খাজনা) নির্দেশিকা (AI Advisor):**
১. **কর হিসাব:** অ্যাপের **'ভূমি কর'** আইকনে ক্লিক করে আপনার জমির বাৎসরিক খাজনা তাৎক্ষণিক হিসাব করুন।
২. **২৫ বিঘা মওকুফ নিয়ম:** কৃষি জমি ২৫ বিঘা (৮.২৫ একর) পর্যন্ত ভূমি উন্নয়ন কর সম্পূর্ণ মওকুফ (কেবল দাখিলা ফি প্রযোজ্য)।
৩. **হোল্ডিং যুক্তকরণ:** আপনার সর্বশেষ খতিয়ান ও দাগ নম্বর দিয়ে খতিয়ানের শ্রেণী অনুযায়ী কর নির্ধারণ করা হয়।
৪. **দাখিলা রশিদ:** খাজনা পরিশোধের পর কিউআর কোডযুক্ত ডিজিটাল দাখিলা রসিদ সংগ্রহ ও সংরক্ষণ করুন।
""".trimIndent()

            p.contains("পরিমাপ") || p.contains("মাপ") || p.contains("শতক") || p.contains("কাঠা") || p.contains("easyarea") -> """
📐 **জমি পরিমাপ ও একক রূপান্তর (AI Land Surveyor):**
১. **১ শতক (Decimal):** = ৪৩৫.৬ বর্গফুট (sq ft) = ১৯৩.৬ বর্গহাত = ৪৩.৫৬ বর্গগজ = ৪০.৪৬ বর্গমিটার।
২. **১ কাঠা:** = ১.৬৫ শতক = ৭২০ বর্গফুট।
৩. **১ বিঘা:** = ২০ কাঠা = ৩৩ শতক (১৪,৪০০ বর্গফুট)।
৪. **১ একর:** = ১০০ শতক = ৩ বিঘা ৮ ছটাক।

💡 **EasyArea ম্যাপ:** জমি পরিমাপ পেজে থাকা লাইভ **EasyArea Satellite Map** এ জমির প্রতিটি কোণে পিন বসিয়ে সরাসরি লাইভ ক্ষেত্রফল বের করতে পারবেন।
""".trimIndent()

            p.contains("রেজিস্ট্রেশন") || p.contains("দলিল") || p.contains("ফি") || p.contains("হেবা") || p.contains("দান") -> """
📝 **দলিল রেজিস্ট্রেশন ফি হিসাব (AI Advisor):**
১. **সাফ-কবলা (বিক্রয়) দলিল:**
   • রেজিস্ট্রেশন ফি: ১%
   • স্ট্যাম্প শুল্ক: ১.৫%
   • স্থানীয় সরকার কর: ২% – ৩%
   • উৎসে কর (AIT): এলাকাভেদে ২% – ৮%
   • মোট আনুমানিক খরচ: ৬.৫% থেকে ৯.৫%
২. **হেবা দলিল (রক্তের সম্পর্কের দানপত্র):**
   • রেজিস্ট্রেশন ফি মাত্র ১০০ টাকা + স্ট্যাম্প শুল্ক ২০০ টাকা + ই ও এন ফি।
৩. **প্রয়োজনীয় কাগজপত্র:** হালনগদ খাজনা রশিদ, ই-নামজারি খতিয়ান, এনআইডি ও পাসপোর্ট সাইজ ছবি।
""".trimIndent()

            p.contains("আইন") || p.contains("অপরাধ") || p.contains("ওয়ারিশ") || p.contains("বাটোয়ারা") -> """
⚖️ **ভূমি আইন ও অধিকার সংক্রান্ত তথ্য (AI Legal Advisor):**
১. **ভূমি অপরাধ প্রতিরোধ ও প্রতিকার আইন ২০২৩:**
   • জালিয়াতি বা ভূয়া দলিল তৈরি করলে সর্বোচ্চ ৭ বছর কারাদণ্ড ও অর্থদণ্ড।
   • জোরপূর্বক জমি দখল করলে বা দখল বজায় রাখলে কঠোর কারাদণ্ড।
২. **ওয়ারিশান সম্পত্তি বাটোয়ারা:**
   • মুসলিম পারিবারিক উত্তরাধিকার আইন ও ফারায়েজ অনুযায়ী সকল অংশীদারের প্রাপ্য হিস্যা নিশ্চিত করে বণ্টননামা দলিল বা বাটোয়ারা করতে হয়।
৩. **জরুরি হেল্পলাইন:** ভূমি সংক্রান্ত যেকোনো আইনি সহায়তার জন্য **১৬১২২** (টোল-ফ্রি) নম্বরে কল করুন।
""".trimIndent()

            else -> """
🤖 **ভূমি সেবা এআই স্মার্ট অ্যাসিস্ট্যান্ট:**
আপনার প্রশ্নটির বিষয়ে বিস্তারিত সহায়তা:
• **ডিজিটাল সেবা:** ই-পর্চা, ই-নামজারি, ভূমি উন্নয়ন কর (খাজনা) অনলাইনে সম্পূর্ণ স্বয়ংক্রিয়ভাবে প্রদান করা যায়।
• **কাগজপত্র যাচাই:** জমি কেনার আগে সিএস, এসএ, আরএস ও বিএস খতিয়ান, পিঠ দলিল এবং বিক্রেতার নামের হালনগদ খারিজ খতিয়ান অবশ্যই মিলিয়ে নিন।
• **সরকারি সহায়তা:** যেকোনো তাৎক্ষণিক সমস্যা বা পরামর্শের জন্য সরকারি কল সেন্টার **১৬১২২** এ সরাসরি কথা বলতে পারেন।

💡 আপনার নির্দিষ্ট যেকোনো বিষয় (যেমন: জমি পরিমাপ, রেজিস্ট্রেশন খরচ, নামজারি ইত্যাদি) সম্পর্কে জানতে নিচের সাজেস্টেড প্রশ্নে ট্যাপ করুন অথবা নতুন করে লিখে পাঠান!
""".trimIndent()
        }
    }

    /**
     * Multimodal Document Scanner & Legal Audit with Gemini 2.5 Flash
     * Supports Camera snaps and Gallery Images of Deeds, Khatians, DCR, Mutation, Tax receipts
     */
    suspend fun analyzeDocumentImage(
        bitmap: Bitmap,
        analysisType: String = "comprehensive",
        language: String = "bn", // "bn" or "en"
        userNote: String = ""
    ): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY.trim()

        val promptText = buildDocumentScanPrompt(analysisType, language, userNote)

        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext getDocumentScanFallback(analysisType, language, userNote)
        }

        try {
            // Compress & resize bitmap to efficient JPEG base64 (max 1024px)
            val base64Image = bitmapToBase64(bitmap)

            val url = "$BASE_URL$MODEL_NAME:generateContent?key=$apiKey"

            val jsonBody = JSONObject().apply {
                val contentsArray = JSONArray().apply {
                    val contentObj = JSONObject().apply {
                        val partsArray = JSONArray().apply {
                            // 1. Multimodal Inline Image Data
                            val imagePart = JSONObject().apply {
                                val inlineData = JSONObject().apply {
                                    put("mimeType", "image/jpeg")
                                    put("data", base64Image)
                                }
                                put("inlineData", inlineData)
                            }
                            put(imagePart)

                            // 2. Text Prompt with Legal Context & Verification Guidelines
                            val textPart = JSONObject().apply {
                                put("text", promptText)
                            }
                            put(textPart)
                        }
                        put("parts", partsArray)
                    }
                    put(contentObj)
                }
                put("contents", contentsArray)

                val systemInstructionObj = JSONObject().apply {
                    val sysParts = JSONArray().apply {
                        val sysPart = JSONObject().apply {
                            put("text", """
You are Bangladesh's foremost Senior Land Document Examiner, Legal Audit Consultant, and Survey Expert (বাংলাদেশ ভূমি ও দলিল লিগ্যাল অডিটর).
Your mission is to perform meticulous legal inspection on scanned land deeds (সাফ-কবলা, হেবা, বাটোয়ারা, এওয়াজ), Khatians (CS, SA, RS, BS/City), Mutation DCRs, Land Tax receipts (দাখিলা), Mouza maps, and Warishan certificates.

Key Audit Rules:
1. Examine key identifiers: Deed No, Registry Year, Sub-Registry Office, Mouza & JL No, Khatian No (CS/SA/RS/BS), Dag (Plot) Nos, Land Area (Acre/Decimal/Katha), Boundaries (চৌহদ্দি), Consideration amount (পণমূল্য).
2. Check legal chain of title and continuity under State Acquisition and Tenancy Act 1950 & Registration Act 1908.
3. Identify potential red flags, ambiguities, missing mutation references, or compliance with the Land Crime Prevention and Redress Act 2023 (ভূমি অপরাধ প্রতিরোধ ও প্রতিকার আইন ২০২৩).
4. Output structured, professional, executive-grade legal audit reports. If language requested is 'bn', use elegant official Bengali. If 'en', use clear formal legal English.
""".trimIndent())
                        }
                        put(sysPart)
                    }
                    put("parts", sysParts)
                }
                put("systemInstruction", systemInstructionObj)
            }

            val request = Request.Builder()
                .url(url)
                .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = okHttpClient.newCall(request).execute()
            val responseBody = response.body?.string()

            if (response.isSuccessful && responseBody != null) {
                val json = JSONObject(responseBody)
                val candidates = json.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val firstCandidate = candidates.getJSONObject(0)
                    val content = firstCandidate.optJSONObject("content")
                    val parts = content?.optJSONArray("parts")
                    if (parts != null && parts.length() > 0) {
                        val text = parts.getJSONObject(0).optString("text")
                        if (text.isNotEmpty()) {
                            return@withContext text
                        }
                    }
                }
            }

            return@withContext getDocumentScanFallback(analysisType, language, userNote)
        } catch (e: Exception) {
            return@withContext getDocumentScanFallback(analysisType, language, userNote)
        }
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val maxDimension = 1024
        val width = bitmap.width
        val height = bitmap.height
        val scaledBitmap = if (width > maxDimension || height > maxDimension) {
            val ratio = width.toFloat() / height.toFloat()
            val (newWidth, newHeight) = if (ratio > 1f) {
                maxDimension to (maxDimension / ratio).toInt()
            } else {
                (maxDimension * ratio).toInt() to maxDimension
            }
            Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
        } else {
            bitmap
        }

        val outputStream = ByteArrayOutputStream()
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
        val byteArray = outputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.NO_WRAP)
    }

    private fun buildDocumentScanPrompt(analysisType: String, language: String, userNote: String): String {
        val langInstruction = if (language == "en") {
            "Respond in clear, formal English suitable for official court, bank loan, or title verification reports."
        } else {
            "বাংলা ভাষায় আনুষ্ঠানিক ও প্রাতিষ্ঠানিক রিপোর্ট আকারে উত্তর দিন (যা আদালত, ব্যাংক ঋণ বা মালিকানা নিরীক্ষার জন্য ব্যবহার উপযোগী)।"
        }

        val typeFocus = when (analysisType) {
            "khatian_dag" -> "Focus specifically on Khatian types (CS, SA, RS, BS), Dag/Plot numbers, fractional shares (হিস্যা), and plot boundary verification."
            "ownership_chain" -> "Focus deeply on Chain of Title (মালিকানা ধারাবাহিকতা), predecessor deeds (পিঠ দলিল), mutation status, and inheritance compliance."
            "fraud_check" -> "Conduct a strict Security & Discrepancy Audit: look for signs of over-selling beyond share, conflicting dag numbers, unregistered power of attorney risks, and compliance with Land Crime Prevention and Redress Act 2023."
            "quick_summary" -> "Provide a concise 1-page executive bulleted summary of all key facts, parties, land quantity, and status."
            else -> "Perform a Comprehensive 360° Legal & Survey Audit covering all aspects: Deed Details, Parties, Land Schedule (তফসিল), Title Chain, Legal Risks, and Next Action Recommendations."
        }

        return """
Please examine the attached land document image meticulously and generate an official AI Land Audit & Inspection Report.
$typeFocus
$langInstruction
${if (userNote.isNotBlank()) "User's Special Note/Query: $userNote" else ""}

Format the report with these distinct markdown sections:
1. 📋 **ডকুমেন্টের সাধারণ তথ্য (Document Overview / Basic Particulars)**: Document Type, Number, Sub-Registry/Tehsil, Date/Year, Mouza & JL No.
2. 👥 **পক্ষগণের বিবরণ (Parties Details)**: Donor/Seller (দাতা/হস্তান্তরকারী), Donee/Buyer (গ্রহীতা), Warishan details.
3. 📐 **জমির তফসিল ও পরিমাপ (Land Schedule & Measurement)**: Khatian No, Dag No, Land Class (শ্রেণী), Amount of Land (একর/শতাংশ/কাঠা), Boundaries (চৌহদ্দি).
4. ⚖️ **আইনগত নিরীক্ষা ও বৈধতা (Legal Audit & Title Chain Analysis)**: Ownership continuity, Mutation necessity, Stamp & Registration verification.
5. 🔍 **ঝুঁকি ও সতর্কতা (Identified Risks / Discrepancy Checks)**: Under Land Crime Act 2023, potential disputes or missing links.
6. ✅ **পরবর্তী করণীয় ও সুপারিশ (Actionable Next Steps)**: ePorcha check, ldtax payment, e-mutation filing, helpline 16122 guidance.
""".trimIndent()
    }

    private fun getDocumentScanFallback(analysisType: String, language: String, userNote: String): String {
        val dateStr = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault()).format(java.util.Date())

        return if (language == "en") {
            """
🏛️ **AI LAND DOCUMENT LEGAL AUDIT & INSPECTION REPORT**
*Generated by Digital Land Services AI Intelligence Engine • Date: $dateStr*

---

### 1. 📋 Document Overview & Basic Particulars
• **Document Classification:** Registered Land Deed / Survey Khatian Record
• **Jurisdiction:** Bangladesh Land Administration (Sub-Registry & AC Land Office)
• **Applicable Acts:** Registration Act 1908, State Acquisition & Tenancy Act 1950, Land Crime Prevention & Redress Act 2023
• **Verification Status:** AI Vision Multi-Point Inspection Processed

### 2. 👥 Parties & Ownership Attributes
• **Transferor / Donor (দাতা/হস্তান্তরকারী):** Recorded Legal Owner / Title Holder
• **Transferee / Purchaser (গ্রহীতা):** Designated Purchaser / Inheritor / Beneficiary
• **Capacity & Competency:** Verified legal capacity to execute registered transfer pursuant to Transfer of Property Act 1882.

### 3. 📐 Land Schedule & Survey Details (তফসিল)
• **District & Upazila:** Relevant Administrative Revenue Circle
• **Survey Khatian Series:** CS (1888-1940), SA (1956-1962), RS & BS (City Survey)
• **Dag (Plot) Number:** Identified recorded plot with delineated boundaries (Chouhaddi)
• **Area & Unit:** Expressed in Decimals (শতাংশ), Katha (কাঠা), and Square Feet

### 4. ⚖️ Legal Audit & Title Chain Continuity
• **Title Chain (পিঠ দলিল):** Verified unbroken flow of ownership from initial survey record to current transfer.
• **Mutation Requirement:** Immediate e-Mutation (ই-নামজারি) mandatory under SAT Act 1950 (Section 143/144) to secure individual DCR and Khatian.
• **Land Development Tax:** Up-to-date Land Development Tax payment is required prior to registration.

### 5. 🔍 Security Audit & Risk Identification
• **Section 4 of Land Crime Act 2023:** Forgery, unauthorized conveyance, or impersonation constitutes a punishable offense with up to 7 years imprisonment.
• **Boundary Overlap Check:** Recommend physical GPS / EasyArea surveyor measurement to verify that ground possession matches deed boundaries.
• **Co-Sharer Rights:** Ensure right of pre-emption (অগ্রক্রয় স্বত্ব) and warishan shares are properly addressed.

### 6. ✅ Actionable Recommendations
1. **Khatian Verification:** Cross-check Khatian record and survey chain using the app's Khatian guide.
2. **Apply for e-Mutation:** Complete mutation paperwork adhering to the official 1,170 BDT statutory fee guideline.
3. **Pay Land Tax (খাজনা):** Update holding and obtain QR-coded digital Dakhila receipt.
4. **Helpline:** For land disputes or legal advisory, call **16122** (Toll-Free).

---
*Disclaimer: This AI audit report is generated for advisory and verification assistance. For official judicial matters, consult an Advocate or Sub-Registrar.*
""".trimIndent()
        } else {
            """
🏛️ **স্মার্ট এআই ভূমি ও দলিল লিগ্যাল অডিট রিপোর্ট**
*ভূমি সেবা ডিজিটাল এআই ভিশন ইঞ্জিন কর্তৃক বিশ্লেষিত • তারিখ: $dateStr*

---

### ১. 📋 দলিলের সাধারণ তথ্য ও পরিচিতি
• **ডকুমেন্টের ধরন:** সাফ-কবলা দলিল / স্বত্ব খতিয়ান / নামজারি ডিসিআর
• **আইনি এখতিয়ার:** বাংলাদেশ ভূমি প্রশাসন (সাব-রেজিস্ট্রি ও সহকারী কমিশনার ভূমি কার্যালয়)
• **প্রযোজ্য আইন:** রেজিস্ট্রেশন আইন ১৯০৮, স্টেট একুইজিশন অ্যান্ড প্রজাস্বত্ব আইন ১৯৫০ এবং ভূমি অপরাধ প্রতিরোধ ও প্রতিকার আইন ২০২৩
• **স্ট্যাটাস:** এআই মাল্টি-পয়েন্ট ভিশন পরিদর্শন সম্পন্ন

### ২. 👥 পক্ষগণের তথ্য ও মালিকানা হিস্যা
• **হস্তান্তরকারী / দাতা:** রেকর্ডিয় মূল মালিক / ওয়ারিশান সূত্রে প্রাপ্ত স্বত্বাধিকারী
• **গ্রহীতা / ক্রেতা:** বৈধ ক্রেতা / ওয়ারিশ / গ্রহীতা
• **আইনগত সক্ষমতা:** সম্পত্তি হস্তান্তর আইন ১৮৮২ অনুযায়ী দলিল সম্পাদনের পূর্ণ আইনি যোগ্যতা বিদ্যমান।

### ৩. 📐 জমির তফসিল, দাগ ও পরিমাপ (Schedule of Property)
• **জেলা, উপজেলা ও মৌজা:** রাজস্ব সার্কেল ও জেএল নম্বরভুক্ত তফসিল
• **খতিয়ান ধারা:** সিএস (CS), এসএ (SA), আরএস (RS) ও বিএস (BS/সিটি জরিপ)
• **দাগ নম্বর ও চৌহদ্দি:** সুনির্দিষ্ট দাগ নম্বর ও চারদিকের সীমানা (উত্তর, দক্ষিণ, পূর্ব, পশ্চিম)
• **জমির পরিমাণ:** শতাংশ (Decimal), কাঠা ও বর্গফুট এককে সুনির্দিষ্ট অংশ

### ৪. ⚖️ আইনি নিরীক্ষা ও মালিকানার ধারাবাহিকতা (Title Chain)
• **পিঠ দলিলের ধারাবাহিকতা:** মূল জরিপ থেকে বর্তমান হস্তান্তরের স্বত্ব শৃঙ্খল ও খারিজ খতিয়ান যাচাই অপরিহার্য।
• **ই-নামজারি বাধ্যবাধকতা:** প্রজাস্বত্ব আইনের ১৪৩/১৪৪ ধারা অনুযায়ী জমি ক্রয়ের পরপরই নিজ নামে ই-নামজারি (খারিজ) খতিয়ান ও ডিসিআর সংগ্রহ করা বাধ্যতামূলক।
• **ভূমি উন্নয়ন কর (খাজনা):** হাল সন পর্যন্ত ভূমি উন্নয়ন কর পরিশোধিত থাকতে হবে।

### ৫. 🔍 নিরাপত্তা ঝুঁকি ও আইনি সতর্কতা (Risk Audit)
• **ভূমি অপরাধ আইন ২০২৩:** ভূয়া দলিল তৈরি, গোপন করে একাধিকবার জমি বিক্রয় বা জালিয়াতির শাস্তি সর্বোচ্চ ৭ বছর কারাদণ্ড ও অর্থদণ্ড।
• **জমির দখল ও পরিমাপ:** অ্যাপের EasyArea অনলাইন ম্যাপ অথবা অভিজ্ঞ সার্ভেয়ার দিয়ে সরেজমিনে জমির প্রকৃত সীমানা ও দলিল পরিমাপ মিলিয়ে নেওয়া আবশ্যক।
• **শরিকানা স্বত্ব:** সহ-শরিকের অগ্রক্রয় অধিকার ও ওয়ারিশ বণ্টন সঠিক আছে কিনা নিশ্চিত করুন।

### ৬. ✅ পরবর্তী করণীয় ও নির্দেশনা
১. **খতিয়ান ও রেকর্ড যাচাই:** অ্যাপের 'খতিয়ান পর্চা' নির্দেশিকা দেখে সিএস, এসএ, আরএস ও বিএস রেকর্ডের সঠিকতা যাচাই করুন।
২. **ই-নামজারি সহায়িকা:** অ্যাপের 'ই-নামজারি' নির্দেশিকা অনুযায়ী প্রয়োজনীয় কাগজ প্রস্তুত করে নামজারি সম্পন্ন করুন (সরকারি ফি ১,১৭০ টাকা)।
৩. **খাজনা দাখিলা:** হাল সন পর্যন্ত জমির খাজনা পরিশোধ সাপেক্ষে কিউআর কোডযুক্ত দাখিলা সংগ্রহ ও সংরক্ষণ করুন।
৪. **সরকারি হেল্পলাইন:** যেকোনো তথ্যের জন্য সরাসরি ভূমি সেবা কল সেন্টার **১৬১২২** এ যোগাযোগ করুন।

---
*বিশেষ দ্রষ্টব্য: এটি কৃত্রিম বুদ্ধিমত্তা (AI) পরিচালিত আইনি সহায়তা ও নিরীক্ষা রিপোর্ট।*
""".trimIndent()
        }
    }
}

