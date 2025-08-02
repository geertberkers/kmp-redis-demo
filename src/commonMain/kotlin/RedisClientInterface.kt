import kotlinx.coroutines.flow.Flow

interface RedisClientInterface {
    suspend fun disconnect()
    suspend fun connect(uri: String): Boolean
    suspend fun addKey(key: String, value: String): Boolean
    suspend fun deleteKey(key: String): Boolean
    fun getAllKeyValues(): Flow<Pair<String, String?>>
}