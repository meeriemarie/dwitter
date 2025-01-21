package dev.cc231054.dwitter_ccl3

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.cc231054.dwitter_ccl3.ui.AddPostButton
import dev.cc231054.dwitter_ccl3.ui.BottomNavigationBar
import dev.cc231054.dwitter_ccl3.ui.ProfileNav
import dev.cc231054.dwitter_ccl3.ui.Screens
import dev.cc231054.dwitter_ccl3.ui.screens.ContentScreen
import dev.cc231054.dwitter_ccl3.ui.screens.EditPostScreen
import dev.cc231054.dwitter_ccl3.ui.screens.ProfileScreen
import dev.cc231054.dwitter_ccl3.ui.screens.SearchScreen
import dev.cc231054.dwitter_ccl3.viewmodel.UserViewModel

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    userViewModel: UserViewModel = viewModel(),
) {
    val posts by userViewModel.posts.observeAsState(emptyList())
    val users by userViewModel.users.observeAsState(emptyList())
    val currentUserId by userViewModel.currentUserId.observeAsState()

    Column {
        val bottomNavController = rememberNavController()

        Scaffold(
            modifier = modifier.fillMaxSize(),
            bottomBar = { BottomNavigationBar(bottomNavController) },
            floatingActionButton = {
                AddPostButton(onNavigate = {
                    bottomNavController.currentBackStackEntry?.savedStateHandle?.set(
                        key = "id",
                        value = null
                    )

                    bottomNavController.navigate(Screens.Edit.name)
                })
            }
        ) { innerPadding ->
            NavHost(bottomNavController, startDestination = Screens.Home.name) {
                composable(Screens.Home.name) {
                    LaunchedEffect(Unit) {
                        userViewModel.fetchPosts()
                        userViewModel.fetchUsers()
                    }

                    ContentScreen(
                        modifier = modifier.padding(innerPadding),
                        currentUserId = currentUserId ?: "",
                        onNavigate = {
                            bottomNavController.currentBackStackEntry?.savedStateHandle?.set(
                                "id", it
                            )
                            bottomNavController.navigate(Screens.Edit.name) },
                        users = users,
                        posts = posts,
                        deletePost = { userViewModel.deletePost(it) },
                        viewModel = userViewModel
                    )
                }
                composable(Screens.Search.name) {
                    SearchScreen(modifier.padding(innerPadding))
                }
                composable(Screens.Profile.name) {
                    ProfileNav()
                }
                composable(Screens.Edit.name){
                    val postId = bottomNavController
                        .previousBackStackEntry
                        ?.savedStateHandle
                        ?.get<Int>("id")


                    EditPostScreen(
                        Modifier.padding(innerPadding),
                        currentUserId = currentUserId ?: "",
                        userViewModel = userViewModel,
                        postId = postId,
                        upsertPost = { userViewModel.upsertPost(it) },
                        onBackButton = { bottomNavController.popBackStack() }
                    )
                }
            }
        }
    }
}




