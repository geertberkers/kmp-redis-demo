package geet.berkers.kmpredis

import android.content.Context
import geet.berkers.kmpredis.interfaces.RedisClientInterface
import java.util.Properties

actual val client: RedisClientInterface by lazy {
    LettuceRedisClient()
}

actual fun loadRedisUri(): String? = null // Not used

actual fun loadRedisUri(context: Any): String? {
    val androidContext = context as? Context ?: return null
    return try {
        val props = Properties()
        val stream = androidContext.assets.open("private_redis.properties")
        props.load(stream)
        val uri = props.getProperty("redis.uri")
        uri
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}