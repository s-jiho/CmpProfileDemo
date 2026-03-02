package jp.jiho.cmpprofiledemo.data.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess

class ApiClient(
    @PublishedApi internal val httpClient: HttpClient,
    @PublishedApi internal val executor: SerialApiExecutor,
) {
    suspend inline fun <reified T> get(url: String): Result<T> =
        executor.execute { safeRequest { httpClient.get(url) } }

    suspend inline fun <reified T> post(url: String, body: Any): Result<T> =
        executor.execute {
            safeRequest {
                httpClient.post(url) {
                    contentType(ContentType.Application.Json)
                    setBody(body)
                }
            }
        }

    suspend inline fun <reified T> put(url: String, body: Any): Result<T> =
        executor.execute {
            safeRequest {
                httpClient.put(url) {
                    contentType(ContentType.Application.Json)
                    setBody(body)
                }
            }
        }

    suspend fun <T> parallel(block: suspend ParallelScope.() -> T): T =
        executor.executeParallel { ParallelScope(this@ApiClient).block() }

    @PublishedApi
    internal suspend inline fun <reified T> safeRequest(
        crossinline block: suspend () -> HttpResponse,
    ): Result<T> = try {
        val response = block()
        if (response.status.isSuccess()) {
            Result.success(response.body<T>())
        } else {
            Result.failure(response.toAppError())
        }
    } catch (e: Exception) {
        Result.failure(e.toAppError())
    }
}
