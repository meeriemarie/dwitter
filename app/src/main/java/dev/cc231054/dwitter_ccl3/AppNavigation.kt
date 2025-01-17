package dev.cc231054.dwitter_ccl3

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.cc231054.dwitter_ccl3.ui.BottomNavigationBar
import dev.cc231054.dwitter_ccl3.ui.ContentScreen
import dev.cc231054.dwitter_ccl3.ui.ProfilePage
import dev.cc231054.dwitter_ccl3.ui.Screens
import dev.cc231054.dwitter_ccl3.ui.UserViewModel
import dev.cc231054.dwitter_ccl3.ui.logInScreen
import dev.cc231054.dwitter_ccl3.ui.signUpScreen

@Composable
fun MainScreen(modifier: Modifier = Modifier
) {
        Column {
            val bottomNav = rememberNavController()
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                bottomBar = { BottomNavigationBar(bottomNav) })
            {innerPadding ->
                NavHost(bottomNav, Screens.Home.name) {
                    composable(Screens.Home.name) {
                        ContentScreen(Modifier.padding(innerPadding))
                    }
                    composable(Screens.Search.name) {
                    }
                    composable(Screens.Profile.name) {
                        ProfilePage(Modifier.padding(innerPadding))
                    }
                }
            }
        }
}

