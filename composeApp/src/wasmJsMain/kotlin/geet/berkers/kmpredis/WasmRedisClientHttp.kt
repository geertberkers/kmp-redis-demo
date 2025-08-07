package geet.berkers.kmpredis

import geet.berkers.kmpredis.interfaces.RedisClientInterface
import kotlinx.browser.window
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
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

class WasmRedisClientHttp(val uri: String = "http://localhost:8082") : RedisClientInterface {

    private var baseUrl: String = uri

    override suspend fun connect(uri: String): Boolean {
        baseUrl = if (uri != "") uri.removeSuffix("/") else baseUrl
        return true
    }

    override suspend fun disconnect() {
        baseUrl = ""
    }

    override suspend fun addKey(key: String, value: String): Boolean {
        val response = window.fetch(
            "$baseUrl/set",
            createPostRequest(KeyValue(key, value))
        ).await() as Response

        return response.ok
    }

    override suspend fun deleteKey(key: String): Boolean {
        val response = window.fetch(
            "$baseUrl/delete",
            createPostRequest(KeyValue(key, null))
        ).await() as Response

        return response.ok
    }

    override fun getAllKeyValues(): Flow<Pair<String, String?>> = flow {
        val response = window.fetch("$baseUrl/keys").await() as Response
        if (response.ok) {
            val jsonText = response.text().await<String>()
            // Explicitly specify the type here to fix the type inference error:
            val parsed: List<KeyValue> = Json.decodeFromString<List<KeyValue>>(jsonText)
            parsed.forEach { emit(it.key to it.value) }
        }
    }

    private fun createPostRequest(body: KeyValue): RequestInit {
        val headers = Headers()
        headers.append("Content-Type", "application/json")

        val jsonBody = Json.encodeToString(body)

        return RequestInit(
            method = "POST",
            headers = headers,
            body = jsonBody as JsAny // Kotlin/WASM safe cast
        )
    }
}
