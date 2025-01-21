package dev.cc231054.dwitter_ccl3.ui

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.cc231054.dwitter_ccl3.MainScreen
import dev.cc231054.dwitter_ccl3.data.model.UserState
import dev.cc231054.dwitter_ccl3.ui.screens.LoginHandlerScreen
import dev.cc231054.dwitter_ccl3.viewmodel.UserViewModel

@Composable
fun AppNavigation(
    context: Context,
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "loginHandler") {
        composable("loginHandler") {
            LoginHandlerScreen(
                context = context,
                navigateToApp = {navController.navigate("mainApp")},
                navigateToLogin = {navController.navigate("loginPage")}
            )
        }
        composable("mainApp") {
            MainScreen()
        }

        composable("loginPage") {
            LogInScreen(
                navigateToSignup = {navController.navigate("signupPage")},
                navigateToApp = {navController.navigate("mainApp")}
            ) }
        composable("signupPage") {
            SignUpScreen(
                navigateToLogin = {navController.navigate("loginPage")},
                navigateToApp = {navController.navigate("mainApp")}
            )
        }
    }
}