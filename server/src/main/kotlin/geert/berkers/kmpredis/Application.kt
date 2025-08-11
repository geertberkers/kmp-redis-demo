package geert.berkers.kmpredis

import geet.berkers.kmpredis.SERVER_PORT
import geet.berkers.kmpredis.impl.Greeting
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import java.util.concurrent.ConcurrentHashMap
import io.ktor.server.plugins.cors.routing.*
import io.ktor.http.*

@Serializable
data class KeyValue(val key: String, val value: String?)

val mockRedis = ConcurrentHashMap<String, String>()
fun main() {
    embeddedServer(Netty, port = SERVER_PORT, host = "0.0.0.0") {
        install(ContentNegotiation) {
            json()
        }

        install(CORS) {
            anyHost() // ⚠️ Allow from all origins (OK for local dev)
            allowMethod(HttpMethod.Get)
            allowMethod(HttpMethod.Post)
            allowHeader(HttpHeaders.ContentType)
        }
        routing {
            post("/set") {
                val kv = call.receive<KeyValue>()
                if (kv.value != null) {
                    mockRedis[kv.key] = kv.value
                    call.respond(mapOf("status" to "OK"))
                } else {
                    call.respond(mapOf("status" to "Value null"))
                }
            }
            post("/delete") {
                val kv = call.receive<KeyValue>()
                mockRedis.remove(kv.key)
                call.respond(mapOf("status" to "Deleted"))
            }
            get("/keys") {
                val result = mockRedis.map { KeyValue(it.key, it.value) }
                call.respond(result)
            }
            get("/ping") {
                call.respondText("pong", ContentType.Text.Plain)
            }
        }
    }.start(wait = true)
//    embeddedServer(Netty, port = SERVER_PORT, host = "0.0.0.0", module = Application::module)
//        .start(wait = true)
}

fun Application.module() {
    routing {
        get("/") {
            call.respondText("Ktor: ${Greeting().greet()}")
        }
    }
}

//fun Application.module() {
//    val redis = Jedis("localhost", 6379)
//
//    routing {
//        post("/set") {
//            val kv = call.receive<KeyValue>()
//            redis.set(kv.key, kv.value)
//            call.respond(HttpStatusCode.OK)
//        }
//
//        post("/delete") {
//            val kv = call.receive<KeyValue>()
//            redis.del(kv.key)
//            call.respond(HttpStatusCode.OK)
//        }
//
//        get("/keys") {
//            val keys = redis.keys("*")
//            val result = keys.map { key -> KeyValue(key, redis.get(key)) }
//            call.respond(result)
//        }
//    }
//}
