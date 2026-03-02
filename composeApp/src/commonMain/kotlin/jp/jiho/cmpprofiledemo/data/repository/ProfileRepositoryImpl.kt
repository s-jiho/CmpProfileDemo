package jp.jiho.cmpprofiledemo.data.repository

import jp.jiho.cmpprofiledemo.BuildKonfig
import jp.jiho.cmpprofiledemo.data.dto.ProfileResponse
import jp.jiho.cmpprofiledemo.data.dto.toDomain
import jp.jiho.cmpprofiledemo.data.dto.toRequest
import jp.jiho.cmpprofiledemo.data.network.ApiClient
import jp.jiho.cmpprofiledemo.domain.model.Profile
import jp.jiho.cmpprofiledemo.domain.repository.ProfileRepository

class ProfileRepositoryImpl(
    private val apiClient: ApiClient,
    private val baseUrl: String = BuildKonfig.BASE_URL,
) : ProfileRepository {
    override suspend fun getProfile(): Result<Profile> =
        apiClient.get<ProfileResponse>("$baseUrl/profile").map { it.toDomain() }

    override suspend fun updateProfile(profile: Profile): Result<Unit> =
        apiClient.put("$baseUrl/profile", profile.toRequest())
}
