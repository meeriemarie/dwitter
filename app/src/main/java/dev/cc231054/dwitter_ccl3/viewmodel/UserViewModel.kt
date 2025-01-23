package dev.cc231054.dwitter_ccl3.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.cc231054.dwitter_ccl3.data.FollowingEntity
import dev.cc231054.dwitter_ccl3.data.LikedPostEntity
import dev.cc231054.dwitter_ccl3.data.UserEntity
import dev.cc231054.dwitter_ccl3.data.model.UserState
import dev.cc231054.dwitter_ccl3.data.network.supabase
import dev.cc231054.dwitter_ccl3.data.PostEntity
import dev.cc231054.dwitter_ccl3.utils.SharedPreferenceHelper
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import java.util.UUID

// todo: viewmodel needs refactoring, api calls should be in repository

class UserViewModel : ViewModel() {
    private val _userState = mutableStateOf<UserState>(UserState.Loading)
    val userState: State<UserState> = _userState

    private val _currentUser =
        MutableStateFlow(UserEntity(UUID(0, 0), "", "", "", ""))
    val currentUserState: StateFlow<UserEntity> = _currentUser

    private val _currentUserId = MutableLiveData<String?>(null)
    val currentUserId: LiveData<String?> get() = _currentUserId

    private val _userProfile = MutableStateFlow<List<UserEntity>>(emptyList())
    val userProfile: StateFlow<List<UserEntity>> get() = _userProfile

    private val _users = MutableLiveData<List<UserEntity>>(emptyList())
    val users: LiveData<List<UserEntity>> get() = _users

    private val _posts = MutableLiveData<List<PostEntity>>()
    val posts: LiveData<List<PostEntity>> get() = _posts



    fun unlikePost(
        userId: UUID,
        postId: Int
    ) {
        viewModelScope.launch {
            try {
                supabase.from("liked_posts").delete {
                    filter {
                        eq("userid", userId)
                        eq("postid", postId)
                    }
                }
                _userState.value = UserState.Success("Unliked successfully!")
            } catch (e: Exception) {
                _userState.value = UserState.Error("Error: ${e.message}")
            }
        }
    }

    suspend fun getLikedPosts(userId: UUID): List<Int> {
        return try {
            val fetchedPosts = supabase.from("liked_posts")
                .select(columns = Columns.list("postid")) {
                    filter {
                        eq("userid", userId)
                    }
                }.decodeList<Map<String, Int>>()
            fetchedPosts.mapNotNull { it["postid"] }.also {
                Log.i("fetched posts", "UserId: $userId, Fetched posts: $it")
            }
        } catch (e: Exception) {
            _userState.value = UserState.Error("Error: ${e.message}")
            Log.i("Error", e.message.toString())
            emptyList()
        }
    }


//    fun checkIfLiked(
//        postId: Int,
//        userId: UUID,
//        onResult: (Boolean) -> Unit
//    ) {
//        viewModelScope.launch {
//            try {
//                val result = supabase.from("liked_posts")
//                    .select() {
//                        filter {
//                            eq("userid", userId)
//                            eq("postid", postId)
//                        }
//                    }
//                if (result.data != null && result.data.isNotEmpty()) {
//                    onResult(true)
//                    _userState.value = UserState.Success("Liked")
//                } else {
//                    onResult(false)
//                    _userState.value = UserState.Error("Not Liked")
//                }
//            } catch (e: Exception) {
//                _userState.value = UserState.Error("Error: ${e.message}")
//                onResult(false)
//            }
//        }
//    }

    fun likePost(
        postId: Int,
        userId: UUID
    ) {
        viewModelScope.launch {
            try {
                val likedPost = LikedPostEntity(userId, postId)
                supabase.from("liked_posts")
                    .insert(likedPost)
                _userState.value = UserState.Success("Liked successfully!")
            } catch (e: Exception) {
                _userState.value = UserState.Error("Error: ${e.message}")
            }
        }
    }

    fun unfollowUser(
        followedId: UUID,
        userid: UUID
    ) {
        viewModelScope.launch {
            try {
                supabase.from("following").delete {
                    filter {
                        eq("followedid", followedId)
                        eq("userid", userid)
                    }
                }
                _userState.value = UserState.Success("Unfollowed successfully!")
            } catch (e: Exception) {
                _userState.value = UserState.Error("Error: ${e.message}")
            }
        }
    }

    fun followUser(
        followedId: UUID,
        userid: UUID
    ) {
        viewModelScope.launch {
            try {
                val following = FollowingEntity(followedId, userid)
                supabase.from("following")
                    .insert(
                        following
                    )
                _userState.value = UserState.Success("Followed successfully!")
            } catch (e: Exception) {
                _userState.value = UserState.Error("Error: ${e.message}")
            }
        }
    }

