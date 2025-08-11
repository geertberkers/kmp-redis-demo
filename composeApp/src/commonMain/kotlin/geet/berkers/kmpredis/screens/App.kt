package geet.berkers.kmpredis.screens

import geet.berkers.kmpredis.interfaces.RedisClientInterface
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.ui.unit.dp
import geet.berkers.kmpredis.client
import geet.berkers.kmpredis.getPlatform
import kotlinx.coroutines.launch


@Composable
fun App(
    redisConnectionUri: String = "",
    redisClient: RedisClientInterface = client
) {
    var uri by remember { mutableStateOf(redisConnectionUri) }

    var key by remember { mutableStateOf("") }
    var value by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var connected by remember { mutableStateOf(false) }
    var isConnecting by remember { mutableStateOf(false) }
    var reloadKeysTrigger by remember { mutableStateOf(0) }
    val coroutineScope = rememberCoroutineScope()

    val client = "Redis " + getPlatform().name  + " Client"
    val reloadKeys = { reloadKeysTrigger++ }

    Column(
        modifier = Modifier.padding(16.dp).fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        Text(client, style = MaterialTheme.typography.headlineSmall)

        OutlinedTextField(
            value = uri,
            onValueChange = { uri = it },
            label = { Text("Redis URI (e.g. rediss://...)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            if (!connected) {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            isConnecting = true
                            val success = redisClient.connect(uri)
                            connected = success
                            message = if (success) "Connected to Redis" else "Failed to connect"
                            if (success) reloadKeys()
                            isConnecting = false
                        }
                    },
                    enabled = uri.isNotBlank() && !isConnecting
                ) {
                    Text("Connect")
                }

                Spacer(Modifier.width(8.dp))

                if (isConnecting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                }
            } else {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            redisClient.disconnect()
                            connected = false
                            message = "Disconnected"
                            key = ""
                            value = ""
                            reloadKeysTrigger = 0 // reset reload keys trigger
                        }
                    }
                ) {
                    Text("Disconnect")
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = key,
            onValueChange = { key = it },
            label = { Text("Key") },
            modifier = Modifier.fillMaxWidth(),
            enabled = connected
        )
        OutlinedTextField(
            value = value,
            onValueChange = { value = it },
            label = { Text("Value") },
            modifier = Modifier.fillMaxWidth(),
            enabled = connected
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = {
                    coroutineScope.launch {
                        if (connected) {
                            val added = redisClient.addKey(key, value)
                            message = if (added) {
                                reloadKeys()
                                "Added key '$key'"
                            } else "Failed to add key"
                            key = ""
                            value = ""
                            reloadKeysTrigger++ // trigger reload keys
                        } else {
                            message = "Not connected"
                        }
                    }
                },
                enabled = connected
            ) {
                Text("Add Key")
            }

            Button(
                onClick = {
                    coroutineScope.launch {
                        if (connected) {
                            val deleted = redisClient.deleteKey(key)
                            message = if (deleted) {
                                reloadKeys()
                                "Deleted key '$key'"
                            } else "Failed to delete key"
                            key = ""
                        } else {
                            message = "Not connected"
                        }
                    }
                },
                enabled = connected
            ) {
                Text("Delete Key")
            }
        }

        Spacer(Modifier.height(16.dp))
        Text(message)

        Spacer(Modifier.height(16.dp))

        if (connected) {
            AppKeys(client = redisClient, reloadTrigger = reloadKeysTrigger, uri = uri)
        }
    }
}

@Composable
fun AppKeys(client: RedisClientInterface, reloadTrigger: Int, uri: String) {
    val keyValueList = remember { mutableStateListOf<Pair<String, String?>>() }

    LaunchedEffect(reloadTrigger) {
        println("AppKeysReloading keys $reloadTrigger")
        keyValueList.clear()
        client.getAllKeyValues().collect { pair ->
            if (!keyValueList.any { it.first == pair.first }) {
                keyValueList.add(pair)
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Stored Redis Keys", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))
        if (keyValueList.isEmpty()) {
            Text("No keys found.")
        } else {
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(keyValueList) { (k, v) ->
                    Row(modifier = Modifier.padding(vertical = 4.dp)) {
                        Text("$k : ${v ?: "null"}")
                    }
                }
            }
        }
    }
}