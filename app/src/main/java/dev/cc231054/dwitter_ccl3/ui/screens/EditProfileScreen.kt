package dev.cc231054.dwitter_ccl3.ui.screens

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import dev.cc231054.dwitter_ccl3.data.UserEntity
import dev.cc231054.dwitter_ccl3.data.model.UserState
import dev.cc231054.dwitter_ccl3.viewmodel.UserViewModel

@Composable
fun EditProfileScreen(
    viewModel: UserViewModel = viewModel(),
    onSaveClick: () -> Unit
) {
    LaunchedEffect(Unit) {
        viewModel.fetchUser()
    }
    val state by viewModel.currentUserState.collectAsStateWithLifecycle()
    EditProfile(
        navigateToProfile = onSaveClick,
        userEntity = state)
}


@Composable
fun EditProfile(
    modifier: Modifier = Modifier,
    viewModel: UserViewModel = viewModel(),
    navigateToProfile: () -> Unit,
    userEntity: UserEntity
) {
    val userState by viewModel.userState
    val context = LocalContext.current

    var userAvatarUrl by remember { mutableStateOf(userEntity.avatar_url) }
    var userName by remember { mutableStateOf(userEntity.name) }
    var userUsername by remember { mutableStateOf(userEntity.username) }
    var userEmail by remember { mutableStateOf(userEntity.email) }
    var currentUserState by remember { mutableStateOf("") }

    LaunchedEffect(userEntity) {
        userAvatarUrl = userEntity.avatar_url
        userName = userEntity.name
        userUsername = userEntity.username
        userEmail = userEntity.email

        Log.d("EditProfile", "UserEntity: $userEntity")
    }

    var userAvatarUri by remember {
        mutableStateOf<Uri?>(null)
    }

    val imgPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            userAvatarUri = uri
        }
    )

    Column(modifier = modifier
        .fillMaxSize()
        .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            IconButton(
                onClick = {
                    imgPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                modifier = Modifier
                    .padding(5.dp)
                    .fillMaxWidth()
                    .height(125.dp)
                    .clip(CircleShape)
            ) {
                if (userAvatarUri != null) {
                    Log.d("EditProfile", "Displaying selected image URI: $userAvatarUri")
                    AsyncImage(
                        modifier = Modifier
                            .size(125.dp)
                            .clip(CircleShape),
                        model = userAvatarUri,
                        contentDescription = "User Avatar",
                        contentScale = ContentScale.Crop
                    )
                } else {
                    if (userAvatarUrl != "") {
                        Log.d("EditProfile", "Displaying existing avatar URL: $userAvatarUrl")
                        AsyncImage(
                            modifier = Modifier
                                .size(125.dp)
                                .clip(CircleShape),
                            model = userAvatarUrl,
                            contentDescription = "User Avatar",
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Log.d("EditProfile", "Displaying default icon")
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            tint = MaterialTheme.colorScheme.primary,
                            contentDescription = "User Avatar",
                            modifier = Modifier.size(125.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Choose your profile picture",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(modifier = Modifier.height(36.dp))

            TextField(
                label = { Text("Name") },
                value = userName,
                onValueChange = { userName = it },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                label = { Text("Username") },
                value = userUsername,
                onValueChange = { userUsername = it },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = {
                Log.d("EditProfile", userAvatarUri.toString())

                viewModel.updateProfile(
                    userName,
                    userEmail,
                    userAvatarUri,
                    userUsername
                )
            }) {
                Text(text = "Save Changes")
            }
        }

        when(userState) {
            is UserState.Loading -> {
            }
            is UserState.Success -> {
                val message = (userState as UserState.Success).message
                currentUserState = message
                Toast.makeText(context,message,Toast.LENGTH_SHORT).show()
                navigateToProfile()
            }
            is UserState.Error -> {
                val message = (userState as UserState.Error).message
                currentUserState = message
                Toast.makeText(context,message,Toast.LENGTH_SHORT).show()
            }
        }
    }

}