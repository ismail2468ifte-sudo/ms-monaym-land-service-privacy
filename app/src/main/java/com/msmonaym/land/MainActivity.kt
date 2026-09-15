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
import com.msmonaym.land.ui.calculator.WarishCalculatorScreen
import com.msmonaym.land.ui.consultant.CompanyConsultantScreen
import com.msmonaym.land.ui.dashboard.SummaryDashboardScreen
import com.msmonaym.land.ui.deed.DeedWriterScreen
import com.msmonaym.land.ui.scanner.AiDocScannerScreen
import com.msmonaym.land.ui.contact.ContactScreen
import com.msmonaym.land.ui.home.HomeScreen
import com.msmonaym.land.ui.khatian.KhatianScreen
import com.msmonaym.land.ui.law.LawScreen
import com.msmonaym.land.ui.mutation.MutationGuideScreen
import com.msmonaym.land.ui.tax.LandTaxCalculatorScreen
import com.msmonaym.land.ui.navigation.Screen
import com.msmonaym.land.ui.registration.RegistrationScreen
import com.msmonaym.land.ui.search.SearchScreen
import com.msmonaym.land.ui.security.LockScreen
import com.msmonaym.land.ui.admin.AdminControlScreen
import com.msmonaym.land.ui.admin.AdminEarningsScreen
import com.msmonaym.land.ui.payment.PaymentScreen
import com.msmonaym.land.ui.qrcode.AppQrCodeScreen
import com.msmonaym.land.ui.services.AllServicesHubScreen
import com.msmonaym.land.ui.services.PremiumServicesScreen
import com.msmonaym.land.ui.services.ServicesScreen
import com.msmonaym.land.ui.settings.SettingsScreen
import com.msmonaym.land.ui.splash.SplashScreen
import com.msmonaym.land.ui.survey.SurveyReportScreen
import com.msmonaym.land.ui.theme.MSMonaymTheme

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
            KhatianScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Mutation.route) {
            MutationGuideScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Tax.route) {
            LandTaxCalculatorScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Registration.route) {
            RegistrationScreen(
                onBack = { navController.popBackStack() },
                onNavigateToDeedWriter = { navController.navigate(Screen.DeedWriter.route) }
            )
        }

        composable(Screen.Services.route) {
            ServicesScreen(
                onSelectService = { service ->
                    when (service.id) {
                        "eporcha" -> navController.navigate(Screen.Khatian.route)
                        "emutation" -> navController.navigate(Screen.Mutation.route)
                        "ldtax" -> navController.navigate(Screen.Tax.route)
                        else -> {}
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }


        composable(Screen.Contact.route) {
            ContactScreen(
                onNavigateToPayment = { navController.navigate(Screen.Payment.route) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Calculator.route) {
            LandCalculatorScreen(
                onNavigateToSurvey = { navController.navigate(Screen.SurveyReport.route) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.SurveyReport.route) {
            SurveyReportScreen(
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
                onNavigateToAdmin = { navController.navigate(Screen.AdminGuide.route) },
                onNavigateToQr = { navController.navigate(Screen.AppQrCode.route) },
                onNavigateToPayment = { navController.navigate(Screen.Payment.route) },
                onNavigateToPremiumServices = { navController.navigate(Screen.PremiumServices.route) },
                onNavigateToEarnings = { navController.navigate(Screen.AdminEarnings.route) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.AdminGuide.route) {
            AdminControlScreen(
                onNavigateToEarnings = { navController.navigate(Screen.AdminEarnings.route) },
                onNavigateToPremiumServices = { navController.navigate(Screen.PremiumServices.route) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.AppQrCode.route) {
            AppQrCodeScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Payment.route) {
            PaymentScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.PremiumServices.route) {
            PremiumServicesScreen(
                onNavigateToPayment = { navController.navigate(Screen.Payment.route) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.AdminEarnings.route) {
            AdminEarningsScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.AllServicesHub.route) {
            AllServicesHubScreen(
                onNavigate = { screen -> navController.navigate(screen.route) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.WarishCalculator.route) {
            WarishCalculatorScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.AiDocScanner.route) {
            AiDocScannerScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.SummaryDashboard.route) {
            SummaryDashboardScreen(
                onNavigate = { screen -> navController.navigate(screen.route) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.DeedWriter.route) {
            DeedWriterScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.CompanyConsultant.route) {
            CompanyConsultantScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
