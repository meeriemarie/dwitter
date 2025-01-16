package dev.cc231054.dwitter_ccl3.ui

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import coil.compose.rememberAsyncImagePainter
import dev.cc231054.dwitter_ccl3.db.PostEntity

@Composable
fun ContentScreen(modifier: Modifier = Modifier) {
    PostList(modifier = modifier)
}

//needs auth session
//@Composable
//fun UserList(
//    modifier: Modifier = Modifier,
//    viewModel: UserViewModel = viewModel()
//) {
//    val users by viewModel.users.observeAsState(emptyList())
//    LazyColumn(
//        modifier = modifier
//    ) {
//        items(users, key = { user -> user.id }) { user ->
//            Text(user.username, modifier = Modifier.padding(8.dp))
//        }
//    }
//}

@Composable
fun PostList(
    modifier: Modifier = Modifier,
    viewModel: UserViewModel = viewModel()
) {
    val posts by viewModel.posts.observeAsState(emptyList())
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        items(posts, key = { post -> post.id }) { post ->
            PostCard(post = post, modifier = Modifier.padding(8.dp).fillMaxWidth(0.8f))
        }
    }
}

@Composable
fun PostCard (
    modifier: Modifier = Modifier,
    post: PostEntity
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

                // placeholder user data, need auth session
                Row {
                    Image(
                        modifier = Modifier.size(42.dp).clip(CircleShape),
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile"
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    val annotatedString = buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                color = Color.White,
                                fontSize = 18.sp
                            )
                        ) {
                            append("Username")
                        }

                        append("\n")

                        withStyle(
                            style = SpanStyle(
                                color = Color.White.copy(alpha = 0.75f),
                                fontSize = 16.sp
                            )
                        ) {
                            append("@username")
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