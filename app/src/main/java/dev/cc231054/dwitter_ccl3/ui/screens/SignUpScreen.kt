package dev.cc231054.dwitter_ccl3.ui

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.cc231054.dwitter_ccl3.data.model.UserState
import dev.cc231054.dwitter_ccl3.viewmodel.UserViewModel

@Composable
fun SignUpScreen(modifier: Modifier = Modifier,
                 viewModel: UserViewModel = viewModel(),
                 navigateToLogin: () -> Unit,
                 navigateToApp: () -> Unit
) {
    val userState by viewModel.userState
    val context = LocalContext.current

    var userEmail by remember { mutableStateOf("") }
    var userPassword by remember { mutableStateOf("") }
    var userAvatar by remember { mutableStateOf("") }
    var userName by remember { mutableStateOf("") }
    var userUsername by remember { mutableStateOf("") }
    var currentUserState by remember { mutableStateOf("") }


    Column(modifier = modifier
        .fillMaxSize()
        .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = userEmail,
            onValueChange = {userEmail = it},
            placeholder = { Text(text = "Enter your email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = userPassword,
            onValueChange = {userPassword= it},
            placeholder = { Text(text = "Enter your password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = userName,
            onValueChange = {userName= it},
            placeholder = { Text(text = "Enter your Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = userUsername,
            onValueChange = {userUsername= it},
            placeholder = { Text(text = "Enter your username") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = userAvatar,
            onValueChange = {userAvatar= it},
            placeholder = { Text(text = "Enter your avatar url") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            viewModel.signUp(
                context,
                userEmail,
                userPassword,
                userName,
                userAvatar,
                userUsername
            )
        }) {
            Text(text = "Sign Up")
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Already have an account?")

        Button(onClick = navigateToLogin) {
            Text(text = "Log In")
        }

        when(userState) {
            is UserState.Loading -> {
            }
            is UserState.Success -> {
                val message = (userState as UserState.Success).message
                currentUserState = message
                Toast.makeText(context,message,Toast.LENGTH_SHORT).show()
                navigateToApp()
            }
            is UserState.Error -> {
                val message = (userState as UserState.Error).message
                currentUserState = message
                Toast.makeText(context,message,Toast.LENGTH_SHORT).show()
            }
        }
    }

}