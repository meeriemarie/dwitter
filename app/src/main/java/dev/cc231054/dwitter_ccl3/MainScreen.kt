package dev.cc231054.dwitter_ccl3

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.cc231054.dwitter_ccl3.ui.AddPostButton
import dev.cc231054.dwitter_ccl3.ui.BottomNavigationBar
import dev.cc231054.dwitter_ccl3.ui.screens.ContentScreen
import dev.cc231054.dwitter_ccl3.ui.screens.ProfileScreen
import dev.cc231054.dwitter_ccl3.ui.Screens
import dev.cc231054.dwitter_ccl3.ui.screens.SearchScreen
import dev.cc231054.dwitter_ccl3.viewmodel.UserViewModel

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    userViewModel: UserViewModel = viewModel()
) {

    val currentUserId = userViewModel.currentUserId.observeAsState()

    Column {
        val bottomNav = rememberNavController()
        Scaffold(
            modifier = modifier.fillMaxSize(),
            bottomBar = { BottomNavigationBar(bottomNav) },
            floatingActionButton = {
                AddPostButton(navController = navController)
            }
        ) {innerPadding ->
            NavHost(bottomNav, Screens.Home.name) {
                composable(Screens.Home.name) {
                    ContentScreen(
                        modifier =  modifier.padding(innerPadding),
                        currentUserId = currentUserId.value ?: ""
                    )
                }
                composable(Screens.Search.name) {
                    SearchScreen(modifier.padding(innerPadding))
                }
                composable(Screens.Profile.name) {
                    ProfileScreen(modifier.padding(innerPadding))
                }
            }
        }
    }
}

