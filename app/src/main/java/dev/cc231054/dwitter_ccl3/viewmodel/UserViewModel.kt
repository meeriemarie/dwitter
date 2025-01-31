package dev.cc231054.dwitter_ccl3.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.cc231054.dwitter_ccl3.data.FollowingEntity
import dev.cc231054.dwitter_ccl3.data.LikedPostEntity
import dev.cc231054.dwitter_ccl3.data.PostEntity
import dev.cc231054.dwitter_ccl3.data.UserEntity
import dev.cc231054.dwitter_ccl3.data.model.UserState
import dev.cc231054.dwitter_ccl3.data.network.supabase
import dev.cc231054.dwitter_ccl3.utils.SharedPreferenceHelper
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order.*
import io.github.jan.supabase.postgrest.rpc
import io.github.jan.supabase.storage.storage
import io.github.jan.supabase.storage.upload
import io.ktor.http.ContentType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import java.util.UUID

class UserViewModel : ViewModel() {
    // Session states
    private val _userState = mutableStateOf<UserState>(UserState.Loading)
    val userState: State<UserState> = _userState

    private val _currentUser =
        MutableStateFlow(UserEntity(UUID(0, 0), "", "", "", ""))
    val currentUserState: StateFlow<UserEntity> = _currentUser

    private val _currentUserId = MutableLiveData<String?>(null)
    val currentUserId: LiveData<String?> get() = _currentUserId

    private val _userProfile = MutableStateFlow<List<UserEntity>>(emptyList())
    val userProfile: StateFlow<List<UserEntity>> get() = _userProfile

    // User & Post states
    private val _users = MutableLiveData<List<UserEntity>>(emptyList())
    val users: LiveData<List<UserEntity>> get() = _users

    private val _posts = MutableStateFlow<List<PostEntity>>(emptyList())
    val posts: StateFlow<List<PostEntity>> get() = _posts

    // Search states
    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()

    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _searchedPosts = MutableLiveData<List<PostEntity>>(emptyList())
    val searchedPosts: LiveData<List<PostEntity>> get() = _searchedPosts

