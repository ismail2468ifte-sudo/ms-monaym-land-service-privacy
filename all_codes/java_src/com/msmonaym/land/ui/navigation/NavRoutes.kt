package com.msmonaym.land.ui.navigation

sealed class Screen(val route: String, val title: String, val iconEmoji: String) {
    object Splash : Screen("splash", "স্প্ল্যাশ", "🚀")
    object Lock : Screen("lock", "সিকিউরিটি কোড", "🔒")
    object Home : Screen("home", "হোম", "🏠")
    object Law : Screen("law", "জমি আইন", "📖")
    object Khatian : Screen("khatian", "খতিয়ান", "📜")
    object Porcha : Screen("porcha", "ই-পর্চা", "📄")
    object Mutation : Screen("mutation", "ই-নামজারি", "🏛️")
    object Tax : Screen("tax", "ভূমি কর", "💰")
    object Registration : Screen("registration", "রেজিস্ট্রেশন", "📝")
    object Services : Screen("services", "সরকারি সেবা", "🌐")
    object Contact : Screen("contact", "যোগাযোগ", "📞")
    object Calculator : Screen("calculator", "জমি মাপজোক", "📐")
    object Search : Screen("search", "সার্চ", "🔍")
    object About : Screen("about", "আমাদের সম্পর্কে", "ℹ️")
    object Settings : Screen("settings", "সেটিংস", "⚙️")
}
