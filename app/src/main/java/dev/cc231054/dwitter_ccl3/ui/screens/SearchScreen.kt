package dev.cc231054.dwitter_ccl3.ui.screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.cc231054.dwitter_ccl3.data.PostEntity
import dev.cc231054.dwitter_ccl3.data.UserEntity
import dev.cc231054.dwitter_ccl3.ui.PostCard
import dev.cc231054.dwitter_ccl3.ui.PostList
import dev.cc231054.dwitter_ccl3.viewmodel.UserViewModel
import io.github.jan.supabase.realtime.Column
import io.github.jan.supabase.realtime.PostgresAction
import java.util.UUID

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SearchScreen(
    currentUserId: UUID,
    onNavigate: (Int?) -> Unit,
    users: List<UserEntity>,
    posts: List<PostEntity>,
    deletePost: (postId: Int) -> Unit,
    viewModel: UserViewModel,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = { AppBar(viewModel) },
        content = {
            Column(
                modifier = modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(modifier = Modifier.height(32.dp))
                MainSearch(
                    currentUserId = currentUserId,
                    onNavigate = { onNavigate(it) },
                    posts = posts,
                    users = users,
                    deletePost = { deletePost(it) },
                    viewModel = viewModel,
                    modifier = modifier.padding(vertical = 32.dp)
                )
            }

        }
    )
}


@Composable
fun MainSearch(
    currentUserId: UUID,
    posts: List<PostEntity>,
    users: List<UserEntity>,
    viewModel: UserViewModel,
    onNavigate: (Int?) -> Unit,
    deletePost: (Int) -> Unit,
    modifier: Modifier
) {
    val isSearching by viewModel.isSearching.collectAsState()

    if (isSearching) {
        SearchScreenComponent(
            currentUserId = currentUserId,
            posts = posts,
            users = users,
            viewModel = viewModel,
            onNavigate = onNavigate,
            deletePost = deletePost,
            modifier = modifier
        )
    }
}


@Composable
fun SearchScreenComponent(
    modifier: Modifier = Modifier,
    viewModel: UserViewModel = viewModel(),
    currentUserId: UUID,
    posts: List<PostEntity>,
    users: List<UserEntity>,
    onNavigate: (Int?) -> Unit,
    deletePost: (Int) -> Unit
) {
    val searchText by viewModel.searchText.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()

    var likedPosts by remember { mutableStateOf<List<Int>?>(null) }
    LaunchedEffect(currentUserId) {
        likedPosts = viewModel.getLikedPosts(currentUserId)
    }

    Column(modifier = modifier.fillMaxSize()) {

        TextField(
            value = searchText,
            onValueChange = { viewModel.onSearchTextChange(it) },
            placeholder = { Text("Search for posts") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(searchResults, key = { post -> post.id ?: 0 }) { post ->
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
                                likedPosts = likedPosts?.plus(post.id!!)
                            }
                        },
                        isLiked = likedPosts?.contains(post.id) ?: false
                    )
                }
            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(viewModel: UserViewModel) {
    TopAppBar(
        title = { Text("Posts") },
        actions = {
            IconButton(onClick = { viewModel.onToggleSearch() }) {
                Icon(Icons.Default.Search, contentDescription = "Search")
            }
        }
    )
}