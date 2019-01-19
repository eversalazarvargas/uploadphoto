package com.example.everardo.uploadphoto

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log


/**
 * @author everardo.salazar on 1/18/19
 */
class BootBroadcastReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        StringBuilder().apply {
            append("Action: ${intent?.action}\n")
            append("URI: ${intent?.toUri(Intent.URI_INTENT_SCHEME)}\n")
            toString().also { log ->
                Log.d("PROBANDO", log)
            }
        }

        val uploadPhotoScheduler = UploadPhotoScheduler()
        uploadPhotoScheduler.start()
    }
}