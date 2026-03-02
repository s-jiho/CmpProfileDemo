package jp.jiho.cmpprofiledemo.presentation.editprofile

import io.konform.validation.Invalid
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class EditProfileValidatorTest {

    @Test
    fun `toFieldErrors - blank name maps to nameError NameRequired`() {
        val state = EditProfileUiState(name = "", email = "a@b.com")
        val result = EditProfileValidator.validation(state) as Invalid
        val errors = EditProfileValidator.toFieldErrors(result)

        errors.nameError shouldBe EditProfileValidationError.NameRequired
        errors.emailError shouldBe null
        errors.bioError shouldBe null
        errors.profileImageUrlError shouldBe null
    }

    @Test
    fun `toFieldErrors - invalid email format maps to emailError InvalidEmailFormat`() {
        val state = EditProfileUiState(name = "太郎", email = "not-an-email")
        val result = EditProfileValidator.validation(state) as Invalid
        val errors = EditProfileValidator.toFieldErrors(result)

        errors.nameError shouldBe null
        errors.emailError shouldBe EditProfileValidationError.InvalidEmailFormat
        errors.bioError shouldBe null
        errors.profileImageUrlError shouldBe null
    }

    @Test
    fun `toFieldErrors - bio over 100 chars maps to bioError BioTooLong`() {
        val state = EditProfileUiState(name = "太郎", email = "a@b.com", bio = "あ".repeat(101))
        val result = EditProfileValidator.validation(state) as Invalid
        val errors = EditProfileValidator.toFieldErrors(result)

        errors.nameError shouldBe null
        errors.emailError shouldBe null
        errors.bioError shouldBe EditProfileValidationError.BioTooLong
        errors.profileImageUrlError shouldBe null
    }

    @Test
    fun `toFieldErrors - http image url maps to profileImageUrlError InvalidHttpsUrl`() {
        val state = EditProfileUiState(
            name = "太郎",
            email = "a@b.com",
            profileImageUrl = "http://example.com/img.png"
        )
        val result = EditProfileValidator.validation(state) as Invalid
        val errors = EditProfileValidator.toFieldErrors(result)

        errors.nameError shouldBe null
        errors.emailError shouldBe null
        errors.bioError shouldBe null
        errors.profileImageUrlError shouldBe EditProfileValidationError.InvalidHttpsUrl
    }

    @Test
    fun `toFieldErrors - multiple errors each mapped to correct field`() {
        val state = EditProfileUiState(name = "", email = "bad", bio = "あ".repeat(101))
        val result = EditProfileValidator.validation(state) as Invalid
        val errors = EditProfileValidator.toFieldErrors(result)

        errors.nameError shouldBe EditProfileValidationError.NameRequired
        errors.emailError shouldBe EditProfileValidationError.InvalidEmailFormat
        errors.bioError shouldBe EditProfileValidationError.BioTooLong
        errors.profileImageUrlError shouldBe null
    }
}
