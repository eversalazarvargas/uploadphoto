package com.example.everardo.uploadphoto

import android.app.job.JobParameters
import android.app.job.JobService
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import android.util.Log
import com.example.everardo.uploadphoto.UploadPhotoScheduler.Companion.PARAM_TASK_TYPE

class UploadPhotoJobService: JobService() {

    companion object {
        private const val MSG_ADD_PENDING_PHOTO_TASK = 1
        private const val MSG_SYNC_ALL_TASK = 3
        const val IMAGE_URI = "ImageUri"
        const val SYNC_ALL_TASK_TYPE = 2
    }

    private lateinit var handler: Handler
    private lateinit var uploadPhotoScheduler: UploadPhotoScheduler

    override fun onCreate() {
        super.onCreate()

        uploadPhotoScheduler = UploadPhotoScheduler(applicationContext)
        val handlerThread = HandlerThread("UploadPhotoServiceHandler")
        handlerThread.start()

        handler = object : Handler(handlerThread.looper) {
            override fun handleMessage(msg: Message?) {
                msg?.let {
                    if (msg.what == MSG_ADD_PENDING_PHOTO_TASK) {
                        (msg.obj as? JobParameters)?.let {
                            startAddPendingPhotosTask(it)
                        }
                    } else {
                        (msg.obj as? JobParameters)?.let { syncAll(it) }
                    }
                }
            }
        }
    }

    private fun syncAll(params: JobParameters) {
        val uploadPhotoTask = UploadPhotoTask(this)
        uploadPhotoTask.syncPhotos()

        jobFinished(params, false)
    }

    private fun startAddPendingPhotosTask(params: JobParameters) {

        val uploadPhotoTask = UploadPhotoTask(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (params.triggeredContentAuthorities != null) {
                params.triggeredContentUris?.let {
                    uploadPhotoTask.addPendingPhotos(it.asList())
                }
            }
        } else if (params.extras.containsKey(IMAGE_URI)) {
            uploadPhotoTask.addPendingPhotos(listOf(Uri.parse(params.extras.getString(IMAGE_URI))))
        }

        uploadPhotoScheduler.syncAllImmediate()

        jobFinished(params, false)
    }

    override fun onStartJob(params: JobParameters?): Boolean {
        Log.i("PROBANDO", "onStartJob")

        if (params != null && params.extras.getInt(PARAM_TASK_TYPE, 0) == SYNC_ALL_TASK_TYPE) {
            handler.obtainMessage(MSG_SYNC_ALL_TASK, params).sendToTarget()
        } else {
            handler.obtainMessage(MSG_ADD_PENDING_PHOTO_TASK, params).sendToTarget()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && params != null && params.extras.getInt(PARAM_TASK_TYPE, 0) != SYNC_ALL_TASK_TYPE) {
            // when using JobInfo.Builder.addTriggerContentUri() we need to trigger again job
            // to continue monitoring changes
            uploadPhotoScheduler.start()
        }


        return true
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        return true
    }
}