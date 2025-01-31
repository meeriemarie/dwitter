package dev.cc231054.dwitter_ccl3.ui.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import dev.cc231054.dwitter_ccl3.data.PostEntity
import dev.cc231054.dwitter_ccl3.data.UserEntity
import dev.cc231054.dwitter_ccl3.ui.LoadingComponent
import dev.cc231054.dwitter_ccl3.ui.components.PostCard
import dev.cc231054.dwitter_ccl3.viewmodel.UserViewModel
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
            LaunchedEffect(Unit) {
                viewModel.fetchUser()
                viewModel.fetchUsers()
                viewModel.fetchPosts()
            }

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

enum class ProfileTab {
    Posts,
    Favorites
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
    logoutClick: () -> Unit,
    editClick: () -> Unit
) {
    var activeTab by remember { mutableStateOf(ProfileTab.Posts) }

    LaunchedEffect(activeTab) {
        viewModel.getLikedPosts(currentUserId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
                .padding(horizontal = 16.dp)
        ) {
            ProfileBanner(
                modifier = Modifier.padding(8.dp),
                viewModel = viewModel,
                editClick = editClick,
                logoutClick = logoutClick
            )
            Spacer(modifier.height(4.dp))
            Row(
                modifier = Modifier.padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                TextButton(onClick = { activeTab = ProfileTab.Posts }) {
                    Text(
                        "Posts",
                        textDecoration = if (activeTab == ProfileTab.Posts) TextDecoration.Underline else TextDecoration.None
                    )
                }
                TextButton(onClick = { activeTab = ProfileTab.Favorites }) {
                    Text(
                        "Favorites",
                        textDecoration = if (activeTab == ProfileTab.Favorites) TextDecoration.Underline else TextDecoration.None
                    )
                }
            }
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            when (activeTab) {
                ProfileTab.Posts -> {
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

                ProfileTab.Favorites -> {
                    LikedPosts(
                        modifier = modifier,
                        currentUserId = currentUserId,
                        onNavigate = { onNavigate(it) },
                        users = users,
                        deletePost = { deletePost(it) },
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}


@Composable
fun ProfileBanner(
    viewModel: UserViewModel = viewModel(),
    modifier: Modifier,
    editClick: () -> Unit,
    logoutClick: () -> Unit
) {
    val profileData by viewModel.userProfile.collectAsState()
    val user = profileData.firstOrNull()

    if (user != null) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                modifier = Modifier
                    .size(126.dp)
                    .clip(CircleShape),
                painter = if (user.avatar_url != "") {
                    rememberAsyncImagePainter(user.avatar_url)
                } else {
                    rememberAsyncImagePainter("https://static.vecteezy.com/system/resources/previews/009/292/244/non_2x/default-avatar-icon-of-social-media-user-vector.jpg")
                },
                contentDescription = "user avatar",
                contentScale = ContentScale.Crop
            )
            Spacer(modifier.width(4.dp))
            Row {
                Column(
                    modifier = Modifier.weight(1f), // Push content to fill remaining space
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = user.name,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 18.sp
                    )
                    Text(
                        text = "@${user.username}",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 16.sp
                    )
                    Spacer(modifier.height(4.dp))
                    LogoutBtn(
                        onLogOut = logoutClick
                    )
                }
                EditBtn(
                    navigateToEdit = editClick,
                    modifier = Modifier.wrapContentWidth()
                )
            }
        }
    }
}


@Composable
fun EditBtn(
    navigateToEdit: () -> Unit,
    modifier: Modifier
) {
    IconButton(
        onClick = navigateToEdit
    ) {
        Icon(
            imageVector = Icons.Default.Edit,
            tint = MaterialTheme.colorScheme.primary,
            contentDescription = "Edit Profile"
        )
    }
}

@Composable
fun LogoutBtn(
    viewModel: UserViewModel = viewModel(),
    onLogOut: () -> Unit
) {
    Column(
        Modifier.fillMaxWidth()
    ) {
        Button(
            onClick = {
                viewModel.logout()
                onLogOut()
            },
            contentPadding = PaddingValues(2.dp),
            modifier = Modifier.size(width = 90.dp, height = ButtonDefaults.MinHeight)
        ) {
            Text(text = "Log Out")
        }
    }
}

@Composable
fun LikedPosts(
    modifier: Modifier = Modifier,
    currentUserId: UUID,
    users: List<UserEntity>,
    onNavigate: (Int?) -> Unit,
    deletePost: (Int) -> Unit,
    viewModel: UserViewModel
) {
    val coroutineScope = rememberCoroutineScope()

    var usersLikedPosts by remember { mutableStateOf<List<PostEntity>?>(null) }
    var likedPosts by remember { mutableStateOf<List<Int>?>(null) }
    var followedUsers by remember { mutableStateOf<List<UUID>?>(null) }
    LaunchedEffect(currentUserId) {
        coroutineScope.launch {
            likedPosts = viewModel.getLikedPosts(currentUserId)
            followedUsers = viewModel.getFollowedUsers(currentUserId)
            usersLikedPosts = viewModel.usersLikedPosts(currentUserId)
        }
    }

    if (usersLikedPosts == null) {
        LoadingComponent()
    } else {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(bottom = 108.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            items(usersLikedPosts!!, key = { post -> post.id ?: 0 }) { post ->
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
                        isFollowed = followedUsers?.contains(user.id) ?: false,
                        likedCount = post.likes
                    )
                }
            }
        }
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
            viewModel.fetchPosts()
        }
    }

    val userPosts = posts.filter { it.userid == currentUserId }
    Log.i("Profile Screen", posts.size.toString())
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(bottom = 108.dp),
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
                    isFollowed = followedUsers?.contains(user.id) ?: false,
                    likedCount = post.likes
                )
            }
        }
    }
}