package dev.cc231054.dwitter_ccl3.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import dev.cc231054.dwitter_ccl3.data.PostEntity
import dev.cc231054.dwitter_ccl3.ui.components.BackButton
import dev.cc231054.dwitter_ccl3.viewmodel.UserViewModel
import java.util.UUID

@Composable
fun EditPostScreen(
    modifier: Modifier = Modifier,
    userViewModel: UserViewModel,
    currentUserId: UUID,
    postId: Int?,
    upsertPost: (PostEntity, Uri?) -> Unit,
    onBackButton: () -> Unit,
) {

    var post by remember { mutableStateOf<PostEntity?>(null) }
    var postText by remember { mutableStateOf("") }

    LaunchedEffect(postId) {
        post = userViewModel.tryFetchPostById(postId)
        postText = post?.post ?: ""
    }

    val toUpsertPost = PostEntity(
        id = post?.id,
        userid = currentUserId,
        created_at = post?.created_at,
        post = postText,
        image = post?.image
    )

    // Image upload logic
    var postImgUri by remember {
        mutableStateOf<Uri?>(null)
    }

    val imgPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            postImgUri = uri
        }
    )

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BackButton(onBackButton = onBackButton)

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = postText,
            onValueChange = {
                postText = it
            },
            label = { Text("Edit Post") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        IconButton(
            modifier = Modifier.fillMaxWidth()
                .height(250.dp),
            onClick = {
                imgPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            },
        ) {
            if (postImgUri != null) {
                AsyncImage(
                    model = postImgUri,
                    contentDescription = "Post Image",
                    modifier = Modifier.height(200.dp)
                        .fillMaxWidth(),
                )
            } else {
                if (post?.image != null) {
                    AsyncImage(
                        model = post?.image,
                        contentDescription = "Post Image",
                        modifier = Modifier.height(200.dp)
                            .fillMaxWidth(),
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Image,
                        tint = MaterialTheme.colorScheme.primary,
                        contentDescription = "Post Image",
                        modifier = Modifier.size(200.dp),
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Click to change image",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(36.dp))

        Button(
            onClick = {
                upsertPost(toUpsertPost, postImgUri)
                onBackButton()
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text("Save", color = MaterialTheme.colorScheme.onPrimary)
        }
    }
}