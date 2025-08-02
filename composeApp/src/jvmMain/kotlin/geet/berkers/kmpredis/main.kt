package geet.berkers.kmpredis

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application


fun main() = application {
    val client = LettuceRedisClient()
    Window(onCloseRequest = ::exitApplication, title = "Redis Client") {
        App(client) // This should be your main @Composable function
    }
}