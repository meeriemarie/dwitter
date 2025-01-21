package dev.cc231054.dwitter_ccl3.ui.screens

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.cc231054.dwitter_ccl3.data.model.UserState
import dev.cc231054.dwitter_ccl3.ui.LoadingComponent
import dev.cc231054.dwitter_ccl3.viewmodel.UserViewModel

@Composable
fun LoginHandlerScreen(
    context: Context,
    viewModel: UserViewModel = viewModel(),
    navigateToApp: () -> Unit,
    navigateToLogin: () -> Unit
) {
    val userState by viewModel.userState

    LaunchedEffect(Unit) {
        viewModel.isUserLoggedIn(context)
    }

    when(userState) {
        is UserState.Loading -> {
            LoadingComponent()
        }
        is UserState.Success -> {
            navigateToApp()
        }
        is UserState.Error -> {
            navigateToLogin()
        }
    }
}