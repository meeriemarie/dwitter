package dev.cc231054.dwitter_ccl3.ui

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.cc231054.dwitter_ccl3.data.UserEntity
import dev.cc231054.dwitter_ccl3.data.model.UserState
import dev.cc231054.dwitter_ccl3.viewmodel.UserViewModel
import kotlinx.coroutines.flow.collect

@Composable
fun EditProfileScreen(
    viewModel: UserViewModel = viewModel(),
    onSaveClick: () -> Unit
) {
    val state by viewModel.currentUserState.collectAsStateWithLifecycle();
    EditProfile(
        navigateToProfile = onSaveClick,
        userEntity = state)
}


@Composable
fun EditProfile(modifier: Modifier = Modifier,
                 viewModel: UserViewModel = viewModel(),
                 navigateToProfile: () -> Unit,
                userEntity: UserEntity
) {
    val userState by viewModel.userState
    val context = LocalContext.current

    Log.i("Edit Profile", userEntity.username)

    var userAvatar by remember { mutableStateOf(userEntity.avatar_url) }
    var userName by remember { mutableStateOf(userEntity.name) }
    var userUsername by remember { mutableStateOf(userEntity.username) }
    var currentUserState by remember { mutableStateOf("") }

    LaunchedEffect(userEntity) {
        userAvatar = userEntity.avatar_url
        userName = userEntity.name
        userUsername = userEntity.username
    }


    Column(modifier = modifier
        .fillMaxSize()
        .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = userName,
            onValueChange = {userName= it},
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = userUsername,
            onValueChange = {userUsername= it},
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = userAvatar,
            onValueChange = {userAvatar= it},
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            viewModel.updateProfile(
                userName,
                userAvatar,
                userUsername
            )
        }) {
            Text(text = "Save Changes")
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