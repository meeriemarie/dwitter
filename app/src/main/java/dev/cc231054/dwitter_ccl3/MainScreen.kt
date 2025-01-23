package dev.cc231054.dwitter_ccl3

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.cc231054.dwitter_ccl3.ui.AddPostButton
import dev.cc231054.dwitter_ccl3.ui.BottomNavigationBar
import dev.cc231054.dwitter_ccl3.ui.ProfileNav
import dev.cc231054.dwitter_ccl3.ui.Screens
import dev.cc231054.dwitter_ccl3.ui.screens.ContentScreen
import dev.cc231054.dwitter_ccl3.ui.screens.EditPostScreen
import dev.cc231054.dwitter_ccl3.ui.screens.SearchScreen
import dev.cc231054.dwitter_ccl3.viewmodel.UserViewModel

@Composable
fun MainScreen(
    mainNavController: NavController,
    modifier: Modifier = Modifier,
    userViewModel: UserViewModel = viewModel(),
) {
    LaunchedEffect(Unit) {
        userViewModel.fetchUser()
        userViewModel.fetchCurrentUserId()
    }

    val searchedPosts by userViewModel.searchedPosts.observeAsState(emptyList())
    val posts by userViewModel.posts.collectAsStateWithLifecycle(emptyList())
    val users by userViewModel.users.observeAsState(emptyList())
    val currentUserState by userViewModel.currentUserState.collectAsStateWithLifecycle()

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

                    // LaunchedEffect is used to ensure livedata is updated
                    // (probably a more intuitive way of doing it, but it works for now)
                    LaunchedEffect(Unit) {
                        userViewModel.fetchPosts()
                        userViewModel.fetchUsers()

                        Log.d("MainScreen", userViewModel.users.value.toString())
                    }

                    ContentScreen(
                        modifier = modifier.padding(innerPadding),
                        currentUserId = currentUserState.id,
                        onNavigate = {
                            bottomNavController.currentBackStackEntry?.savedStateHandle?.set(
                                "id", it
                            )
                            bottomNavController.navigate(Screens.Edit.name)
                        },
                        users = users,
                        posts = posts,
                        deletePost = { userViewModel.deletePost(it) },
                        viewModel = userViewModel
                    )
                }
                composable(Screens.Search.name) {
                    SearchScreen(
                        currentUserId = currentUserState.id,
                        onNavigate = {
                            bottomNavController.currentBackStackEntry?.savedStateHandle?.set(
                                "id", it
                            )
                            bottomNavController.navigate(Screens.Edit.name)
                        },
                        users = users,
                        posts = searchedPosts,
                        deletePost = { userViewModel.deletePost(it) },
                        viewModel = userViewModel
                    )
                }
                composable(Screens.Profile.name) {
                    ProfileNav(mainNavController = mainNavController)
                }
                composable(Screens.Edit.name) {
                    val postId = bottomNavController
                        .previousBackStackEntry
                        ?.savedStateHandle
                        ?.get<Int>("id")


                    EditPostScreen(
                        Modifier.padding(innerPadding),
                        currentUserId = currentUserState.id,
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




