package com.example.smsnew

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.example.smsnew.ui.theme.SmsnewTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1
                )
            }
        }

        createNotificationChannel()

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.RECEIVE_SMS
            ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.SEND_SMS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECEIVE_SMS, Manifest.permission.SEND_SMS),
                1
            )
        }

        setContent {
            SmsnewTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    SmsScreen()
                }
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "SMS Forwarder"
            val descriptionText = "Notifications for forwarded SMS messages"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("SMS_FORWARDER_CHANNEL", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmsScreen() {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("SmsPrefs", Context.MODE_PRIVATE)
    var keywordsInput by remember { mutableStateOf(sharedPreferences.getString("keywords", "") ?: "") }
    var phoneNumber by remember { mutableStateOf(sharedPreferences.getString("phoneNumber", "") ?: "") }
    var savedKeywords by remember { mutableStateOf(sharedPreferences.getString("keywords", "") ?: "")}

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Saved keywords: $savedKeywords")
        TextField(
            value = keywordsInput,
            onValueChange = { keywordsInput = it },
            label = { Text("Keywords (comma-separated)") }
        )
        TextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            label = { Text("Phone Number") }
        )
        Button(onClick = {
            with(sharedPreferences.edit()) {
                putString("keywords", keywordsInput)
                putString("phoneNumber", phoneNumber)
                apply()
            }
            savedKeywords = keywordsInput
            Toast.makeText(context, "Saved!", Toast.LENGTH_SHORT).show()
        }) {
            Text("Save")
        }
        Button(onClick = { context.startActivity(Intent(context, LogActivity::class.java)) }) {
            Text("View Logs")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SmsnewTheme {
        SmsScreen()
    }
}
