package dev.cc231054.dwitter_ccl3.data

import kotlinx.serialization.Serializable

@Serializable
data class PostEntity (
    val id: Int? = null,
    val userid: String,
    val created_at: String? = null,
    val post: String,
    val image: String?,
)