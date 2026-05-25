package com.desarrolloaplicaciones1.patitasperdidas.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.desarrolloaplicaciones1.patitasperdidas.presentation.auth.LoginScreen
import com.desarrolloaplicaciones1.patitasperdidas.presentation.auth.RegisterScreen
import com.desarrolloaplicaciones1.patitasperdidas.presentation.create.CreateAlertScreen
import com.desarrolloaplicaciones1.patitasperdidas.presentation.create.ExpressAlertScreen
import com.desarrolloaplicaciones1.patitasperdidas.presentation.detail.AlertDetailScreen
import com.desarrolloaplicaciones1.patitasperdidas.presentation.home.HomeScreen
import com.desarrolloaplicaciones1.patitasperdidas.presentation.map.MapScreen
import com.desarrolloaplicaciones1.patitasperdidas.presentation.onboarding.OnboardingScreen
import com.desarrolloaplicaciones1.patitasperdidas.presentation.profile.EditProfileScreen
import com.desarrolloaplicaciones1.patitasperdidas.presentation.profile.ProfileScreen
import com.desarrolloaplicaciones1.patitasperdidas.presentation.splash.SplashScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Splash.route
) {
    NavHost(navController = navController, startDestination = startDestination) {

        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToOnboarding = {
                    navController.navigate(Screen.Onboarding.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) { popUpTo(0) }
                }
            )
        }

        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) { popUpTo(0) }
                },
                onNavigateToRegister = { navController.navigate(Screen.Register.route) }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Screen.Home.route) { popUpTo(0) }
                },
                onNavigateToLogin = { navController.popBackStack() }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToDetail = { alertId ->
                    navController.navigate(Screen.AlertDetail.createRoute(alertId))
                },
                onLogout = {
                    navController.navigate(Screen.Login.route) { popUpTo(0) }
                },
                onNavigateToCreateAlert = { navController.navigate(Screen.CreateAlert.route) },
                onNavigateToExpressAlert = { navController.navigate(Screen.ExpressAlert.route) },
                onNavigateToMap = { navController.navigate(Screen.Map.route) },
                onNavigateToProfile = { navController.navigate(Screen.Profile.route) }
            )
        }

        composable(Screen.CreateAlert.route) {
            CreateAlertScreen(
                onBack = { navController.popBackStack() },
                onAlertCreated = { navController.popBackStack() }
            )
        }

        composable(Screen.ExpressAlert.route) {
            ExpressAlertScreen(
                onBack = { navController.popBackStack() },
                onPublished = { navController.popBackStack() }
            )
        }

        composable(Screen.Map.route) {
            MapScreen(
                onBack = { navController.popBackStack() },
                onNavigateToDetail = { alertId ->
                    navController.navigate(Screen.AlertDetail.createRoute(alertId))
                }
            )
        }

        composable(
            route = Screen.AlertDetail.route,
            arguments = listOf(navArgument("alertId") { type = NavType.StringType })
        ) { backStackEntry ->
            val alertId = backStackEntry.arguments?.getString("alertId") ?: return@composable
            AlertDetailScreen(
                alertId = alertId,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                onBack = { navController.popBackStack() },
                onLogout = {
                    navController.navigate(Screen.Login.route) { popUpTo(0) }
                },
                onNavigateToAlertDetail = { alertId ->
                    navController.navigate(Screen.AlertDetail.createRoute(alertId))
                },
                onNavigateToEditProfile = {
                    navController.navigate(Screen.EditProfile.route)
                }
            )
        }

        composable(Screen.EditProfile.route) {
            EditProfileScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.MyPets.route) { }
        composable(Screen.MyAlerts.route) { }

        composable(
            route = Screen.EditAlert.route,
            arguments = listOf(navArgument("alertId") { type = NavType.StringType })
        ) { }
    }
}
