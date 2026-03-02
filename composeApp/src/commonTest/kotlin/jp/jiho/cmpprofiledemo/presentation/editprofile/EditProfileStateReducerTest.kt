package jp.jiho.cmpprofiledemo.presentation.editprofile

import io.kotest.matchers.shouldBe
import jp.jiho.cmpprofiledemo.domain.AppError
import kotlin.test.Test

class EditProfileStateReducerTest {

    @Test
    fun `NameChanged updates name and clears nameError`() {
        val initial = EditProfileUiState(
            name = "old",
            fieldErrors = EditProfileFieldErrors(nameError = EditProfileValidationError.NameRequired)
        )
        val result = EditProfileStateReducer.reduce(initial, EditProfileEvent.NameChanged("new"))
        result.name shouldBe "new"
        result.fieldErrors.nameError shouldBe null
    }

    @Test
    fun `EmailChanged updates email and clears emailError`() {
        val initial = EditProfileUiState(
            email = "old@example.com",
            fieldErrors = EditProfileFieldErrors(emailError = EditProfileValidationError.InvalidEmailFormat)
        )
        val result = EditProfileStateReducer.reduce(initial, EditProfileEvent.EmailChanged("new@example.com"))
        result.email shouldBe "new@example.com"
        result.fieldErrors.emailError shouldBe null
    }

    @Test
    fun `BioChanged updates bio and clears bioError`() {
        val initial = EditProfileUiState(
            bio = "old",
            fieldErrors = EditProfileFieldErrors(bioError = EditProfileValidationError.BioTooLong)
        )
        val result = EditProfileStateReducer.reduce(initial, EditProfileEvent.BioChanged("new"))
        result.bio shouldBe "new"
        result.fieldErrors.bioError shouldBe null
    }

    @Test
    fun `ImageUrlChanged updates profileImageUrl and clears profileImageUrlError`() {
        val initial = EditProfileUiState(
            profileImageUrl = "http://old.com",
            fieldErrors = EditProfileFieldErrors(profileImageUrlError = EditProfileValidationError.InvalidHttpsUrl)
        )
        val result = EditProfileStateReducer.reduce(initial, EditProfileEvent.ImageUrlChanged("https://new.com"))
        result.profileImageUrl shouldBe "https://new.com"
        result.fieldErrors.profileImageUrlError shouldBe null
    }

    @Test
    fun `NotificationToggled updates notificationEnabled`() {
        val initial = EditProfileUiState(notificationEnabled = false)
        val result = EditProfileStateReducer.reduce(initial, EditProfileEvent.NotificationToggled(true))
        result.notificationEnabled shouldBe true
    }

    @Test
    fun `SaveErrorDismissed clears saveError`() {
        val initial = EditProfileUiState(saveError = AppError.Unknown("error"))
        val result = EditProfileStateReducer.reduce(initial, EditProfileEvent.SaveErrorDismissed)
        result.saveError shouldBe null
    }

    @Test
    fun `ValidationFailed sets all field errors`() {
        val errors = EditProfileFieldErrors(
            nameError = EditProfileValidationError.NameRequired,
            emailError = EditProfileValidationError.InvalidEmailFormat,
            bioError = null,
            profileImageUrlError = EditProfileValidationError.InvalidHttpsUrl,
        )
        val initial = EditProfileUiState()
        val result = EditProfileStateReducer.reduce(initial, EditProfileEvent.ValidationFailed(errors))
        result.fieldErrors.nameError shouldBe EditProfileValidationError.NameRequired
        result.fieldErrors.emailError shouldBe EditProfileValidationError.InvalidEmailFormat
        result.fieldErrors.bioError shouldBe null
        result.fieldErrors.profileImageUrlError shouldBe EditProfileValidationError.InvalidHttpsUrl
    }

    @Test
    fun `SavingStarted sets isSaving true and clears all errors`() {
        val initial = EditProfileUiState(
            isSaving = false,
            saveError = AppError.Unknown("err"),
            fieldErrors = EditProfileFieldErrors(
                nameError = EditProfileValidationError.NameRequired,
                emailError = EditProfileValidationError.InvalidEmailFormat,
                bioError = EditProfileValidationError.BioTooLong,
                profileImageUrlError = EditProfileValidationError.InvalidHttpsUrl,
            )
        )
        val result = EditProfileStateReducer.reduce(initial, EditProfileEvent.SavingStarted)
        result.isSaving shouldBe true
        result.saveError shouldBe null
        result.fieldErrors.nameError shouldBe null
        result.fieldErrors.emailError shouldBe null
        result.fieldErrors.bioError shouldBe null
        result.fieldErrors.profileImageUrlError shouldBe null
    }

    @Test
    fun `SaveSucceeded sets savedEvent true and isSaving false`() {
        val initial = EditProfileUiState(isSaving = true)
        val result = EditProfileStateReducer.reduce(initial, EditProfileEvent.SaveSucceeded)
        result.isSaving shouldBe false
        result.savedEvent shouldBe true
    }

    @Test
    fun `SaveFailed sets saveError and isSaving false`() {
        val error = AppError.Unknown("保存失敗")
        val initial = EditProfileUiState(isSaving = true)
        val result = EditProfileStateReducer.reduce(initial, EditProfileEvent.SaveFailed(error))
        result.isSaving shouldBe false
        result.saveError shouldBe error
    }
}
