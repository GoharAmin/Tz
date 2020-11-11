package com.gohar_amin.tz.utils

import android.app.Activity
import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import aybrowser.ultra.taxibookingonline.utils.InternetConnectivity


class MyApplication : Application(), ActivityLifecycleCallbacks {
     val CHANNEL_ID="gigOrderChannelId"
    override fun onCreate() {
        context = this
        isConnected = InternetConnectivity.isConnected(this)
        Log.e("MyApplication", "" + InternetConnectivity.isConnected(this))
        super.onCreate()
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            val notificationChannel = NotificationChannel(CHANNEL_ID, "Gig Order", NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.enableLights(true)
            notificationChannel.enableVibration(true)
            notificationChannel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        isConnected = InternetConnectivity.isConnected(this)
        Log.e("MyApplication", "" + InternetConnectivity.isConnected(this))
    }

    override fun onActivityStarted(activity: Activity) {}
    override fun onActivityResumed(activity: Activity) {
        isConnected = InternetConnectivity.isConnected(this)
        Log.e("MyApplication", "" + InternetConnectivity.isConnected(this))
    }

    override fun onActivityPaused(activity: Activity) {
        isConnected = InternetConnectivity.isConnected(this)
        Log.e("MyApplication", "" + InternetConnectivity.isConnected(this))
    }

    override fun onActivityStopped(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
    override fun onActivityDestroyed(activity: Activity) {
        isConnected = InternetConnectivity.isConnected(this)
        Log.e("MyApplication", "" + InternetConnectivity.isConnected(this))
    }

    companion object {
        var context: Context? = null
        var isConnected = false
    }
}