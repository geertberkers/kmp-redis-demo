package geet.berkers.kmpredis

import geet.berkers.kmpredis.interfaces.RedisClientInterface
import java.util.Properties

actual val client: RedisClientInterface by lazy {
    LettuceRedisClient()
}

actual fun loadRedisUri(): String? {
    return try {
        val props = Properties()
        val stream = Thread.currentThread().contextClassLoader.getResourceAsStream("private_redis.properties")
            ?: return null
        props.load(stream)
        props.getProperty("redis.uri")
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

actual fun loadRedisUri(context: Any): String? = loadRedisUri() // fallback
