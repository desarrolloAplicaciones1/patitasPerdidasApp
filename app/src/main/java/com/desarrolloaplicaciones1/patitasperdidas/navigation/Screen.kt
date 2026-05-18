package com.desarrolloaplicaciones1.patitasperdidas.navigation

sealed class Screen(val route: String) {
    object Splash      : Screen("splash")
    object Onboarding  : Screen("onboarding")
    object Login       : Screen("login")
    object Register    : Screen("register")
    object Home        : Screen("home")
    object MyPets      : Screen("my_pets")
    object AddPet      : Screen("add_pet")
    object MyAlerts    : Screen("my_alerts")

    object AlertDetail : Screen("alert_detail/{alertId}") {
        fun createRoute(alertId: String) = "alert_detail/$alertId"
    }
    object EditAlert   : Screen("edit_alert/{alertId}") {
        fun createRoute(alertId: String) = "edit_alert/$alertId"
    }
    object EditPet     : Screen("edit_pet/{petId}") {
        fun createRoute(petId: String) = "edit_pet/$petId"
    }
}
