package geet.berkers.kmpredis

import geet.berkers.kmpredis.interfaces.RedisClientInterface
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow

class RedisWasmClient : RedisClientInterface {

    private val store = mutableMapOf<String, String>()
    private val snapshot = MutableStateFlow(store.toMap())
    private var connected = false

    override suspend fun connect(uri: String): Boolean {
        connected = true
        return true
    }

    override suspend fun disconnect() {
        connected = false
    }

    override suspend fun addKey(key: String, value: String): Boolean {
        if (!connected) return false
        store[key] = value
        snapshot.value = store.toMap()
        return true
    }

    override suspend fun deleteKey(key: String): Boolean {
        if (!connected) return false
        val result = store.remove(key) != null
        if (result) {
            snapshot.value = store.toMap()
        }
        return result
    }

    override fun getAllKeyValues(): Flow<Pair<String, String?>> = flow {
        for ((key, value) in store) {
            emit(key to value)
        }
    }
}
