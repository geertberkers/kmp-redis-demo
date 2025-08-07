package geet.berkers.kmpredis

import geet.berkers.kmpredis.interfaces.RedisClientInterface

actual fun loadRedisUri(): String? = "rediss://red-d270om6uk2gs73cn7b90:iRgdfXaxQpc330NOnY5X33zyhm5rDAkw@frankfurt-keyvalue.render.com:6379"

actual fun loadRedisUri(context: Any): String? = loadRedisUri()

actual val client: RedisClientInterface by lazy {
    WasmRedisClientHttp()
}
