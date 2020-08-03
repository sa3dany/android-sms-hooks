package com.example.smshooks

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters


class PostingWorker(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {
    override fun doWork(): Result {
        val smsBody = inputData.getString("SMS_BODY") ?: return Result.failure()
        val smsFrom = inputData.getString("SMS_FROM") ?: return Result.failure()
        val smsTime = inputData.getLong("SMS_TIME", 0)

        // Post the SMS here

        // Indicate whether the work finished successfully with the Result
        return Result.success()
//        Result.retry(): The work failed and should be tried at another time according to its retry policy.

    }
}

