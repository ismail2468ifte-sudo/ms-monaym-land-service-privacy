package com.msmonaym.land.ui.deed

/**
 * Represents the various types of land deeds used in Bangladesh Sub-Registry offices.
 */
enum class DeedCategory(
    val id: String,
    val titleBangla: String,
    val subtitleBangla: String,
    val emoji: String,
    val standardStampFee: String,
    val shortCode: String
) {
    SAF_KABALA(
        id = "saf_kabala",
        titleBangla = "সাফ-কবলা দলিল (বিক্রয়)",
        subtitleBangla = "জমি বা স্থাবর সম্পত্তি চূড়ান্ত বিক্রয় ও স্বত্ব হস্তান্তর দলিল",
        emoji = "📜",
        standardStampFee = "১.৫% স্ট্যাম্প শুল্ক + অন্যান্য ফি",
        shortCode = "কবলা"
    ),
    FLAT_BAYNAMA(
        id = "flat_baynama",
        titleBangla = "ফ্লাট রেজিস্ট্রি বায়না পত্র",
        subtitleBangla = "রিয়েল এস্টেট আইন ২০১০ অনুযায়ী ফ্ল্যাট ক্রয়ের রেজিস্ট্রি বায়নাপত্র ফরম",
        emoji = "🏢",
        standardStampFee = "মূল্য অনুযায়ী নির্ধারিত স্ট্যাম্প শুল্ক ও বায়না ফি",
        shortCode = "ফ্ল্যাট বায়না"
    ),
    FLAT_SAF_KABALA(
        id = "flat_saf_kabala",
        titleBangla = "ফ্লাট সাব-কাওলা রেজিস্ট্রি দলিল",
        subtitleBangla = "ফ্ল্যাট, অ্যাপার্টমেন্ট ও অবিভক্ত জমির চূড়ান্ত বিক্রয় ও মালিকানা হস্তান্তর",
        emoji = "🏙️",
        standardStampFee = "স্ট্যাম্প শুল্ক ১.৫% + গেইন ট্যাক্স ও রেজিস্ট্রেশন ফি",
        shortCode = "ফ্ল্যাট কবলা"
    ),
    HEBA_DANPATRO(
        id = "heba_danpatro",
        titleBangla = "হেবা / দানপত্র দলিল (রক্তসম্পর্কীয়)",
        subtitleBangla = "পিতা-মাতা, সন্তান, ভাই-বোন, স্বামী-স্ত্রীর মাঝে নিঃশর্ত দানপত্র",
        emoji = "🎁",
        standardStampFee = "ফিক্সড স্ট্যাম্প ২০০/- ও রেজিস্ট্রেশন ফি ১০০/-",
        shortCode = "হেবা"
    ),
    HEBA_BIL_EWAZ(
        id = "heba_bil_ewaz",
        titleBangla = "হেবা-বিল-এওয়াজ দলিল",
        subtitleBangla = "দেনমোহর বা বিশেষ প্রতিদানের বিনিময়ে প্রদত্ত হেবা দলিল",
        emoji = "💍",
        standardStampFee = "ফিক্সড রেজিস্ট্রেশন ও স্ট্যাম্প নিয়মাবলী",
        shortCode = "বিল-এওয়াজ"
    ),
    BAYNAMA(
        id = "baynama",
        titleBangla = "বায়নাপত্র দলিল (চুক্তিপত্র)",
        subtitleBangla = "জমি ক্রয়ের বায়না ও নির্দিষ্ট সময়ে রেজিস্ট্রি সম্পন্ন করার অঙ্গীকারনামা",
        emoji = "🤝",
        standardStampFee = "মূল্যভেদে ফিক্সড স্ট্যাম্প শুল্ক",
        shortCode = "বায়না"
    ),
    PARTITION_BONTAN(
        id = "partition_bontan",
        titleBangla = "আপোষ বন্টননামা দলিল (হিস্যা)",
        subtitleBangla = "ওয়ারিশ বা সহ-শরীকগণের মাঝে ফারায়েজ ও নিজ নিজ দাগ বণ্টন",
        emoji = "⚖️",
        standardStampFee = "মূল্য অনুপাতে স্ট্যাম্প ও ফি",
        shortCode = "বণ্টন"
    ),
    POWER_OF_ATTORNEY(
        id = "power_of_attorney",
        titleBangla = "আমমোক্তারনামা দলিল (পাওয়ার অব এটর্নি)",
        subtitleBangla = "জমি দেখাশোনা, খারিজ বা বিক্রয়ের আইনগত ক্ষমতা অর্পণ দলিল",
        emoji = "📑",
        standardStampFee = "অপ্রত্যাহারযোগ্য বা সাধারণ পাওয়ার ফি",
        shortCode = "পাওয়ার"
    ),
    EXCHANGE_EWAZ(
        id = "exchange_ewaz",
        titleBangla = "এওয়াজ বদল দলিল (অদল-বদল)",
        subtitleBangla = "পারস্পরিক সমমূল্য বা সুবিধাজনক জমি বিনিময়ের দলিল",
        emoji = "🔄",
        standardStampFee = "সর্বোচ্চ মূল্যের সম্পত্তির ভিত্তিতে স্ট্যাম্প",
        shortCode = "এওয়াজ"
    ),
    RELINQUISHMENT_NADABI(
        id = "relinquishment_nadabi",
        titleBangla = "না-দাবি দলিল (স্বত্ব ত্যাগ)",
        subtitleBangla = "ওয়ারিশি বা সম্পত্তির অংশ থেকে স্বেচ্ছায় নিজের দাবি ত্যাগপত্র",
        emoji = "🕊️",
        standardStampFee = "স্ট্যাম্প শুল্ক ও রেজিস্ট্রেশন ফি প্রযোজ্য",
        shortCode = "না-দাবি"
    ),
    WILL_OCHIYOT(
        id = "will_ochiyot",
        titleBangla = "অছিয়তনামা / উইল দলিল",
        subtitleBangla = "জীবদ্দশায় লিখিত ও মৃত্যুর পর কার্যকরী হওয়ার অন্তিম ইচ্ছাপত্র",
        emoji = "📖",
        standardStampFee = "উইল রেজিস্ট্রি ফিক্সড ফি ৩০০/-",
        shortCode = "অছিয়ত"
    ),
    MORTGAGE_BONDHOK(
        id = "mortgage_bondhok",
        titleBangla = "খাই-খালাসী বন্ধকী দলিল",
        subtitleBangla = "নির্দিষ্ট মেয়াদ বা টাকার বিপরীতে জমি ভোগদখল বন্ধক দলিল",
        emoji = "🔒",
        standardStampFee = "ঋণের পরিমাণের ভিত্তিতে স্ট্যাম্প ফি",
        shortCode = "বন্ধক"
    )
}

