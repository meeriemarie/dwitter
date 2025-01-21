package dev.cc231054.dwitter_ccl3.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState


enum class Screens {
    Home,
    Search,
    Profile,
    Edit,
}

@Composable
fun BottomNavigationBar(navController: NavController){
    val activeRoute = navController.currentBackStackEntryAsState().value?.destination?.route?:Screens.Home.name
    val activeSreen = Screens.valueOf(activeRoute)

    NavigationBar{
        NavigationBarItem(
            selected = activeSreen == Screens.Home,
            onClick = {navController.navigate(Screens.Home.name)},
            icon = {
                Icon(
                    imageVector = if (activeSreen != Screens.Home) Icons.Outlined.Home else Icons.Filled.Home,
                    contentDescription = "Home",
                )
            },
        )
        NavigationBarItem(
            selected = activeSreen == Screens.Search,
            onClick = {navController.navigate(Screens.Search.name)},
            icon = {
                Icon(
                    imageVector = if (activeSreen != Screens.Search) Icons.Outlined.Search else Icons.Filled.Search,
                    contentDescription = "Search",
                )
            },
        )
        NavigationBarItem(
            selected = activeSreen == Screens.Profile,
            onClick = {navController.navigate(Screens.Profile.name)},
            icon = {
                Icon(
                    imageVector = if (activeSreen != Screens.Profile) Icons.Outlined.Person else Icons.Filled.Person,
                    contentDescription = "Profile",
                )
            },
        )
    }
}
