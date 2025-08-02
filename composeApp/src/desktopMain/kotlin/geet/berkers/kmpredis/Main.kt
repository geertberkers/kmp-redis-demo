package geet.berkers.kmpredis

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application


fun main() = application {
    val uri = loadRedisUri(this) ?: ""

    Window(onCloseRequest = ::exitApplication, title = "Redis Client") {
        App(redisConnectionUri = uri) // This should be your main @Composable function
    }
}