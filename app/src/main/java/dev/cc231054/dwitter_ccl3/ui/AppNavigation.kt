package dev.cc231054.dwitter_ccl3.ui

import android.content.Context
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.cc231054.dwitter_ccl3.MainScreen
import dev.cc231054.dwitter_ccl3.ui.screens.EditPostScreen
import dev.cc231054.dwitter_ccl3.ui.screens.LogInScreen
import dev.cc231054.dwitter_ccl3.ui.screens.LoginHandlerScreen
import dev.cc231054.dwitter_ccl3.ui.screens.SignUpScreen
import dev.cc231054.dwitter_ccl3.viewmodel.UserViewModel

@Composable
fun AppNavigation(
    context: Context,
    userViewModel: UserViewModel = viewModel()
) {
    val navController = rememberNavController()
    val currentUserId = userViewModel.currentUserId.observeAsState()

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
                EditPostScreen(
                    Modifier.padding(innerPadding),
                    navController = navController,
                    currentUserId = currentUserId.value ?: "",
                    upsertPost = { userViewModel.upsertPost(it) }
                )
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
}
