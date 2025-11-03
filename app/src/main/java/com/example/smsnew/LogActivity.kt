package com.example.smsnew

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.smsnew.ui.theme.SmsnewTheme

class LogActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SmsnewTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    LogScreen()
                }
            }
        }
    }
}

@Composable
fun LogScreen() {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("SmsPrefs", Context.MODE_PRIVATE)
    val logs = sharedPreferences.getStringSet("logs", emptySet())?.toList()?.map {
        val parts = it.split("|")
        val timestamp = parts.getOrNull(0)?.toLongOrNull() ?: 0L
        val logMessage = parts.getOrNull(1) ?: it
        timestamp to logMessage
    }?.sortedByDescending { it.first } ?: emptyList()

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Logs", style = MaterialTheme.typography.headlineMedium)
        LazyColumn {
            items(logs) { (_, log) ->
                Text(log)
                Divider()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LogPreview() {
    SmsnewTheme {
        LogScreen()
    }
}
