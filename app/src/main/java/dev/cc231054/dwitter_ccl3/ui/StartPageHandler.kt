package dev.cc231054.dwitter_ccl3.ui

import android.content.Context
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.cc231054.dwitter_ccl3.MainScreen
import dev.cc231054.dwitter_ccl3.data.model.UserState

//todo: rename file to fit the class name
@Composable
fun AppNavigation(context: Context) {
    val navController = rememberNavController()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
    ) {innerPadding ->
        NavHost(navController = navController, startDestination = "loginHandler") {
            composable("loginHandler") {
                LoginHandlerScreen(
                    context = context,
                    navigateToApp = {navController.navigate("mainApp")},
                    navigateToLogin = {navController.navigate("loginPage")}
                )
            }
            composable("mainApp") {
                MainScreen(navController = navController)
            }

            composable("editPost") {
                EditPostScreen(Modifier.padding(innerPadding),navController = navController)
            }

            composable("loginPage") {
                logInScreen(
                    navigateToSignup = {navController.navigate("signupPage")},
                    navigateToApp = {navController.navigate("mainApp")}
                ) }
            composable("signupPage") {
                signUpScreen(
                    navigateToLogin = {navController.navigate("loginPage")},
                    navigateToApp = {navController.navigate("mainApp")}
                )
            }
        }
    }
}

//todo: screen should be in its own file
@Composable
fun LoginHandlerScreen(
    context: Context,
    viewModel: UserViewModel = viewModel(),
    navigateToApp: () -> Unit,
    navigateToLogin: () -> Unit
) {
    val userState by viewModel.userState

    LaunchedEffect(Unit) {
        viewModel.isUserLoggedIn(context)
    }

    when(userState) {
        is UserState.Loading -> {
            LoadingComponent()
        }
        is UserState.Success -> {
            navigateToApp()
        }
        is UserState.Error -> {
            navigateToLogin()
        }
    }
}