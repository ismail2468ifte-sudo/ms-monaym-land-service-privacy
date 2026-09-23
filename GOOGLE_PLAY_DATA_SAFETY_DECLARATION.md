# Google Play Console Data Safety Form Guide for M.S MONAYM ENT.
Package Name: `com.msmonaym.land`
App Name: `M.S MONAYM ENT.`
Target Version: v14.0.0 (versionCode: 14, Target SDK 36, Android 16 Ready)

গুগল প্লে কনসোলে (Google Play Console) অ্যাপ জমা দেওয়ার সময় **"App content > Data safety"** ও **"Government apps"** সেকশনে নিচের ছক ও উত্তর অনুযায়ী পূরণ করলে আপনার AAB রিজেকশন ছাড়াই ১০০% নির্ভুলভাবে পাশ করবে:

---

## ১. Data collection and security (সাধারণ প্রশ্নাবলি)
1. **Does your app collect or share any of the required user data types?**
   - **Answer: Yes**
2. **Is all of the user data collected by your app encrypted in transit?**
   - **Answer: Yes** (All network traffic uses HTTPS / TLS 1.3)
3. **Do you provide a way for users to request that their data be deleted?**
   - **Answer: Yes** (ব্যবহারকারী সেটিংস থেকে ডেটা ক্লিয়ার করতে পারেন বা ডেভেলপার ইমেইলে অনুরোধ করতে পারেন)

---

## ২. Data types & Actual Data Behavior (প্রকৃত ডেটা আচরণ)

### A. Location (লোকেশন)
- **Approximate location / Precise location:**
  - **Collected?**: Yes
  - **Shared?**: No
  - **Ephemeral (ক্ষণস্থায়ী) প্রসেসিং?**: Yes (কেবলমাত্র জমি পরিমাপ ও দাগ নম্বর নির্ধারণের সময় অ্যাপ চলাকালীন ব্যবহৃত হয়)
  - **Is this data required or optional?**: Optional
  - **Purpose**: App functionality (জমি ও প্লটের সীমানা পরিমাপ)

### B. Personal info (ব্যক্তিগত তথ্য)
- **Name / Phone number:**
  - **Collected?**: Yes
  - **Shared?**: No (জিরো শেয়ারিং, অন-ডিভাইস SharedPreferences-এ সেভ থাকে)
  - **Is this data required or optional?**: Optional (ব্যবহারকারী নিজের প্রোফাইল ও দলিলে নাম রাখতে চাইলে)
  - **Purpose**: Account management / App functionality

### C. Photos and videos (ছবি ও ডকুমেন্টস)
- **Photos / Documents:**
  - **Collected?**: Yes (ব্যবহারকারী ক্যামেরা দিয়ে দলিলের ছবি তুললে বা গ্যালারি থেকে সিলেক্ট করলে)
  - **Shared?**: No (কোনো বিজ্ঞাপন বা তৃতীয় পক্ষের সাথে শেয়ার হয় না)
  - **Ephemeral?**: Yes (Gemini AI Vision দ্বারা দলিলের তথ্য প্রসেসিং ও পিডিএফ জেনারেটের জন্য)
  - **Purpose**: App functionality (দলিল অডিট ও স্ক্যানিং)

### D. Financial info (আর্থিক তথ্য)
- **Other financial info (Transaction ID):**
  - **Collected?**: Yes (ব্যবহারকারী প্রিমিয়াম সেবার বুকিংয়ের সময় TrxID দিলে)
  - **Shared?**: No
  - **Purpose**: Purchase management / Fraud prevention

### E. Biometric data / Health & Fitness / Contacts / Web browsing / Device IDs:
- **Answer: NO** (কোনো বায়োমেট্রিক ডেটা অ্যাপে জমা হয় না, BiometricPrompt সিস্টেম হ্যান্ডেল করে; কোনো অ্যাডভার্টাইজিং ট্র্যাকার বা থার্ড পার্টি অ্যানালিটিক্স নেই)।

---

## ৩. Government Apps Policy Declaration (সরকারি অ্যাপস পলিসি)
গুগল প্লে কনসোলের **"App content > Government apps"** প্রশ্নে:
- **"Is your app developed by or on behalf of a government?"**: **NO** (না, এটি স্বাধীন বেসরকারি অ্যাপ)
- **সরকারি তথ্যের মূল উৎসসমূহ (Primary Sources of Government Information)**:
  1. জাতীয় ভূমি সেবা পোর্টাল (Ministry of Land): `https://www.land.gov.bd`
  2. ভূমি রেকর্ড ও জরিপ অধিদপ্তর (DLR&S): `https://dlrs.gov.bd`
  3. ভূমি মন্ত্রণালয় বাংলাদেশ: `https://minland.gov.bd`
  4. জাতীয় ই-পর্চা সেবা: `https://eporcha.gov.bd`
  5. ডিজিটাল ভূমি উন্নয়ন কর: `https://ldtax.gov.bd`
  6. ই-নামজারি সিস্টেম: `https://mutation.land.gov.bd`
  7. বাংলাদেশ আইন বাতায়ন: `http://bdlaws.minlaw.gov.bd`

---

## ৪. Privacy Policy URL
গুগল প্লে কনসোলে আপনার প্রাইভেসি পলিসি লিংক হিসেবে আপনার লাইভ ডোমেন বা অ্যাপের URL দিতে পারেন:
`https://ais-pre-2pbv6u37on6ftvr6eh3ut3-773309380409.asia-east1.run.app/assets/privacy-policy.html`
(অথবা অ্যাপের ভিতরের সেটিংস ও 'আমাদের সম্পর্কে' পেজ থেকে সরাসরি যেকোনো সময় অফলাইনেও এটি পড়তে পারবেন)।
