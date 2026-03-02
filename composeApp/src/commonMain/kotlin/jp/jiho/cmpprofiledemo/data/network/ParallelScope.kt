package jp.jiho.cmpprofiledemo.data.network

import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class ParallelScope(@PublishedApi internal val apiClient: ApiClient) {
    suspend inline fun <reified T> get(url: String): Result<T> =
        apiClient.safeRequest { apiClient.httpClient.get(url) }

    suspend inline fun <reified T> post(url: String, body: Any): Result<T> =
        apiClient.safeRequest {
            apiClient.httpClient.post(url) {
                contentType(ContentType.Application.Json)
                setBody(body)
            }
        }

    suspend inline fun <reified T> put(url: String, body: Any): Result<T> =
        apiClient.safeRequest {
            apiClient.httpClient.put(url) {
                contentType(ContentType.Application.Json)
                setBody(body)
            }
        }
}
