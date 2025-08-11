@file:Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")

package geet.berkers.kmpredis

import geet.berkers.kmpredis.interfaces.RedisClientInterface
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.browser.window
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.w3c.fetch.Headers
import org.w3c.fetch.RequestInit
import org.w3c.fetch.Response
import kotlin.js.JsAny
import kotlinx.coroutines.await

@Serializable
data class KeyValue(val key: String, val value: String?)

class WasmRedisClientHttp : RedisClientInterface {

    private var baseUrl: String = ""
    private val updates = MutableSharedFlow<Pair<String, String?>>()

    override suspend fun disconnect() {
        baseUrl = ""
    }

    override suspend fun connect(uri: String): Boolean {
        baseUrl = uri.removeSuffix("/")
        return try {
            // Optional: test connection with ping endpoint
            val response: Response = window.fetch("$baseUrl/ping").await()
            response.ok
        } catch (e: Throwable) {
            consoleError("Connection failed: ${e.message}")
            false
        }
    }

    override suspend fun addKey(key: String, value: String): Boolean {
        val payload = KeyValue(key, value)
        return try {
            val response: Response = window.fetch("$baseUrl/set", requestInit("POST", payload)).await()
            response.ok
        } catch (e: Throwable) {
            consoleError("Add key failed: ${e.message}")
            false
        }
    }

    override suspend fun deleteKey(key: String): Boolean {
        val payload = KeyValue(key, null)
        return try {
            val response: Response = window.fetch("$baseUrl/delete", requestInit("POST", payload)).await()
            response.ok
        } catch (e: Throwable) {
            consoleError("Delete key failed: ${e.message}")
            false
        }
    }

    override fun getAllKeyValues(): Flow<Pair<String, String?>> = flow {
        try {
            val response : Response = window.fetch("$baseUrl/keys").await()
            if (response.ok) {
                val jsonText : String = response.text().await()
                println("Received JSON: $jsonText")

                val parsed = Json {
                    ignoreUnknownKeys = true
                }.decodeFromString<List<KeyValue>>(jsonText)

                for (item in parsed) {
                    emit(item.key to item.value)
                }
            } else {
                println("Response not OK: ${response.status}")
            }
        } catch (e: Throwable) {
            println("Error parsing keys: ${e.message}")
        }
    }


//    override fun getAllKeyValues(): Flow<Pair<String, String?>> = flow {
//        try {
//            val response: Response = window.fetch("$baseUrl/keys").await()
//            if (response.ok) {
//                val jsonText: String = response.text().await()
//                val parsed: List<KeyValue> = Json.decodeFromString(jsonText)
//                parsed.forEach { emit(it.key to it.value) }
//            } else {
//                consoleError("Failed to fetch keys: HTTP ${response.status}")
//            }
//        } catch (e: Throwable) {
//            consoleError("getAllKeyValues failed: ${e.message}")
//        }
//    }

    // Helper function for POST request bodies
    private fun requestInit(method: String, body: Any): RequestInit {
        val jsonBody = Json.encodeToString(body)
        return RequestInit(
            method = method,
            headers = Headers().apply {
                append("Content-Type", "application/json")
            },
            body = jsonBody as JsAny
        )
    }
}