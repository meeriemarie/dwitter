package dev.cc231054.dwitter_ccl3.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.cc231054.dwitter_ccl3.data.UserEntity
import dev.cc231054.dwitter_ccl3.data.model.UserState
import dev.cc231054.dwitter_ccl3.data.network.supabase
import dev.cc231054.dwitter_ccl3.data.PostEntity
import dev.cc231054.dwitter_ccl3.utils.SharedPreferenceHelper
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

// todo: viewmodel needs refactoring, api calls should be in repository

class UserViewModel: ViewModel() {
    private val _userState = mutableStateOf<UserState>(UserState.Loading)
    val userState: State<UserState> = _userState

    private val _users = MutableLiveData<List<UserEntity>>()
    val users: LiveData<List<UserEntity>> get() = _users

    private val _posts = MutableLiveData<List<PostEntity>>()
    val posts: LiveData<List<PostEntity>> get() = _posts

    private val _currentUserId = MutableLiveData<String?>(null)
    val currentUserId: LiveData<String?> get() = _currentUserId

    init {
        viewModelScope.launch {
            try {
                val fetchedUsers = supabase.from("profiles")
                    .select()
                    .decodeList<UserEntity>()
                _users.value = fetchedUsers

                val fetchedPosts = supabase.from("posts")
                    .select()
                    .decodeList<PostEntity>()
                _posts.value = fetchedPosts

                val fetchedCurrentUserId = supabase.auth.retrieveUserForCurrentSession(updateSession = true).id
                _currentUserId.value = fetchedCurrentUserId

            } catch (e: Exception) {
                Log.e("UserViewModel", "Error: ${e.message}")
            }
        }
    }

    private fun saveToken(context: Context) {
        viewModelScope.launch {
            val accessToken = supabase.auth.currentAccessTokenOrNull() ?: ""
            val sharedPref = SharedPreferenceHelper(context)
            sharedPref.saveStringData("accessToken", accessToken)
        }
    }

    private fun getToken(context: Context): String? {
        val sharedPref = SharedPreferenceHelper(context)
        return sharedPref.getStringData("accessToken")
    }

    fun signUp(
        context: Context,
        userEmail: String,
        userPassword: String,
        userName: String,
        userAvatar: String,
        userUsername: String
    ) {
        viewModelScope.launch {
            try {
                supabase.auth.signUpWith(Email) {
                    email = userEmail
                    password = userPassword
                    data = buildJsonObject {
                        put("name", userName)
                        put("username", userUsername)
                        put("avatar_url", userAvatar)
                    }
                }
                saveToken(context)
                _userState.value = UserState.Success("Registered user successfully!")
            } catch (e: Exception) {
                _userState.value = UserState.Error("Error: ${e.message}")
            }
        }
    }


    fun logIn(
        context: Context,
        userEmail: String,
        userPassword: String
    ) {
        viewModelScope.launch {
            try {
                supabase.auth.signInWith(Email) {
                    email = userEmail
                    password = userPassword
                }
                saveToken(context)
                _userState.value = UserState.Success("Logged in successfully!")
            } catch (e: Exception) {
                _userState.value = UserState.Error("Error: ${e.message}")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                supabase.auth.signOut()
                _userState.value = UserState.Success("Logged out successfully!")
            } catch (e: Exception) {
                _userState.value = UserState.Error("Error: ${e.message}")
            }
        }
    }

    fun isUserLoggedIn(
        context: Context
    ) {
        viewModelScope.launch {
            try {
                val token = getToken(context)
                if (token.isNullOrEmpty()) {
                    _userState.value = UserState.Error("User is not logged in")
                } else {
                    supabase.auth.retrieveUser(token)
                    supabase.auth.refreshCurrentSession()
                    saveToken(context)
                    _userState.value = UserState.Success("User is already logged in!")
                }
            } catch (e: Exception) {
                _userState.value = UserState.Error("Error: ${e.message}")
            }
        }
    }

    fun deletePost(postId: Int) {
        viewModelScope.launch {
            try {
                supabase.from("posts").delete{
                    filter {
                        eq("id", postId)
                    }
                }
                _posts.value = _posts.value?.filter { it.id != postId }
            } catch (e: Exception) {
                Log.e("UserViewModel", "Error: ${e.message}")
            }
        }
    }

    fun upsertPost(post: PostEntity) {
        viewModelScope.launch {
            try {
                supabase.from("posts").upsert(post)
                val updatedPosts = _posts.value?.toMutableList() ?: mutableListOf()
                updatedPosts.add(post)
                _posts.value = updatedPosts
            } catch (e: Exception) {
                Log.e("UserViewModel", "Error: ${e.message}")
            }
        }
    }
}