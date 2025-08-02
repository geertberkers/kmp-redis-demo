package geet.berkers.kmpredis

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        System.setProperty("java.net.preferIPv4Stack", "true")
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val uri = loadRedisUri(this) ?: ""
        setContent {
            App(uri)
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    MaterialTheme {
        App()
    }
}