/**
 * Data container holding form fields used to populate standard deed templates.
 */
data class DeedFormData(
    val deedCategory: DeedCategory = DeedCategory.SAF_KABALA,
    val subRegistryOffice: String = "সদর সাব-রেজিস্ট্রি অফিস",
    val upazilaOrThana: String = "সদর",
    val district: String = "ঢাকা",

    // ১ম পক্ষ (দাতা / বিক্রেতা)
    val firstPartyRole: String = "১ম পক্ষ (দলিল দাতা / বিক্রেতা)",
    val firstPartyName: String = "মোঃ রফিকুল ইসলাম",
    val firstPartyFatherOrHusband: String = "মরহুম আব্দুল খালেক",
    val firstPartyMother: String = "মোসাঃ রহিমা খাতুন",
    val firstPartyNid: String = "19852694512345678",
    val firstPartyMobile: String = "01711000000",
    val firstPartyReligion: String = "ইসলাম",
    val firstPartyNationality: String = "বাংলাদেশী",
    val firstPartyAddress: String = "গ্রাম: বাঘাসুর, ডাকঘর: কলাতিয়া, উপজেলা: কেরানীগঞ্জ, জেলা: ঢাকা",

    // ২য় পক্ষ (গ্রহীতা / ক্রেতা)
    val secondPartyRole: String = "২য় পক্ষ (দলিল গ্রহীতা / ক্রেতা)",
    val secondPartyName: String = "তানভীর আহমেদ",
    val secondPartyFatherOrHusband: String = "মোঃ সিরাজুল হক",
    val secondPartyMother: String = "মোসাঃ ফাতেমা বেগম",
    val secondPartyNid: String = "19922694598765432",
    val secondPartyMobile: String = "01811000000",
    val secondPartyReligion: String = "ইসলাম",
    val secondPartyNationality: String = "বাংলাদেশী",
    val secondPartyAddress: String = "বাড়ি নং- ১২, রোড নং- ৫, ধানমন্ডি, ঢাকা- ১২০৯",

    // সম্পত্তির আর্থিক তথ্য
    val deedValueTaka: String = "১২,৫০,০০০/-",
    val deedValueInWords: String = "বারো লক্ষ পঞ্চাশ হাজার টাকা মাত্র",
    val advancePaidTaka: String = "২,০০,০০০/-", // বায়না দলিলের জন্য

    // সম্পত্তির তফসিল
    val mouzaName: String = "বাঘাসুর",
    val jlNumber: String = "৪৫",
    val khatianTypes: String = "সিএস- ১০২, এসএ- ১২৫, আরএস- ২১৫, বিএস- ৩৪৮",
    val dagNumbers: String = "সাবেক দাগ- ১২০, হাল দাগ- ২৪৫",
    val landClass: String = "নাল / বাস্তু ভিটি",
    val totalDagLandAmount: String = "৫০ শতাংশ",
    val transferredLandAmount: String = "১০ (দশ) শতাংশ",

    // চৌহদ্দি
    val chouhaddiNorth: String = "মোঃ করিম মিয়ার নাল জমি",
    val chouhaddiSouth: String = "১২ ফুট প্রশস্ত সরকারি পাকা রাস্তা",
    val chouhaddiEast: String = "আব্দুর রহিম মুন্সীর বাড়ি",
    val chouhaddiWest: String = "১ম পক্ষ দলিল দাতার অবশিষ্ট জমি",

    // ফ্ল্যাট ও অ্যাপার্টমেন্ট অটো-ফিল বিবরণ (বাংলাদেশের বর্তমান রিয়েল এস্টেট ও রেজিস্ট্রেশন আইন অনুযায়ী)
    val buildingProjectName: String = "গ্রীন ভ্যালি হাইটস প্রজেক্ট (ডেভেলপার)",
    val flatNumberAndFloor: String = "ফ্ল্যাট নং- ৫/বি (৬ষ্ঠ তলা)",
    val flatSizeSqFt: String = "১৩৫০ বর্গফুট (কমন স্পেস ও ৩টি বারান্দা সহ)",
    val carParkingSpace: String = "গ্রাউন্ড ফ্লোরে ১টি নির্দিষ্ট কার পার্কিং স্পেস (নং- CP-05)",
    val undividedLandShare: String = "অবিভক্ত ও অচিহ্নিত ০.৮৫ অযুতাংশ/শতাংশ জমি শেয়ার",
    val approvalPlanMemoAndDate: String = "রাজউক/সিডিএ স্মারক নং- ২৫.৩৯.০০০০.০১২.২০২২ তারিখ: ১২/০২/২০২২",
    val flatHandoverDate: String = "আগামী ৩১শে ডিসেম্বর ২০২৬ খ্রিষ্টাব্দ",
    val installmentPaymentDetails: String = "বায়না বাবদ নগদ/পে-অর্ডার নং- 4829105, ডাচ-বাংলা ব্যাংক লিমিটেড",
    val utilityDetails: String = "বিদ্যুৎ মিটার, গভীর নলকূপ, লিফট ও জেনারেটরের কমন সুবিধাদি সমেত",

    // শনাক্তকারী ও সাক্ষী
    val identifierName: String = "মোঃ আবুল কাশেম (সনাক্তকারী)",
    val witnessNames: String = "১. মোঃ ফারুক হোসেন, ২. সেলিম রেজা",
    val deedWriterInfo: String = "দলিল প্রস্তুতকারক: সনদপ্রাপ্ত দলিল লেখক (মুহুরি), লাইসেন্স নং- ১২৪/২০২০"
)

/**
 * Saved draft metadata for local draft history.
 */
data class SavedDeedDraft(
    val id: String,
    val title: String,
    val categoryId: String,
    val lastUpdated: Long,
    val textContent: String
)
