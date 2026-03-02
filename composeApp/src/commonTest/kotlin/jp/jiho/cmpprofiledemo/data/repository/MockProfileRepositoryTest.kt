package jp.jiho.cmpprofiledemo.data.repository

import io.kotest.matchers.shouldBe
import jp.jiho.cmpprofiledemo.domain.AppError
import jp.jiho.cmpprofiledemo.domain.model.Profile
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class MockProfileRepositoryTest {

    private val repository = MockProfileRepository()

    @Test
    fun `getProfile returns default profile`() = runTest {
        val profile = repository.getProfile().getOrThrow()
        profile.name shouldBe "山田 太郎"
        profile.email shouldBe "taro@example.com"
        profile.notificationEnabled shouldBe true
    }

    @Test
    fun `updateProfile saves and getProfile returns updated data`() = runTest {
        val updated = Profile(
            name = "鈴木 花子",
            email = "hanako@example.com",
            bio = "新しい自己紹介",
            profileImageUrl = "https://example.com/new.png",
            notificationEnabled = false
        )
        repository.updateProfile(updated).isSuccess shouldBe true
        val fetched = repository.getProfile().getOrThrow()
        fetched.name shouldBe "鈴木 花子"
        fetched.notificationEnabled shouldBe false
    }

    // --- getProfile failure tests ---

    @Test
    fun `getProfile returns failure when getProfileError is Unknown`() = runTest {
        val error = AppError.Unknown("fetch failed")
        val repository = MockProfileRepository(getProfileError = error)

        val result = repository.getProfile()

        result.isFailure shouldBe true
        result.exceptionOrNull() shouldBe error
    }

    @Test
    fun `getProfile returns failure when getProfileError is ClientOffline`() = runTest {
        val repository = MockProfileRepository(getProfileError = AppError.ClientOffline)

        val result = repository.getProfile()

        result.isFailure shouldBe true
        result.exceptionOrNull() shouldBe AppError.ClientOffline
    }

    // --- updateProfile failure tests ---

    @Test
    fun `updateProfile returns failure when updateProfileError is Unknown`() = runTest {
        val error = AppError.Unknown("update failed")
        val repository = MockProfileRepository(updateProfileError = error)
        val profile = dummyProfile()

        val result = repository.updateProfile(profile)

        result.isFailure shouldBe true
        result.exceptionOrNull() shouldBe error
    }

    @Test
    fun `updateProfile returns failure when updateProfileError is ClientOffline`() = runTest {
        val repository = MockProfileRepository(updateProfileError = AppError.ClientOffline)
        val profile = dummyProfile()

        val result = repository.updateProfile(profile)

        result.isFailure shouldBe true
        result.exceptionOrNull() shouldBe AppError.ClientOffline
    }


    @Test
    fun `updateProfile failure does not mutate stored profile`() = runTest {
        val repo = MockProfileRepository(updateProfileError = AppError.ClientOffline)
        val original = repo.getProfile().getOrThrow()
        repo.updateProfile(dummyProfile())
        repo.getProfile().getOrThrow() shouldBe original
    }

    private fun dummyProfile() = Profile(
        name = "テスト 太郎",
        email = "test@example.com",
        bio = "bio",
        profileImageUrl = "",
        notificationEnabled = false
    )
}