    private val _searchResults = MutableStateFlow(_posts.value)
    val searchResults = searchText
        .combine(_posts) { text, posts ->
            if (text.isBlank()) {
            }

            posts.filter { post ->
                val doesPostTextMatches = post.post.contains(text, ignoreCase = true)
                var doesMatchUUID = false
                if (!doesPostTextMatches) {
                    val matchingUUIDs =
                        users.value?.filter { u ->
                            u.username.contains(text, ignoreCase = true)
                        }
                            ?.map {
                                it.id
                            }
                    doesMatchUUID = matchingUUIDs!!.contains(post.userid)
                }
                doesPostTextMatches || doesMatchUUID
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = _searchResults.value
        )

    // Auth functions
    fun signUp(
        context: Context,
        userEmail: String,
        userPassword: String,
        userName: String,
        userAvatarUri: Uri?,
        userUsername: String
    ) {
        viewModelScope.launch {
            try {
                val path = upsertAvatarImg(userEmail, userAvatarUri)

                supabase.auth.signUpWith(Email) {
                    email = userEmail
                    password = userPassword
                    data = buildJsonObject {
                        put("name", userName)
                        put("username", userUsername)
                        let {
                            if (path != null) {
                                put(
                                    "avatar_url",
                                    "https://wysgyswdoefgyxubgcdl.supabase.co/storage/v1/object/public/images/userAvatars/$path"
                                )
                            }
                        }
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
                Log.i("Login", e.message.toString())
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

    // Session functions
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

    // Search functions
    fun onSearchTextChange(text: String) {
        Log.i("Search text", text)
        _searchText.value = text
    }

    fun onToggleSearch() {
        _isSearching.value = !_isSearching.value
        if (!_isSearching.value) {
            onSearchTextChange("")
        }
    }

    // Like functions
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

    suspend fun usersLikedPosts(userid: UUID): List<PostEntity> {
        return supabase.postgrest.rpc(
            "fetch_liked_posts",
            mapOf("userid_uuid" to userid.toString())
        )
            .decodeList<PostEntity>()
    }

    fun countLikes() {
        viewModelScope.launch {
            try {
                supabase.postgrest.rpc("count_likes_for_posts")
            } catch (e: Exception) {
                _userState.value = UserState.Error("Error: ${e.message}")
            }
        }
    }

    // Follow functions
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

    suspend fun getFollowedUsers(userId: UUID): List<UUID> {
        return try {
            val fetchedUsers = supabase.from("following")
                .select(columns = Columns.list("followedid", "userid")) {
                    filter {
                        eq("userid", userId)
                    }
                }.decodeList<FollowingEntity>()
            fetchedUsers.map { it.followedid }.also {
                Log.i("fetched users", "UserId: $userId, Fetched users: $it")
            }
        } catch (e: Exception) {
            _userState.value = UserState.Error("Error: ${e.message}")
            Log.i("Error follow", e.message.toString())
            emptyList()
        }
    }


    suspend fun getFollowedPosts(userid: UUID): List<PostEntity> {
        return supabase.postgrest.rpc(
            "get_followed_posts",
            mapOf("user_id" to userid.toString())
        )
            .decodeList<PostEntity>()
    }

    // Profile functions
    fun updateProfile(
        userName: String,
        userEmail: String,
        userAvatar: Uri?,
        userUsername: String
    ) {
        viewModelScope.launch {
            try {
                val userId = supabase.auth.currentUserOrNull()?.id

                val path = upsertAvatarImg(userEmail, userAvatar)

                if (userId != null) {
                    supabase.from("profiles").update({
                        set("name", userName)
                        set("username", userUsername)
                        let {
                            if (path != null) {
                                set(
                                    "avatar_url",
                                    "https://wysgyswdoefgyxubgcdl.supabase.co/storage/v1/object/public/images/userAvatars/$path"
                                )
                            }
                        }
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

    private fun reloadProfile() {
        viewModelScope.launch {
            try {
                supabase.from("profiles")
                    .select {
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

    // Image functions
    private suspend fun upsertAvatarImg(
        userEmail: String,
        avatarUri: Uri?
    ): String? {
        return try {
            avatarUri?.let {
                val path = "$userEmail - ${System.currentTimeMillis()}"
                supabase.storage.from("images/userAvatars").upload(path, it) {
                    upsert = true
                    contentType = ContentType.Image.JPEG
                }

                path
            }
        } catch (e: Exception) {
            _userState.value = UserState.Error("Error: ${e.message}")
            Log.d("Upload", e.message.toString())
            null
        }
    }

    private suspend fun upsertPostImg(
        postId: Int,
        postImgUri: Uri?
    ): String? {
        return try {
            postImgUri?.let {
                val path = "$postId - ${System.currentTimeMillis()}"
                supabase.storage.from("images/postImages").upload(path, it) {
                    upsert = true
                    contentType = ContentType.Image.JPEG
                }

                path
            }
        } catch (e: Exception) {
            _userState.value = UserState.Error("Error: ${e.message}")
            Log.d("Upload", e.message.toString())
            null
        }
    }

    // User functions
    suspend fun fetchCurrentUserId() {
        val fetchedCurrentUserId =
            supabase.auth.retrieveUserForCurrentSession(updateSession = true).id
        _currentUserId.value = fetchedCurrentUserId
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
                .select {
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

    //Post functions
    suspend fun fetchPosts() {
        try {
            val fetchedPosts = supabase.postgrest.rpc("count_likes_for_post") {
                order(
                    column = "id", order = DESCENDING
                )
            }.decodeList<PostEntity>()
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
                    .select {
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
                _posts.value = _posts.value.filter { it.id != postId }
                _userState.value = UserState.Success("Post deleted successfully!")
            } catch (e: Exception) {
                _userState.value = UserState.Error("Error: ${e.message}")
            }
        }
    }

    fun upsertPost(
        post: PostEntity,
        postImgUri: Uri?
    ) {
        viewModelScope.launch {
            try {
                val path = upsertPostImg(post.id!!, postImgUri)

                val postToUpsert = if (!path.isNullOrEmpty()) {
                    post.copy(
                        image = "https://wysgyswdoefgyxubgcdl.supabase.co/storage/v1/object/public/images/postImages/$path"
                    )
                } else {
                    post
                }

                supabase.from("posts").upsert(postToUpsert)
                fetchPosts()
                _userState.value = UserState.Success("Post added successfully!")
            } catch (e: Exception) {
                _userState.value = UserState.Error("Error: ${e.message}")
            }
        }
    }
}