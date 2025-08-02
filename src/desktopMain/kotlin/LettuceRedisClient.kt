import io.lettuce.core.ExperimentalLettuceCoroutinesApi
import io.lettuce.core.RedisClient
import io.lettuce.core.api.StatefulRedisConnection
import io.lettuce.core.api.coroutines
import io.lettuce.core.api.coroutines.RedisCoroutinesCommands
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

@OptIn(ExperimentalLettuceCoroutinesApi::class)
class LettuceRedisClient : RedisClientInterface {

    private var client: RedisClient? = null
    private var connection: StatefulRedisConnection<String, String>? = null
    private var commands: RedisCoroutinesCommands<String, String>? = null

    override suspend fun connect(uri: String): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            client = RedisClient.create(uri)
            connection = client?.connect()
            commands = connection?.coroutines()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override suspend fun addKey(key: String, value: String): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            commands?.set(key, value)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override suspend fun deleteKey(key: String): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            val deleted = commands?.del(key) ?: 0
            deleted > 0
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    @OptIn(ExperimentalLettuceCoroutinesApi::class)
    override fun getAllKeyValues(): Flow<Pair<String, String?>> = flow {
        val syncCommands = connection?.sync()  // get synchronous commands to call `keys`
        val keys: List<String> =
            syncCommands?.keys("*").orEmpty()  // blocking call is OK in flow block
        val asyncCommands = connection?.coroutines()      // back to coroutines for value fetching

        for (key in keys) {
            val value = asyncCommands?.get(key)
            emit(key to value)
        }
    }


    override suspend fun disconnect() {
        try {
            connection?.close()
            client?.shutdown()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            connection = null
            client = null
        }
    }
}