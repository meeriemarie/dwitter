package dev.cc231054.dwitter_ccl3.data

import kotlinx.serialization.Serializable
import java.util.UUID


@Serializable
data class LikedPostEntity (
    @Serializable(with = UUIDSerializer::class)
    val userid: UUID,
    val postid: Int
)