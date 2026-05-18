package com.desarrolloaplicaciones1.patitasperdidas.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.desarrolloaplicaciones1.patitasperdidas.presentation.auth.LoginScreen
import com.desarrolloaplicaciones1.patitasperdidas.presentation.auth.RegisterScreen
import com.desarrolloaplicaciones1.patitasperdidas.presentation.home.HomeScreen
import com.desarrolloaplicaciones1.patitasperdidas.presentation.onboarding.OnboardingScreen
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
                    navController.navigate(Screen.Onboarding.route)
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) { popUpTo(0) }
                }
            )
        }

        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onNavigateToLogin = { navController.navigate(Screen.Login.route) }
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
                }
            )
        }

        composable(
            route = Screen.AlertDetail.route,
            arguments = listOf(navArgument("alertId") { type = NavType.StringType })
        ) { backStackEntry ->
            val alertId = backStackEntry.arguments?.getString("alertId") ?: return@composable
            // TODO: AlertDetailScreen(alertId = alertId, onBack = { navController.popBackStack() })
        }

        composable(
            route = Screen.EditAlert.route,
            arguments = listOf(navArgument("alertId") { type = NavType.StringType })
        ) { backStackEntry ->
            val alertId = backStackEntry.arguments?.getString("alertId") ?: return@composable
            // TODO: EditAlertScreen(alertId = alertId, onBack = { navController.popBackStack() })
        }

        composable(Screen.MyPets.route) {
            // TODO: MyPetsScreen(onAddPet = { navController.navigate(Screen.AddPet.route) })
        }

        composable(Screen.AddPet.route) {
            // TODO: AddPetScreen(onBack = { navController.popBackStack() })
        }

        composable(
            route = Screen.EditPet.route,
            arguments = listOf(navArgument("petId") { type = NavType.StringType })
        ) { backStackEntry ->
            val petId = backStackEntry.arguments?.getString("petId") ?: return@composable
            // TODO: EditPetScreen(petId = petId, onBack = { navController.popBackStack() })
        }
    }
}
