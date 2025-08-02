import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

class MockRedisClient : RedisClientInterface {
    private val data = mutableMapOf<String, String>()
    private var connected = false
    override suspend fun disconnect() {
        connected = false
        data.clear()
    }

    override suspend fun connect(host: String): Boolean {
        connected = true // simulate success
        return connected
    }

    override suspend fun addKey(key: String, value: String): Boolean {
        if (!connected) return false
        data[key] = value
        return true
    }

    override suspend fun deleteKey(key: String): Boolean {
        if (!connected) return false
        return data.remove(key) != null
    }

    override fun getAllKeyValues(): Flow<Pair<String, String?>> {
        return emptyFlow()
    }
}
