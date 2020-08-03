package com.example.smshooks;

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony.Sms.Intents.getMessagesFromIntent
import android.telephony.SmsMessage
import android.util.Log
import android.widget.Toast
import androidx.work.*

class SmsBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val smsMessages = getMessagesFromIntent(intent)

        for (message in smsMessages) {
            val postWorkRequest: WorkRequest =
                OneTimeWorkRequestBuilder<PostingWorker>()
                    .setInputData(
                        workDataOf(
                            "SMS_BODY" to message.messageBody,
                            "SMS_FROM" to message.originatingAddress,
                            "SMS_TIME" to message.timestampMillis
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

        StringBuilder().apply {
            append("Action: ${intent.action}\n")
            append("URI: ${intent.toUri(Intent.URI_INTENT_SCHEME)}\n")
            toString().also { log ->
                Log.d(SmsBroadcastReceiver::class.simpleName, log)
                Toast.makeText(context, log, Toast.LENGTH_LONG).show()
            }
        }
    }
}