    suspend fun getFollowedUsers(userId: UUID): List<UUID> {
        return try {
            val fetchedUsers = supabase.from("following")
                .select(columns = Columns.list("followedid")) {
                    filter {
                        eq("userid", userId)
                    }
                }.decodeList<Map<String, UUID>>()
            fetchedUsers.mapNotNull { it["followedid"] }.also {
                Log.i("fetched users", "UserId: $userId, Fetched users: $it")
            }
        } catch (e: Exception) {
            _userState.value = UserState.Error("Error: ${e.message}")
            Log.i("Error", e.message.toString())
            emptyList()
        }
    }



    private fun reloadProfile() {
        viewModelScope.launch {
            try {
                supabase.from("profiles")
                    .select() {
                        filter {
                            eq("id", currentUserId)
                        }
                    }
                    .decodeList<UserEntity>()
                _userState.value = UserState.Success("Successfully reloaded data")
            } catch (e: Exception) {
                _userState.value = UserState.Error("Error: ${e.message}")

            }
        }
    }

    fun updateProfile(
        userName: String,
        userAvatar: String,
        userUsername: String
    ) {
        viewModelScope.launch {
            try {
                val userId = supabase.auth.currentUserOrNull()?.id
                if (userId != null) {
                    supabase.from("profiles").update({
                        set("name", userName)
                        set("username", userUsername)
                        set("avatar_url", userAvatar)
                    }) {
                        filter {
                            eq("id", userId)
                        }
                    }
                    reloadProfile()
                    _userState.value = UserState.Success("Data was successfully updated")
                }
            } catch (e: Exception) {
                _userState.value = UserState.Error("Error: ${e.message}")
            }
        }

    }

    suspend fun fetchCurrentUserId() {
        val fetchedCurrentUserId =
            supabase.auth.retrieveUserForCurrentSession(updateSession = true).id
        _currentUserId.value = fetchedCurrentUserId
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
                _currentUser.value = UserEntity(UUID(-1, -1), "", "", "", "")
            } catch (e: Exception) {
                _userState.value = UserState.Error("Error: ${e.message}")
            }
        }
    }

    fun isUserLoggedIn(context: Context) {
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

    suspend fun fetchUsers() {
        try {
            val fetchedUsers = supabase.from("profiles")
                .select()
                .decodeList<UserEntity>()
            _users.value = fetchedUsers
            _userState.value = UserState.Success("Users fetched successfully!")
        } catch (e: Exception) {
            _userState.value = UserState.Error("Error: ${e.message}")
        }
    }

    suspend fun fetchUser() {
        val userId = supabase.auth.currentUserOrNull()?.id
        if (userId != null) {
            val profileData = supabase.from("profiles")
                .select() {
                    filter {
                        eq("id", userId)

                    }
                }
                .decodeList<UserEntity>()
            _userProfile.value = profileData
            // If the user exists and is still logged in the request only returns one result
            if (profileData.isEmpty()) throw Exception("User profile could not be found in database")
            _currentUser.value = profileData[0]
        }
    }

    suspend fun fetchPosts() {
        try {
            val fetchedPosts = supabase.from("posts")
                .select()
                .decodeList<PostEntity>()
            _posts.value = fetchedPosts
            _userState.value = UserState.Success("Posts fetched successfully!")
        } catch (e: Exception) {
            _userState.value = UserState.Error("Error: ${e.message}")
        }
    }

    suspend fun tryFetchPostById(postId: Int?): PostEntity {
        val emptyPostEntity =
            PostEntity(id = null, userid = UUID(0, 0), created_at = null, post = "", image = null)

        return if (postId != null) {
            try {
                val fetchedPost = supabase.from("posts")
                    .select() {
                        filter {
                            eq("id", postId)
                        }
                    }
                    .decodeList<PostEntity>()
                    .firstOrNull()
                if (fetchedPost != null) {
                    Log.d("UserViewModel", "fetchedPost: $fetchedPost")
                    _userState.value = UserState.Success("Post fetched successfully!")
                    fetchedPost
                } else {
                    _userState.value = UserState.Error("Post not found!")
                    emptyPostEntity
                }
            } catch (e: Exception) {
                _userState.value = UserState.Error("Error: ${e.message}")
                emptyPostEntity
            }
        } else {
            _userState.value = UserState.Error("Post not found!")
            emptyPostEntity
        }
    }

    fun deletePost(postId: Int) {
        viewModelScope.launch {
            try {
                supabase.from("posts").delete {
                    filter {
                        eq("id", postId)
                    }
                }
                _posts.value = _posts.value?.filter { it.id != postId }
                _userState.value = UserState.Success("Post deleted successfully!")
            } catch (e: Exception) {
                _userState.value = UserState.Error("Error: ${e.message}")
            }
        }
    }

    fun upsertPost(post: PostEntity) {
        viewModelScope.launch {
            try {
                supabase.from("posts").upsert(post)
                val fetchedPosts = supabase.from("posts")
                    .select()
                    .decodeList<PostEntity>()
                Log.d("UserViewModel", "fetchedPosts: $fetchedPosts")
                _userState.value = UserState.Success("Post added successfully!")
            } catch (e: Exception) {
                _userState.value = UserState.Error("Error: ${e.message}")
            }
        }
    }
}