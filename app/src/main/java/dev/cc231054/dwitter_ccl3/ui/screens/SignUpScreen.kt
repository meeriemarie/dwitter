package dev.cc231054.dwitter_ccl3.ui

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import dev.cc231054.dwitter_ccl3.data.model.UserState
import dev.cc231054.dwitter_ccl3.viewmodel.UserViewModel
import kotlinx.coroutines.launch

@Composable
fun SignUpScreen(
    modifier: Modifier = Modifier,
    viewModel: UserViewModel = viewModel(),
    navigateToLogin: () -> Unit,
    navigateToApp: () -> Unit
) {
    val userState by viewModel.userState
    val context = LocalContext.current

    var userEmail by remember { mutableStateOf("") }
    var userPassword by remember { mutableStateOf("") }
    var userName by remember { mutableStateOf("") }
    var userUsername by remember { mutableStateOf("") }
    var currentUserState by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()


    var userAvatarUri by remember {
        mutableStateOf<Uri?>(null)
    }

    val imgPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            userAvatarUri = uri
        }
    )


    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(top = 100.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
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
            ){
                if (userAvatarUri != null) {
                    AsyncImage(
                        modifier = Modifier
                            .size(125.dp)
                            .clip(CircleShape),
                        model = userAvatarUri,
                        contentDescription = "User Avatar",
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        modifier = Modifier
                            .size(125.dp),
                        imageVector = Icons.Default.AccountCircle,
                        tint = MaterialTheme.colorScheme.primary,
                        contentDescription = "User Avatar",
                    )
                }
            }

            Text(
                text = "Choose your profile picture",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Spacer(modifier = Modifier.height(36.dp))

        TextField(
            value = userEmail,
            onValueChange = { userEmail = it },
            placeholder = { Text(text = "Enter your email") },
            modifier = Modifier
                .fillMaxWidth()
                .focusable(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = {
                focusManager.moveFocus(FocusDirection.Next)
            })
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = userPassword,
            onValueChange = { userPassword = it },
            placeholder = { Text(text = "Enter your password") },
            modifier = Modifier
                .fillMaxWidth()
                .focusable(),
            trailingIcon = {
                if (passwordVisible) {
                    IconButton(onClick = { passwordVisible = false }) {
                        Icon(
                            imageVector = Icons.Filled.Visibility,
                            contentDescription = "Hide Password"
                        )
                    }
                } else {
                    IconButton(onClick = { passwordVisible = true }) {
                        Icon(
                            imageVector = Icons.Filled.VisibilityOff,
                            contentDescription = "Show Password"
                        )
                    }
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(onNext = {
                coroutineScope.launch {
                    focusManager.moveFocus(FocusDirection.Next)
                    scrollState.scrollTo(scrollState.maxValue)
                }
            })

        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = userName,
            onValueChange = { userName = it },
            placeholder = { Text(text = "Enter your Name") },
            modifier = Modifier
                .fillMaxWidth()
                .focusable(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = {
                coroutineScope.launch {
                    focusManager.moveFocus(FocusDirection.Next)
                    scrollState.scrollTo(scrollState.maxValue)
                }
            })
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = userUsername,
            onValueChange = { userUsername = it },
            placeholder = { Text(text = "Enter your username") },
            modifier = Modifier
                .fillMaxWidth()
                .focusable(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = {
                coroutineScope.launch {
                    focusManager.moveFocus(FocusDirection.Next)
                    scrollState.scrollTo(scrollState.maxValue)
                }
            })
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            viewModel.signUp(
                context,
                userEmail,
                userPassword,
                userName,
                userAvatarUri,
                userUsername
            )
        }) {
            Text(text = "Sign Up")
        }

        Spacer(modifier = Modifier.weight(1f))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(text = "Already have an account?")
            TextButton(onClick = navigateToLogin) {
                Text(
                    text = "Log In",
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        when (userState) {
            is UserState.Loading -> {
            }

            is UserState.Success -> {
                val message = (userState as UserState.Success).message
                currentUserState = message
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                navigateToApp()
            }

            is UserState.Error -> {
                val message = (userState as UserState.Error).message
                currentUserState = message
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
        }
    }

}