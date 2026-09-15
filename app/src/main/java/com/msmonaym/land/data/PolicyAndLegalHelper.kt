package com.msmonaym.land.data

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.msmonaym.land.ui.theme.GoldAccent
import com.msmonaym.land.ui.theme.LandGreenContainer
import com.msmonaym.land.ui.theme.LandGreenDark
import com.msmonaym.land.ui.theme.LandGreenPrimary

object PolicyAndLegalContent {
    const val GOVERNMENT_DISCLAIMER_TITLE = "সরকারি অস্বীকৃতি ও তথ্যের উৎস (Government Disclaimer)"
    
    const val GOVERNMENT_DISCLAIMER_BODY = """M.S MONAYM ENT. একটি সম্পূর্ণ স্বাধীন, স্বত্বাধিকারী ভিত্তিক বেসরকারি আইনি ও প্রযুক্তিগত সহায়ক ডিজিটাল প্ল্যাটফর্ম।

⚠️ গুরুত্বপূর্ণ ঘোষণা:
১. এই অ্যাপ্লিকেশনটি গণপ্রজাতন্ত্রী বাংলাদেশ সরকার বা ভূমি মন্ত্রণালয় কিংবা অন্য কোনো সরকারি দপ্তরের অফিশিয়াল বা প্রতিনিধিত্বকারী অ্যাপ নয়।
২. এই অ্যাপের মাধ্যমে কোনো সরকারি অফিসিয়াল সার্ভিস সরাসরি প্রদান করা হয় না; বরং নাগরিকদের ভূমি পরিমাপ, রেজিস্ট্রেশন খরচ হিসাব, দলিল খসড়া তৈরি এবং সরকারি তথ্যের সহজ সহায়িকা হিসেবে টুলটি কাজ করে।
৩. অ্যাপটিতে উল্লিখিত তথ্য, ফিস ও বিধিমালার মূল পাবলিক উৎসসমূহ:
   • ভূমি মন্ত্রণালয় বাংলাদেশ: https://minland.gov.bd
   • জাতীয় ই-পর্চা পোর্টাল: https://eporcha.gov.bd
   • ডিজিটাল ভূমি উন্নয়ন কর: https://ldtax.gov.bd
   • বাংলাদেশ আইন ও সংসদ বিষয়ক বিভাগ: http://bdlaws.minlaw.gov.bd
   • জাতীয় তথ্য বাতায়ন: https://bangladesh.gov.bd"""

    const val PRIVACY_POLICY_TITLE = "গোপনীয়তা নীতি ও ডাটা নিরাপত্তা (Privacy Policy)"

    const val PRIVACY_POLICY_BODY = """M.S MONAYM ENT. ব্যবহারকারীদের ব্যক্তিগত গোপনীয়তা ও তথ্যের নিরাপত্তাকে সর্বোচ্চ গুরুত্ব দেয়।

১. তথ্য সংগ্রহ ও প্রক্রিয়াকরণ:
• ব্যবহারকারীর নাম ও মোবাইল নম্বর কেবল মাত্র অ্যাপের অভ্যন্তরে প্রোফাইল সুবিধার্থে ব্যবহার করা হয়।
• এই তথ্য সম্পূর্ণ নিরাপদভাবে ব্যবহারকারীর নিজস্ব ডিভাইসে (Local Storage) সংরক্ষিত থাকে।

২. ডিভাইস পারমিশন ও ব্যবহার:
• ক্যামেরা (Camera): কেবল দলিল বা খতিয়ানের হার্ডকপি স্ক্যান এবং OCR এর জন্য ডিভাইসের মধ্যে ব্যবহার হয়।
• লোকেশন (GPS): স্যাটেলাইটের মাধ্যমে সরাসরি জমির পরিমাপ ও সীমানা ট্র্যাক করার জন্য ব্যবহৃত হয়।
• ফাইল ও মেমোরি: প্রস্তুতকৃত দলিল বা জমি জরিপের রসিদ ও PDF সেভ করার জন্য অ্যান্ড্রয়েড স্কোপড স্টোরেজ ব্যবহৃত হয়।

৩. তথ্য শেয়ারিং ও বিক্রয় নিষেধাজ্ঞা:
আমরা নিশ্চিত করছি যে, কোনো ব্যবহারকারীর ব্যক্তিগত তথ্য, অনুসন্ধানের ইতিহাস বা দলিল সংক্রান্ত তথ্য কোনো তৃতীয় পক্ষের কাছে বিক্রয়, হস্তান্তর বা বিপণনে ব্যবহার করা হয় না।

৪. নীতিমালার হালনাগাদ ও যোগাযোগ:
এই গোপনীয়তা নীতি গুগল প্লে স্টোরের নিরাপত্তা নীতিমালার আলোকে প্রস্তুত। যেকোনো জিজ্ঞাসা বা অভিযোগের জন্য যোগাযোগ করুন:
📧 অফিসিয়াল ইমেইল: msmonaymenterprise@gmail.com
📞 হটলাইন: ০১৯৭৬৪৪৪৫০৪
🏛️ M.S MONAYM ENT. - সমগ্র বাংলাদেশ"""
}

@Composable
fun PrivacyPolicyDialog(
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = LandGreenPrimary),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("আমি সম্মত ও অবগত", fontWeight = FontWeight.Bold)
            }
        },
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Shield,
                    contentDescription = null,
                    tint = LandGreenPrimary,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "গোপনীয়তা ও প্লে-স্টোর ডিসক্লেইমার",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = LandGreenDark
                )
            }
        },
        text = {
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 420.dp)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Disclaimer Box
                Surface(
                    color = Color(0xFFFEF3C7),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFFF59E0B)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Gavel, contentDescription = null, tint = Color(0xFF92400E), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "সরকারি অস্বীকৃতি (Legal Disclaimer)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.5.sp,
                                color = Color(0xFF92400E)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = PolicyAndLegalContent.GOVERNMENT_DISCLAIMER_BODY,
                            fontSize = 11.5.sp,
                            lineHeight = 17.sp,
                            color = Color(0xFF78350F)
                        )
                    }
                }

                // Privacy Policy Box
                Surface(
                    color = Color(0xFFF0FDF4),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, LandGreenPrimary.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = LandGreenDark, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "গোপনীয়তা নীতি (Privacy Policy)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.5.sp,
                                color = LandGreenDark
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = PolicyAndLegalContent.PRIVACY_POLICY_BODY,
                            fontSize = 11.5.sp,
                            lineHeight = 17.sp,
                            color = Color(0xFF166534)
                        )
                    }
                }
            }
        },
        shape = RoundedCornerShape(18.dp),
        containerColor = Color.White
    )
}
