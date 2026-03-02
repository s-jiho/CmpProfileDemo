package jp.jiho.cmpprofiledemo.presentation.profile

import app.cash.turbine.test
import io.kotest.matchers.shouldBe
import jp.jiho.cmpprofiledemo.data.repository.MockProfileRepository
import jp.jiho.cmpprofiledemo.domain.AppError
import jp.jiho.cmpprofiledemo.domain.ErrorKind
import jp.jiho.cmpprofiledemo.domain.model.Profile
import jp.jiho.cmpprofiledemo.domain.repository.ProfileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.get
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest : KoinTest {

    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        startKoin {
            modules(module {
                single<ProfileRepository> { MockProfileRepository() }
            })
        }
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
        stopKoin()
    }

    @Test
    fun `initial state is Loading then Success`() = runTest(testDispatcher) {
        val viewModel = ProfileViewModel(get())
        viewModel.uiState.test {
            val loading = awaitItem()
            loading.isLoading shouldBe true

            val success = awaitItem()
            success.isLoading shouldBe false
            success.profile?.name shouldBe "山田 太郎"
            success.error shouldBe null

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `when repository throws state becomes Error`() = runTest(testDispatcher) {
        val failingRepo = object : ProfileRepository {
            override suspend fun getProfile() = Result.failure<Profile>(RuntimeException("Network error"))
            override suspend fun updateProfile(profile: Profile) = Result.failure<Unit>(RuntimeException())
        }
        val viewModel = ProfileViewModel(failingRepo)
        viewModel.uiState.test {
            val loading = awaitItem()
            loading.isLoading shouldBe true

            val error = awaitItem()
            error.isLoading shouldBe false
            error.profile shouldBe null
            error.error shouldBe AppError.Unknown("Network error")

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `when repository returns ClientOffline error state error type is ClientOffline`() = runTest(testDispatcher) {
        val offlineRepo = object : ProfileRepository {
            override suspend fun getProfile() = Result.failure<Profile>(AppError.ClientOffline)
            override suspend fun updateProfile(profile: Profile) = Result.failure<Unit>(RuntimeException())
        }
        val viewModel = ProfileViewModel(offlineRepo)
        viewModel.uiState.test {
            val loading = awaitItem()
            loading.isLoading shouldBe true

            val error = awaitItem()
            error.isLoading shouldBe false
            error.error shouldBe AppError.ClientOffline

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `when repository returns Http error state error type is Http`() = runTest(testDispatcher) {
        val httpRepo = object : ProfileRepository {
            override suspend fun getProfile() = Result.failure<Profile>(
                AppError.Http(statusCode = 404, kind = ErrorKind.NotFound, detail = "Not Found")
            )
            override suspend fun updateProfile(profile: Profile) = Result.failure<Unit>(RuntimeException())
        }
        val viewModel = ProfileViewModel(httpRepo)
        viewModel.uiState.test {
            val loading = awaitItem()
            loading.isLoading shouldBe true

            val error = awaitItem()
            error.isLoading shouldBe false
            error.error shouldBe AppError.Http(statusCode = 404, kind = ErrorKind.NotFound, detail = "Not Found")

            cancelAndIgnoreRemainingEvents()
        }
    }
}
