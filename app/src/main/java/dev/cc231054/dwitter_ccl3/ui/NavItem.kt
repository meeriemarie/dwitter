package dev.cc231054.dwitter_ccl3.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState


enum class Screens {
    Home,
    Search,
    Profile
}

@Composable
fun BottomNavigationBar(navController: NavController){
    val activeRoute = navController.currentBackStackEntryAsState().value?.destination?.route?:Screens.Home.name
    val activeSreen = Screens.valueOf(activeRoute)

    NavigationBar {
        NavigationBarItem(
            selected = activeSreen == Screens.Home,
            onClick = {navController.navigate(Screens.Home.name)},
            icon = { Icon(imageVector = Icons.Filled.Home, contentDescription = "Home") },
            label = { Text("Home") }
        )
        NavigationBarItem(
            selected = activeSreen == Screens.Search,
            onClick = {navController.navigate(Screens.Search.name)},
            icon = { Icon(imageVector = Icons.Filled.Search, contentDescription = "Search") },
            label = { Text("Search") }
        )
        NavigationBarItem(
            selected = activeSreen == Screens.Profile,
            onClick = {navController.navigate(Screens.Profile.name)},
            icon = { Icon(imageVector = Icons.Filled.Person, contentDescription = "Profile") },
            label = { Text("Profile") }
        )
    }
}
