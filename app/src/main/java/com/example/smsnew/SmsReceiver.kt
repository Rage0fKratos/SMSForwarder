package com.example.smsnew

import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.Telephony
import android.telephony.SmsManager
import android.telephony.SmsMessage
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class SmsReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            val messages: Array<SmsMessage> = Telephony.Sms.Intents.getMessagesFromIntent(intent)
            val sharedPreferences = context.getSharedPreferences("SmsPrefs", Context.MODE_PRIVATE)
            val keywordsString = sharedPreferences.getString("keywords", "") ?: ""
            val keywords = keywordsString.split(",").map { it.trim() }.filter { it.isNotEmpty() }
            val phoneNumber = sharedPreferences.getString("phoneNumber", "") ?: ""

            for (message in messages) {
                val msgBody = message.messageBody
                val log = "SMS from: ${message.originatingAddress}, Body: $msgBody"
                val logs = sharedPreferences.getStringSet("logs", mutableSetOf())?.toMutableSet() ?: mutableSetOf()

                var keywordFound: String? = null
                if (keywords.isNotEmpty() && phoneNumber.isNotEmpty()) {
                    for (keyword in keywords) {
                        if (msgBody.contains(keyword, ignoreCase = true)) {
                            keywordFound = keyword
                            break
                        }
                    }
                }

                val timestamp = System.currentTimeMillis()
                if (keywordFound != null) {
                    val smsManager = context.getSystemService(SmsManager::class.java)
                    smsManager.sendTextMessage(phoneNumber, null, msgBody, null, null)
                    logs.add("$timestamp|$log - Keyword '$keywordFound' found and forwarded to $phoneNumber")

                    // Send notification
                    val intent = Intent(context, LogActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

                    val builder = NotificationCompat.Builder(context, "SMS_FORWARDER_CHANNEL")
                        .setSmallIcon(R.drawable.ic_launcher_foreground) // Replace with your app icon
                        .setContentTitle("SMS Forwarded")
                        .setContentText("Keyword '$keywordFound' found. SMS forwarded to $phoneNumber.")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)

                    with(NotificationManagerCompat.from(context)) {
                        if (ActivityCompat.checkSelfPermission(
                                context,
                                Manifest.permission.POST_NOTIFICATIONS
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {
                            notify(System.currentTimeMillis().toInt(), builder.build())
                        }
                    }

                } else {
                    logs.add("$timestamp|$log - No matching keywords found")
                }
                with(sharedPreferences.edit()) {
                    putStringSet("logs", logs)
                    apply()
                }
            }
        }
    }
}
