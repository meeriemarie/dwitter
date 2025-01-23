package dev.cc231054.dwitter_ccl3.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.cc231054.dwitter_ccl3.data.PostEntity
import dev.cc231054.dwitter_ccl3.data.UserEntity
import dev.cc231054.dwitter_ccl3.ui.EditProfileScreen
import dev.cc231054.dwitter_ccl3.ui.PostCard
import dev.cc231054.dwitter_ccl3.viewmodel.UserViewModel
import io.github.jan.supabase.realtime.Column
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
fun ProfileNav(
    mainNavController: NavController,
    modifier: Modifier = Modifier,
    currentUserId: UUID,
    onNavigate: (Int?) -> Unit,
    users: List<UserEntity>,
    posts: List<PostEntity>,
    deletePost: (postId: Int) -> Unit,
    viewModel: UserViewModel
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "profile") {
        composable("profile") {
            ProfileScreen(
                editClick = { navController.navigate("editPage") },
                logoutClick = { mainNavController.navigate("loginHandler") },
                currentUserId = currentUserId,
                deletePost = deletePost,
                viewModel = viewModel,
                onNavigate = onNavigate,
                posts = posts,
                users = users
            )
        }
        composable("editPage") {
            EditProfileScreen(
                onSaveClick = { navController.navigate("profile") }
            )
        }
    }
}


@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    currentUserId: UUID,
    onNavigate: (Int?) -> Unit,
    users: List<UserEntity>,
    posts: List<PostEntity>,
    deletePost: (postId: Int) -> Unit,
    viewModel: UserViewModel,
    editClick: () -> Unit,
    logoutClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        ProfileBanner(
            viewModel = viewModel,
            modifier = modifier
        )
        Spacer(modifier.height(8.dp))
        EditBtn(
            navigateToEdit = editClick
        )
        Spacer(modifier.height(8.dp))
        LogoutBtn(
            onLogOut = logoutClick
        )
        Spacer(modifier.height(12.dp))
        UserPosts(
            modifier = modifier,
            currentUserId = currentUserId,
            onNavigate = { onNavigate(it) },
            posts = posts,
            users = users,
            deletePost = { deletePost(it) },
            viewModel = viewModel
        )
    }

}


@Composable
fun ProfileBanner(
    viewModel: UserViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val profileData by viewModel.userProfile.collectAsState()

    LazyColumn {
        items(profileData) { user: UserEntity ->
            Text(text = user.username)
            Text(text = "@${user.name}")
        }
    }
}


@Composable
fun EditBtn(
    navigateToEdit: () -> Unit
) {
    Button(
        onClick = navigateToEdit
    ) {
        Text(text = "Edit Profile")
        Icon(imageVector = Icons.Filled.Edit, contentDescription = "Edit")
    }
}

@Composable
fun LogoutBtn(
    viewModel: UserViewModel = viewModel(),
    onLogOut: () -> Unit
) {
    Button(onClick = {
        viewModel.logout()
        onLogOut()
    }) {
        Text(text = "Log Out")
    }
}

@Composable
fun UserPosts(
    modifier: Modifier = Modifier,
    currentUserId: UUID,
    posts: List<PostEntity>,
    users: List<UserEntity>,
    onNavigate: (Int?) -> Unit,
    deletePost: (Int) -> Unit,
    viewModel: UserViewModel
) {

    val coroutineScope = rememberCoroutineScope()

    var likedPosts by remember { mutableStateOf<List<Int>?>(null) }
    var followedUsers by remember { mutableStateOf<List<UUID>?>(null) }
    LaunchedEffect(currentUserId) {
        coroutineScope.launch {
            likedPosts = viewModel.getLikedPosts(currentUserId)
            followedUsers = viewModel.getFollowedUsers(currentUserId)
        }
    }

    val userPosts = posts.filter { it.userid == currentUserId }
    Log.i("Profile Screen", posts.size.toString())
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        items(userPosts, key = { post -> post.id ?: 0 }) { post ->
            val user = users.find { it.id.toString() == post.userid.toString() }
            if (user != null) {
                PostCard(
                    post = post,
                    user = user,
                    currentUserId = currentUserId,
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(0.8f),
                    deletePost = {
                        deletePost(post.id!!)
                    },
                    onNavigate = { onNavigate(it) },
                    onLikeClick = {
                        if (likedPosts?.contains(post.id) == true) {
                            viewModel.unlikePost(currentUserId, post.id!!)
                            likedPosts = likedPosts?.filter { it != post.id }
                        } else {
                            viewModel.likePost(post.id!!, currentUserId)
                            likedPosts = likedPosts?.plus(post.id)
                        }
                    },
                    onFollowClick = {
                        if (followedUsers?.contains(user.id) == true) {
                            viewModel.unfollowUser(user.id, currentUserId)
                            followedUsers = followedUsers?.filter { it != user.id }
                        } else {
                            viewModel.followUser(user.id, currentUserId)
                            followedUsers = followedUsers?.plus(user.id)
                        }

                    },
                    isLiked = likedPosts?.contains(post.id) ?: false,
                    isFollowed = followedUsers?.contains(user.id) ?: false
                )
            }
        }
    }
}