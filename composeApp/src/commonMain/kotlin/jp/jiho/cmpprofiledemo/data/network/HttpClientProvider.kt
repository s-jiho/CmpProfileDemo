package jp.jiho.cmpprofiledemo.data.network

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.serialization.json.Json

class HttpClientProvider(
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
) : AutoCloseable {
    val serialExecutor: SerialApiExecutor by lazy { SerialApiExecutor(scope) }

    val client: HttpClient by lazy {
        HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = false
                    coerceInputValues = true
                })
            }
        }
    }

    val apiClient: ApiClient by lazy { ApiClient(client, serialExecutor) }

    override fun close() {
        scope.cancel()
        client.close()
    }
}
