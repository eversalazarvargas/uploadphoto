package com.example.everardo.uploadphoto

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.os.Build
import android.os.PersistableBundle
import java.util.concurrent.atomic.AtomicInteger
import android.provider.MediaStore
import com.example.everardo.uploadphoto.UploadPhotoJobService.Companion.IMAGE_URI
import com.example.everardo.uploadphoto.UploadPhotoJobService.Companion.SYNC_ALL_TASK_TYPE


class UploadPhotoScheduler(private val context: Context) {

    companion object {
        private val JOB_ID = AtomicInteger()
        const val PARAM_TASK_TYPE = "UploadPhotoScheduler.Task"
        private const val ADD_PENDING_PHOTOS_TASK_TYPE = -1
    }

    private var jobScheduler = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler

    fun start(uri: String? = null) {

        cancelAllTasks()

        val extras = PersistableBundle()
        extras.putInt(PARAM_TASK_TYPE, ADD_PENDING_PHOTOS_TASK_TYPE)

        val jobInfoBuilder = JobInfo.Builder(JOB_ID.getAndIncrement(), ComponentName(context, UploadPhotoJobService::class.java))
                .setBackoffCriteria(30000, JobInfo.BACKOFF_POLICY_EXPONENTIAL) // 30 sec initial backoff
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setRequiresCharging(false)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            jobInfoBuilder.addTriggerContentUri(JobInfo.TriggerContentUri(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    JobInfo.TriggerContentUri.FLAG_NOTIFY_FOR_DESCENDANTS))
        }

        uri?.let {
            extras.putString(IMAGE_URI, uri)
            jobInfoBuilder.setMinimumLatency(10 * 1000)
        }

        jobInfoBuilder.setExtras(extras)

        jobScheduler.schedule(jobInfoBuilder.build())

    }

    fun syncAllImmediate() {
        val extras = PersistableBundle()
        extras.putInt(PARAM_TASK_TYPE, SYNC_ALL_TASK_TYPE)

        val jobInfoBuilder = JobInfo.Builder(JOB_ID.getAndIncrement(), ComponentName(context, UploadPhotoJobService::class.java))
                .setBackoffCriteria(30000, JobInfo.BACKOFF_POLICY_EXPONENTIAL) // 30 sec initial backoff
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setMinimumLatency(2 * 60 * 1000)
                .setRequiresCharging(false)
                .setExtras(extras)

        jobScheduler.schedule(jobInfoBuilder.build())
    }

    fun syncAllPeriodic() {
        val extras = PersistableBundle()
        extras.putInt(PARAM_TASK_TYPE, SYNC_ALL_TASK_TYPE)

        val jobInfoBuilder = JobInfo.Builder(JOB_ID.getAndIncrement(), ComponentName(context, UploadPhotoJobService::class.java))
                .setBackoffCriteria(30000, JobInfo.BACKOFF_POLICY_EXPONENTIAL) // 30 sec initial backoff
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPeriodic(15 * 60 * 1000)
                .setPersisted(true)
                .setRequiresCharging(false)
                .setExtras(extras)

        jobScheduler.schedule(jobInfoBuilder.build())
    }

    fun cancelAllTasks() {
        jobScheduler.allPendingJobs
                .filter { it.extras != null && it.extras.getInt(PARAM_TASK_TYPE, 0) == ADD_PENDING_PHOTOS_TASK_TYPE }
                .forEach { jobScheduler.cancel(it.id) }
    }

}
