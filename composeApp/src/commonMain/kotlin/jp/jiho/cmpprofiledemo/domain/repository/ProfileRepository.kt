package jp.jiho.cmpprofiledemo.domain.repository

import jp.jiho.cmpprofiledemo.domain.model.Profile

interface ProfileRepository {
    suspend fun getProfile(): Result<Profile>
    suspend fun updateProfile(profile: Profile): Result<Unit>
}
