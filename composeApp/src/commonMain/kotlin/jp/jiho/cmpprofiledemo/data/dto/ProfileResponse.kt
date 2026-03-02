package jp.jiho.cmpprofiledemo.data.dto

import jp.jiho.cmpprofiledemo.domain.model.Profile
import kotlinx.serialization.Serializable

@Serializable
data class ProfileResponse(
    val name: String,
    val email: String,
    val bio: String,
    val profileImageUrl: String,
    val notificationEnabled: Boolean,
)

fun ProfileResponse.toDomain() = Profile(
    name = name,
    email = email,
    bio = bio,
    profileImageUrl = profileImageUrl,
    notificationEnabled = notificationEnabled,
)
