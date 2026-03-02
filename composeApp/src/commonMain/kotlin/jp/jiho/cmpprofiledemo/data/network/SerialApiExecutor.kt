package jp.jiho.cmpprofiledemo.data.network

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.launch

class SerialApiExecutor(scope: CoroutineScope) {
    private val channel = Channel<suspend () -> Unit>(capacity = CHANNEL_CAPACITY)

    init {
        scope.launch {
            for (task in channel) {
                task()
            }
        }
    }

    suspend fun <T> execute(block: suspend () -> T): T {
        val deferred = CompletableDeferred<T>()
        currentCoroutineContext()[Job]?.invokeOnCompletion { cause ->
            if (cause != null) deferred.cancel()
        }
        channel.send {
            if (deferred.isActive) {
                try {
                    deferred.complete(block())
                } catch (e: Throwable) {
                    deferred.completeExceptionally(e)
                }
            }
        }
        return deferred.await()
    }

    // Note: the channel consumer suspends for the entire duration of the parallel block.
    // No other queued tasks will execute until all requests in this block complete.
    suspend fun <T> executeParallel(block: suspend CoroutineScope.() -> T): T {
        val deferred = CompletableDeferred<T>()
        currentCoroutineContext()[Job]?.invokeOnCompletion { cause ->
            if (cause != null) deferred.cancel()
        }
        channel.send {
            if (deferred.isActive) {
                try {
                    deferred.complete(coroutineScope { block() })
                } catch (e: Throwable) {
                    deferred.completeExceptionally(e)
                }
            }
        }
        return deferred.await()
    }

    companion object {
        const val CHANNEL_CAPACITY = 64
    }
}
