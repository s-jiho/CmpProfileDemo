package jp.jiho.cmpprofiledemo.presentation.profile

import io.kotest.matchers.shouldBe
import jp.jiho.cmpprofiledemo.domain.AppError
import jp.jiho.cmpprofiledemo.domain.ErrorKind
import jp.jiho.cmpprofiledemo.domain.model.Profile
import kotlin.test.Test

class ProfileStateReducerTest {

    private val dummyProfile = Profile(
        name = "テスト 太郎",
        email = "test@example.com",
        bio = "Bio",
        profileImageUrl = "https://example.com/image.png",
        notificationEnabled = false,
    )

    @Test
    fun `LoadingStarted sets isLoading true and clears profile and error`() {
        val initial = ProfileUiState(
            isLoading = false,
            profile = dummyProfile,
            error = AppError.Unknown("err"),
        )
        val result = ProfileStateReducer.reduce(initial, ProfileEvent.LoadingStarted)
        result.isLoading shouldBe true
        result.profile shouldBe null
        result.error shouldBe null
    }

    @Test
    fun `ProfileLoaded sets profile and isLoading false`() {
        val staleProfile = dummyProfile.copy(name = "Old Name")
        val initial = ProfileUiState(isLoading = true, profile = staleProfile)
        val result = ProfileStateReducer.reduce(initial, ProfileEvent.ProfileLoaded(dummyProfile))
        result.isLoading shouldBe false
        result.profile shouldBe dummyProfile
        result.error shouldBe null
    }

    @Test
    fun `LoadFailed sets error and isLoading false`() {
        val error = AppError.Http(statusCode = 500, kind = ErrorKind.ServerError, detail = "Server Error")
        val initial = ProfileUiState(isLoading = true)
        val result = ProfileStateReducer.reduce(initial, ProfileEvent.LoadFailed(error))
        result.isLoading shouldBe false
        result.profile shouldBe null
        result.error shouldBe error
    }

    @Test
    fun `LoadFailed with ClientOffline sets ClientOffline error`() {
        val initial = ProfileUiState(isLoading = true)
        val result = ProfileStateReducer.reduce(initial, ProfileEvent.LoadFailed(AppError.ClientOffline))
        result.isLoading shouldBe false
        result.error shouldBe AppError.ClientOffline
    }
}
