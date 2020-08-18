package com.tanglycohort.smshooks

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject


class PostingWorker(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {
    companion object {
        private val MEDIA_TYPE_JSON = "application/json; charset=utf-8".toMediaType()
    }

    private val client = OkHttpClient()

    override fun doWork(): Result {
        val smsBody = inputData.getString("SMS_BODY") ?: return Result.failure()
        val smsFrom = inputData.getString("SMS_FROM") ?: return Result.failure()
        val smsTimestamp = inputData.getLong("SMS_TIMESTAMP", 0)
        val webhookUrl = inputData.getString("WEBHOOK_URL") ?: return Result.failure()

        // Serialize to JSON
        val jsonBody = JSONObject()
            .put("body", smsBody)
            .put("from", smsFrom)
            .put("timestamp", smsTimestamp)
            .toString()

        val request = Request.Builder()
            .url(webhookUrl)
            .post(jsonBody.toRequestBody(MEDIA_TYPE_JSON))
            .build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                // Log failure
                StringBuilder().apply {
                    append("Action: WEBHOOK\n")
                    append("URI: $webhookUrl\n")
                    append("Response Code: $response\n")
                    toString().also { log ->
                        Log.d(this::class.simpleName, log)
                    }
                }
                return Result.failure()
            }
        }

        return Result.success()
    }
}

