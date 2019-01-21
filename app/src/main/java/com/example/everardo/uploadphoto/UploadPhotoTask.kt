package com.example.everardo.uploadphoto

import android.content.Context
import android.net.Uri
import android.support.annotation.WorkerThread
import android.util.Log

class UploadPhotoTask(private val context: Context) {

    @WorkerThread
    fun syncPhotos() {
        Log.i("PROBANDO", "sync photos")
    }

    @WorkerThread
    fun addPendingPhotos(uris: List<Uri>) {
        Log.i("PROBANDO", "addPendingPhotos uris = $uris")
    }
}