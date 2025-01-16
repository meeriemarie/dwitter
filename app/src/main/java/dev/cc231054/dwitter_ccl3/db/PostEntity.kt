package dev.cc231054.dwitter_ccl3.db

import kotlinx.serialization.Serializable

@Serializable
data class PostEntity (
    val id: Int,
//    val userid: Int,
    val created_at: String,
    val post: String,
    val image: String,
)