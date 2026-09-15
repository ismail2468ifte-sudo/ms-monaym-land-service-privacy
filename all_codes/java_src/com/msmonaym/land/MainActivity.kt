package com.msmonaym.land

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.msmonaym.land.ui.about.AboutScreen
import com.msmonaym.land.ui.calculator.LandCalculatorScreen
import com.msmonaym.land.ui.contact.ContactScreen
import com.msmonaym.land.ui.home.HomeScreen
import com.msmonaym.land.ui.khatian.KhatianScreen
import com.msmonaym.land.ui.law.LawScreen
import com.msmonaym.land.ui.navigation.Screen
import com.msmonaym.land.ui.registration.RegistrationScreen
import com.msmonaym.land.ui.search.SearchScreen
import com.msmonaym.land.ui.security.LockScreen
import com.msmonaym.land.ui.services.ServicesScreen
import com.msmonaym.land.ui.settings.SettingsScreen
import com.msmonaym.land.ui.splash.SplashScreen
import com.msmonaym.land.ui.theme.MSMonaymTheme
import com.msmonaym.land.ui.webview.LandWebViewScreen

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MSMonaymTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    LandAppNavigation()
                }
            }
        }
    }
}

@Composable
fun LandAppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Lock.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Lock.route) {
            LockScreen(
                onUnlockSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Lock.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onNavigate = { screen ->
                    navController.navigate(screen.route)
                }
            )
        }

        composable(Screen.Law.route) {
            LawScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Khatian.route) {
            KhatianScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Porcha.route) {
            LandWebViewScreen(
                title = "ই-পর্চা (e-Porcha Portal)",
                url = "https://eporcha.gov.bd",
                instructions = listOf(
                    "বিভাগ, জেলা, উপজেলা ও মৌজা নির্বাচন করুন",
                    "খতিয়ান টাইপ (CS/SA/RS/BS) নির্বাচন করুন",
                    "খতিয়ান বা দাগ নম্বর প্রদান করে তথ্য মিলিয়ে নিন",
                    "সার্টিফাইড কপি নিতে অনলাইন পেমেন্ট করুন"
                ),
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Mutation.route) {
            LandWebViewScreen(
                title = "ই-নামজারি (e-Mutation Portal)",
                url = "https://mutation.land.gov.bd",
                instructions = listOf(
                    "এনআইডি ও মোবাইল নম্বর দিয়ে সাইন ইন করুন",
                    "হস্তান্তর দলিলে উল্লিখিত দাগ ও খতিয়ান নম্বর ইনপুট দিন",
                    "খাজনা রশিদ ও প্রয়োজনীয় কাগজ আপলোড করুন",
                    "আবেদন ফি ৭০ টাকা পেমেন্ট করে ট্র্যাকিং আইডি সংগ্রহ করুন"
                ),
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Tax.route) {
            LandWebViewScreen(
                title = "ভূমি উন্নয়ন কর (Land Tax)",
                url = "https://ldtax.gov.bd",
                instructions = listOf(
                    "মোবাইল নম্বর ও এনআইডি দিয়ে নিবন্ধন করুন",
                    "হোল্ডিং নম্বর সিলেক্ট করে কর দাবি চেক করুন",
                    "বিকাশ/রকেট/নগদের মাধ্যমে খাজনা পরিশোধ করুন"
                ),
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Registration.route) {
            RegistrationScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Services.route) {
            ServicesScreen(
                onSelectService = { service ->
                    when (service.id) {
                        "eporcha" -> navController.navigate(Screen.Porcha.route)
                        "emutation" -> navController.navigate(Screen.Mutation.route)
                        "ldtax" -> navController.navigate(Screen.Tax.route)
                        else -> navController.navigate(Screen.Porcha.route)
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Contact.route) {
            ContactScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Calculator.route) {
            LandCalculatorScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Search.route) {
            SearchScreen(
                onNavigateToScreen = { screen ->
                    navController.navigate(screen.route)
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.About.route) {
            AboutScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
