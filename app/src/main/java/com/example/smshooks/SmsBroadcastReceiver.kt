package com.example.smshooks;

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony.Sms.Intents.getMessagesFromIntent
import android.util.Log
import android.widget.Toast
import androidx.preference.PreferenceManager
import androidx.work.*

class SmsBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val appPreferences = PreferenceManager.getDefaultSharedPreferences(context)

        val appEnabled = appPreferences.getBoolean("webhookEnabled", false)
        if (!appEnabled) {
            return
        }

        // Stop here if no server URL have been set in settings
        val webHookUrl = appPreferences.getString("webhookUrl", "")
        if (webHookUrl.isNullOrEmpty()) {
            return
        }

        val smsMessages = getMessagesFromIntent(intent)
        for (message in smsMessages) {
            val postWorkRequest: WorkRequest =
                OneTimeWorkRequestBuilder<PostingWorker>()
                    .setInputData(
                        workDataOf(
                            "SMS_BODY" to message.messageBody,
                            "SMS_FROM" to message.originatingAddress,
                            "SMS_TIMESTAMP" to message.timestampMillis,
                            "WEBHOOK_URL" to webHookUrl
                        )
                    )
                    .setConstraints(
                        Constraints.Builder()
                            .setRequiredNetworkType(NetworkType.CONNECTED)
                            .build()
                    )
                    .build()
            WorkManager
                .getInstance(context)
                .enqueue(postWorkRequest)
        }

        // Log
        StringBuilder().apply {
            append("Action: ${intent.action}\n")
            append("URI: ${intent.toUri(Intent.URI_INTENT_SCHEME)}\n")
            toString().also { log ->
                Log.d(this::class.simpleName, log)
                Toast.makeText(context, log, Toast.LENGTH_LONG).show()
            }
        }
    }
}
