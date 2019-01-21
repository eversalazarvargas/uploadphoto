package com.example.everardo.uploadphoto

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class NewPhotoBroadcastReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.i("PROBANDO", "NewPhotoBroadcastReceiver.onReceive()")
        intent?.let {
            it.data?.let {
                val uploadPhotoScheduler = UploadPhotoScheduler(context!!)
                uploadPhotoScheduler.start(it.toString())
            }
        }
    }

}