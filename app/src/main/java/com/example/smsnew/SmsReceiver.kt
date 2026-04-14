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
            
            // Check if service is enabled
            val isEnabled = sharedPreferences.getBoolean("isEnabled", true)
            if (!isEnabled) return

            val keywordsString = sharedPreferences.getString("keywords", "") ?: ""
            val keywords = keywordsString.split(",").map { it.trim() }.filter { it.isNotEmpty() }
            val phoneNumber = sharedPreferences.getString("phoneNumber", "") ?: ""

            for (message in messages) {
                val msgBody = message.messageBody
                val originatingAddress = message.originatingAddress ?: ""
                val timestamp = System.currentTimeMillis()
                val logPrefix = "SMS from: $originatingAddress, Body: $msgBody"
                val logs = sharedPreferences.getStringSet("logs", mutableSetOf())?.toMutableSet() ?: mutableSetOf()

                // Check for keywords first
                var keywordFound: String? = null
                if (keywords.isNotEmpty()) {
                    for (keyword in keywords) {
                        if (msgBody.contains(keyword, ignoreCase = true)) {
                            keywordFound = keyword
                            break
                        }
                    }
                }

                if (keywordFound != null) {
                    // Keyword found, now check if we should skip due to loop prevention
                    var shouldSkip = false
                    if (originatingAddress.isNotEmpty() && phoneNumber.isNotEmpty()) {
                        val normalizedOriginating = originatingAddress.replace("[^0-9]".toRegex(), "")
                        val normalizedPhone = phoneNumber.replace("[^0-9]".toRegex(), "")
                        
                        // Fix: Only perform end-comparison if we actually have numbers in the sender ID
                        // This prevents alphabetic sender IDs (like AD-ICICIO-T) from triggering a skip
                        if (normalizedOriginating.isNotEmpty() && normalizedPhone.isNotEmpty()) {
                            if (normalizedOriginating.endsWith(normalizedPhone) || normalizedPhone.endsWith(normalizedOriginating)) {
                                shouldSkip = true
                            }
                        }
                    }

                    if (shouldSkip) {
                        logs.add("$timestamp|$logPrefix - Keyword '$keywordFound' found but skipped to prevent infinite loop (Sender is the forward-to number)")
                    } else if (phoneNumber.isNotEmpty()) {
                        val smsManager = context.getSystemService(SmsManager::class.java)
                        smsManager.sendTextMessage(phoneNumber, null, msgBody, null, null)
                        logs.add("$timestamp|$logPrefix - Keyword '$keywordFound' found and forwarded to $phoneNumber")

                        // Send notification
                        val notificationIntent = Intent(context, LogActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        }
                        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)

                        val builder = NotificationCompat.Builder(context, "SMS_FORWARDER_CHANNEL")
                            .setSmallIcon(R.drawable.ic_launcher_foreground)
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
                        logs.add("$timestamp|$logPrefix - Keyword '$keywordFound' found but no phone number configured")
                    }
                } else {
                    logs.add("$timestamp|$logPrefix - No matching keywords found")
                }
                
                with(sharedPreferences.edit()) {
                    putStringSet("logs", logs)
                    apply()
                }
            }
        }
    }
}
