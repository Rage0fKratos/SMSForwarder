package com.example.smsnew

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.smsnew.ui.theme.SmsnewTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LogActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SmsnewTheme {
                LogApp { finish() }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogApp(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            MediumTopAppBar(
                title = { Text("Activity Logs") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = MaterialTheme.colorScheme.background
        ) {
            LogScreen()
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

    if (logs.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.outline
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "No logs yet",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.outline
            )
        }
    } else {
        LazyColumn {
            items(logs) { (timestamp, log) ->
                LogItem(timestamp, log)
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    thickness = 0.5.dp,
                    color = MaterialTheme.colorScheme.outlineVariant
                )
            }
        }
    }
}

@Composable
fun LogItem(timestamp: Long, message: String) {
    val date = if (timestamp > 0) {
        SimpleDateFormat("MMM dd, HH:mm:ss", Locale.getDefault()).format(Date(timestamp))
    } else ""

    ListItem(
        headlineContent = {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        overlineContent = if (date.isNotEmpty()) {
            {
                Text(
                    text = date,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        } else null
    )
}

@Preview(showBackground = true)
@Composable
fun LogPreview() {
    SmsnewTheme {
        LogApp {}
    }
}
