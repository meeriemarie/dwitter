package dev.cc231054.dwitter_ccl3.data

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class FollowingEntity(
    @Serializable(with = UUIDSerializer::class)
    val followedid: UUID,
    @Serializable(with = UUIDSerializer::class)
    val userid: UUID,
)
