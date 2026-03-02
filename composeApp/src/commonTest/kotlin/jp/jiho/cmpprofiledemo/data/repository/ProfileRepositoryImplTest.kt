package jp.jiho.cmpprofiledemo.data.repository

import io.kotest.matchers.shouldBe
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import jp.jiho.cmpprofiledemo.data.network.ApiClient
import jp.jiho.cmpprofiledemo.data.network.SerialApiExecutor
import jp.jiho.cmpprofiledemo.domain.model.Profile
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test

class ProfileRepositoryImplTest {

    private val profileJson = """
        {
          "name": "Test User",
          "email": "test@example.com",
          "bio": "Hello",
          "profileImageUrl": "https://example.com/avatar.png",
          "notificationEnabled": true
        }
    """.trimIndent()

    @Test
    fun `getProfile sends request to baseUrl + profile`() = runTest {
        var capturedUrl: String? = null
        val engine = MockEngine { request ->
            capturedUrl = request.url.toString()
            respond(
                content = profileJson,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        val repo = buildRepo(engine, "https://test-api.example.com")

        repo.getProfile()

        capturedUrl shouldBe "https://test-api.example.com/profile"
    }

    @Test
    fun `getProfile returns success with mapped Profile`() = runTest {
        val engine = MockEngine { _ ->
            respond(
                content = profileJson,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        val repo = buildRepo(engine, "https://test-api.example.com")

        val result = repo.getProfile()

        result.isSuccess shouldBe true
        val profile = result.getOrThrow()
        profile.name shouldBe "Test User"
        profile.email shouldBe "test@example.com"
        profile.bio shouldBe "Hello"
        profile.profileImageUrl shouldBe "https://example.com/avatar.png"
        profile.notificationEnabled shouldBe true
    }

    @Test
    fun `updateProfile sends PUT to baseUrl + profile`() = runTest {
        var capturedUrl: String? = null
        var capturedMethod: HttpMethod? = null
        val engine = MockEngine { request ->
            capturedUrl = request.url.toString()
            capturedMethod = request.method
            respond(
                content = "",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        val repo = buildRepo(engine, "https://test-api.example.com")
        val profile = Profile(
            name = "Test User",
            email = "test@example.com",
            bio = "Hello",
            profileImageUrl = "https://example.com/avatar.png",
            notificationEnabled = true,
        )

        repo.updateProfile(profile)

        capturedUrl shouldBe "https://test-api.example.com/profile"
        capturedMethod shouldBe HttpMethod.Put
    }

    private fun TestScope.buildRepo(engine: MockEngine, baseUrl: String): ProfileRepositoryImpl {
        val client = HttpClient(engine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }
        val executor = SerialApiExecutor(backgroundScope)
        val apiClient = ApiClient(client, executor)
        return ProfileRepositoryImpl(apiClient, baseUrl = baseUrl)
    }
}
