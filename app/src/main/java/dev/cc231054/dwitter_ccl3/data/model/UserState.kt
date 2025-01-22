package dev.cc231054.dwitter_ccl3.data.model

import dev.cc231054.dwitter_ccl3.data.UserEntity

sealed class UserState {
    data object Loading: UserState()
    data class Success(val message: String): UserState()
    data class Error(val message: String): UserState()
}