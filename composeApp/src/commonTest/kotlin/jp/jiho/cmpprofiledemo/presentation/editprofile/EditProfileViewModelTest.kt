package jp.jiho.cmpprofiledemo.presentation.editprofile

import app.cash.turbine.test
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import jp.jiho.cmpprofiledemo.data.repository.MockProfileRepository
import jp.jiho.cmpprofiledemo.domain.AppError
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
class EditProfileViewModelTest : KoinTest {

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
    fun `initial state loads current profile`() = runTest(testDispatcher) {
        val viewModel = EditProfileViewModel(get())
        viewModel.uiState.test {
            awaitItem() // initial blank state before init coroutine runs
            val state = awaitItem()
            state.name shouldBe "山田 太郎"
            state.email shouldBe "taro@example.com"
            state.notificationEnabled shouldBe true
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onNameChange updates name in state`() = runTest(testDispatcher) {
        val viewModel = EditProfileViewModel(get())
        viewModel.uiState.test {
            awaitItem()
            awaitItem()
            viewModel.onNameChange("新しい名前")
            awaitItem().name shouldBe "新しい名前"
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `profile loads only when uiState is subscribed not at construction`() = runTest(testDispatcher) {
        val viewModel = EditProfileViewModel(get())
        testDispatcher.scheduler.advanceUntilIdle() // advance before any subscription

        viewModel.uiState.test {
            val firstState = awaitItem()
            firstState.name shouldBe "" // blank: loading not triggered yet before subscription
            val loadedState = awaitItem()
            loadedState.name shouldBe "山田 太郎"
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onSave triggers isSaving then savedEvent`() = runTest(testDispatcher) {
        val viewModel = EditProfileViewModel(get())
        viewModel.uiState.test {
            awaitItem()
            awaitItem()
            viewModel.onSave()
            awaitItem().isSaving shouldBe true
            val saved = awaitItem()
            saved.isSaving shouldBe false
            saved.savedEvent shouldBe true
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onSave with blank name sets nameError and does not save`() = runTest(testDispatcher) {
        val viewModel = EditProfileViewModel(get())
        viewModel.uiState.test {
            awaitItem() // initial blank state
            awaitItem() // profile loaded (name = "山田 太郎")
            viewModel.onNameChange("")
            awaitItem() // name cleared
            viewModel.onSave()
            val state = awaitItem()
            state.fieldErrors.nameError shouldBe EditProfileValidationError.NameRequired
            state.savedEvent shouldBe false
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onSave with blank email sets emailError and does not save`() = runTest(testDispatcher) {
        val viewModel = EditProfileViewModel(get())
        viewModel.uiState.test {
            awaitItem()
            awaitItem()
            viewModel.onEmailChange("")
            awaitItem()
            viewModel.onSave()
            val state = awaitItem()
            state.fieldErrors.emailError shouldBe EditProfileValidationError.EmailRequired
            state.savedEvent shouldBe false
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onSave with invalid email format sets emailError`() = runTest(testDispatcher) {
        val viewModel = EditProfileViewModel(get())
        viewModel.uiState.test {
            awaitItem()
            awaitItem()
            viewModel.onEmailChange("not-an-email")
            awaitItem()
            viewModel.onSave()
            val state = awaitItem()
            state.fieldErrors.emailError shouldBe EditProfileValidationError.InvalidEmailFormat
            state.savedEvent shouldBe false
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onSave with bio over 100 chars sets bioError`() = runTest(testDispatcher) {
        val viewModel = EditProfileViewModel(get())
        viewModel.uiState.test {
            awaitItem()
            awaitItem()
            viewModel.onBioChange("あ".repeat(101))
            awaitItem()
            viewModel.onSave()
            val state = awaitItem()
            state.fieldErrors.bioError shouldNotBe null
            state.savedEvent shouldBe false
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onSave with bio exactly 100 chars does not set bioError`() = runTest(testDispatcher) {
        val viewModel = EditProfileViewModel(get())
        viewModel.uiState.test {
            awaitItem()
            awaitItem()
            viewModel.onBioChange("あ".repeat(100))
            awaitItem()
            viewModel.onSave()
            // isSaving should become true (validation passed)
            val state = awaitItem()
            state.fieldErrors.bioError shouldBe null
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onSave with http image url sets profileImageUrlError`() = runTest(testDispatcher) {
        val viewModel = EditProfileViewModel(get())
        viewModel.uiState.test {
            awaitItem()
            awaitItem()
            viewModel.onImageUrlChange("http://example.com/image.png")
            awaitItem()
            viewModel.onSave()
            val state = awaitItem()
            state.fieldErrors.profileImageUrlError shouldNotBe null
            state.savedEvent shouldBe false
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onSave with empty image url does not set profileImageUrlError`() = runTest(testDispatcher) {
        val viewModel = EditProfileViewModel(get())
        viewModel.uiState.test {
            awaitItem()
            awaitItem()
            viewModel.onImageUrlChange("")
            awaitItem()
            viewModel.onSave()
            // isSaving should become true (validation passed)
            val state = awaitItem()
            state.fieldErrors.profileImageUrlError shouldBe null
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onSave with https scheme only sets profileImageUrlError`() = runTest(testDispatcher) {
        val viewModel = EditProfileViewModel(get())
        viewModel.uiState.test {
            awaitItem()
            awaitItem()
            viewModel.onImageUrlChange("https")
            awaitItem()
            viewModel.onSave()
            val state = awaitItem()
            state.fieldErrors.profileImageUrlError shouldNotBe null
            state.savedEvent shouldBe false
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onSave with url containing spaces sets profileImageUrlError`() = runTest(testDispatcher) {
        val viewModel = EditProfileViewModel(get())
        viewModel.uiState.test {
            awaitItem()
            awaitItem()
            viewModel.onImageUrlChange("https://not a valid url")
            awaitItem()
            viewModel.onSave()
            val state = awaitItem()
            state.fieldErrors.profileImageUrlError shouldNotBe null
            state.savedEvent shouldBe false
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onSave with 51 emoji chars does not set bioError`() = runTest(testDispatcher) {
        val viewModel = EditProfileViewModel(get())
        viewModel.uiState.test {
            awaitItem()
            awaitItem()
            viewModel.onBioChange("😀".repeat(51)) // 51 code points, but String.length == 102
            awaitItem()
            viewModel.onSave()
            val state = awaitItem()
            state.fieldErrors.bioError shouldBe null
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onSave with 101 emoji chars sets bioError`() = runTest(testDispatcher) {
        val viewModel = EditProfileViewModel(get())
        viewModel.uiState.test {
            awaitItem()
            awaitItem()
            viewModel.onBioChange("😀".repeat(101)) // 101 code points
            awaitItem()
            viewModel.onSave()
            val state = awaitItem()
            state.fieldErrors.bioError shouldNotBe null
            state.savedEvent shouldBe false
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onSave with valid https image url does not set profileImageUrlError`() = runTest(testDispatcher) {
        val viewModel = EditProfileViewModel(get())
        viewModel.uiState.test {
            awaitItem()
            awaitItem()
            viewModel.onImageUrlChange("https://example.com/image.png")
            awaitItem()
            viewModel.onSave()
            val state = awaitItem()
            state.fieldErrors.profileImageUrlError shouldBe null
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onNameChange clears nameError after validation failure`() = runTest(testDispatcher) {
        val viewModel = EditProfileViewModel(get())
        viewModel.uiState.test {
            awaitItem()
            awaitItem()
            viewModel.onNameChange("")
            awaitItem()
            viewModel.onSave()
            awaitItem().fieldErrors.nameError shouldNotBe null
            viewModel.onNameChange("新しい名前")
            awaitItem().fieldErrors.nameError shouldBe null
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onEmailChange clears emailError after validation failure`() = runTest(testDispatcher) {
        val viewModel = EditProfileViewModel(get())
        viewModel.uiState.test {
            awaitItem()
            awaitItem()
            viewModel.onEmailChange("")
            awaitItem()
            viewModel.onSave()
            awaitItem().fieldErrors.emailError shouldNotBe null
            viewModel.onEmailChange("taro@example.com")
            awaitItem().fieldErrors.emailError shouldBe null
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onBioChange clears bioError after validation failure`() = runTest(testDispatcher) {
        val viewModel = EditProfileViewModel(get())
        viewModel.uiState.test {
            awaitItem()
            awaitItem()
            viewModel.onBioChange("あ".repeat(101))
            awaitItem()
            viewModel.onSave()
            awaitItem().fieldErrors.bioError shouldNotBe null
            viewModel.onBioChange("短い自己紹介")
            awaitItem().fieldErrors.bioError shouldBe null
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onSave with repository failure sets saveError and does not set savedEvent`() = runTest(testDispatcher) {
        val error = AppError.Unknown("保存に失敗しました")
        val viewModel = EditProfileViewModel(MockProfileRepository(updateProfileError = error))
        viewModel.uiState.test {
            awaitItem()
            awaitItem()
            viewModel.onSave()
            awaitItem().isSaving shouldBe true
            val state = awaitItem()
            state.isSaving shouldBe false
            state.saveError shouldBe error
            state.savedEvent shouldBe false
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onSaveErrorDismissed clears saveError`() = runTest(testDispatcher) {
        val error = AppError.Unknown("保存に失敗しました")
        val viewModel = EditProfileViewModel(MockProfileRepository(updateProfileError = error))
        viewModel.uiState.test {
            awaitItem()
            awaitItem()
            viewModel.onSave()
            awaitItem() // isSaving = true
            awaitItem().saveError shouldBe error
            viewModel.onSaveErrorDismissed()
            awaitItem().saveError shouldBe null
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onImageUrlChange clears profileImageUrlError after validation failure`() = runTest(testDispatcher) {
        val viewModel = EditProfileViewModel(get())
        viewModel.uiState.test {
            awaitItem()
            awaitItem()
            viewModel.onImageUrlChange("http://example.com/image.png")
            awaitItem()
            viewModel.onSave()
            awaitItem().fieldErrors.profileImageUrlError shouldNotBe null
            viewModel.onImageUrlChange("https://example.com/image.png")
            awaitItem().fieldErrors.profileImageUrlError shouldBe null
            cancelAndIgnoreRemainingEvents()
        }
    }
}
