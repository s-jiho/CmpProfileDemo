package jp.jiho.cmpprofiledemo.domain.model

data class Profile(
    val name: String,
    val email: String,
    val bio: String,
    val profileImageUrl: String,
    val notificationEnabled: Boolean
)
