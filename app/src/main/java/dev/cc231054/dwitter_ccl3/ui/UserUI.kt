package dev.cc231054.dwitter_ccl3.ui

import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import dev.cc231054.dwitter_ccl3.data.UserEntity
import dev.cc231054.dwitter_ccl3.db.PostEntity


//todo: this is a fucking mess and needs refactoring, each screen should be in its own file
@Composable
fun ContentScreen(
    modifier: Modifier = Modifier,
    navController: NavController
) {
    PostList(modifier = modifier)
    AddPostButton(modifier = modifier, navController = navController)
}

@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
) {
    Text(
        modifier = modifier,
        text = "Search Screen"
    )
}

@Composable
fun EditPostScreen(
    modifier: Modifier = Modifier,
    navController: NavController
) {
    Text(
        modifier = modifier,
        text = "Edit Post Screen"
    )

    BackButton(
        modifier = modifier,
        navController = navController
    )
}

@Composable
fun AddPostButton(
    modifier: Modifier = Modifier,
    navController: NavController
) {
    Button(
        modifier = modifier,
        onClick = {navController.navigate("editPost")}
    ) {
        Text(text = "Add Post")
    }
}

@Composable
fun BackButton(
    modifier: Modifier = Modifier,
    navController: NavController
) {
    Button(
        modifier = modifier,
        onClick = {navController.popBackStack()}
    ) {
        Text(text = "Back")
    }
}

@Composable
fun PostList(
    modifier: Modifier = Modifier,
    viewModel: UserViewModel = viewModel()
) {
    val posts by viewModel.posts.observeAsState(emptyList())
    val users by viewModel.users.observeAsState(emptyList())

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        items(posts, key = { post -> post.id }) { post ->
            val user = users.find { it.id == post.userid }
            if (user != null) {
                PostCard(
                    post = post,
                    user = user,
                    modifier = Modifier.padding(8.dp).fillMaxWidth(0.8f)
                )
            }
        }
    }
}

@Composable
fun PostCard (
    modifier: Modifier = Modifier,
    post: PostEntity,
    user: UserEntity,
) {
    var showFullText by remember {
        mutableStateOf(false)
    }

    Card(
        modifier = modifier.animateContentSize(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF544D79)
        )
    ) {
        Column {
            Column(
                modifier = Modifier.padding(vertical = 20.dp, horizontal = 15.dp)
            ) {

                Row {
                    Image(
                        modifier = Modifier.size(42.dp).clip(CircleShape),
                        painter = rememberAsyncImagePainter(user.avatar_url),
                        contentDescription = "user avatar"
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    val annotatedString = buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                color = Color.White,
                                fontSize = 18.sp
                            )
                        ) {
                            append(user.name)
                        }

                        append("\n")

                        withStyle(
                            style = SpanStyle(
                                color = Color.White.copy(alpha = 0.75f),
                                fontSize = 16.sp
                            )
                        ) {
                            append(user.username)
                        }
                    }

                    Text(text = annotatedString)
                }

                Spacer(modifier = Modifier.height(30.dp))

                Text(
                    modifier = Modifier.clickable {
                        showFullText = !showFullText
                    },
                    text = post.post,
                    color = Color.White.copy(alpha = 0.75f),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = if (showFullText) Int.MAX_VALUE else 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            Image(
                modifier = Modifier.fillMaxWidth().height(200.dp),
                painter = rememberAsyncImagePainter(post.image),
                contentDescription = "Post Image",
                contentScale = ContentScale.Crop
            )
        }
    }
}