package com.example.everardo.uploadphoto

import android.app.Application
import android.util.Log


/**
 * @author everardo.salazar on 1/18/19
 */
class MyApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        Log.i("PROBANDO", "MyApplication.onCreate()")

        val uploadPhotoScheduler = UploadPhotoScheduler(this)
        uploadPhotoScheduler.start()
        uploadPhotoScheduler.syncAllPeriodic()

    }
}