package jp.jiho.cmpprofiledemo.data.repository

import jp.jiho.cmpprofiledemo.domain.AppError
import jp.jiho.cmpprofiledemo.domain.model.Profile
import jp.jiho.cmpprofiledemo.domain.repository.ProfileRepository
import kotlinx.coroutines.yield

class MockProfileRepository(
    private val getProfileError: AppError? = null,
    private val updateProfileError: AppError? = null,
) : ProfileRepository {

    private var current = Profile(
        name = "山田 太郎",
        email = "taro@example.com",
        bio = "Compose Multiplatformエンジニア",
        profileImageUrl = "https://i.pravatar.cc/300",
        notificationEnabled = true
    )

    override suspend fun getProfile(): Result<Profile> =
        getProfileError?.let { Result.failure(it) } ?: Result.success(current)

    override suspend fun updateProfile(profile: Profile): Result<Unit> {
        yield() // simulate async I/O so callers see intermediate states (e.g. isSaving=true)
        return updateProfileError?.let { Result.failure(it) } ?: run {
            current = profile
            Result.success(Unit)
        }
    }
}
