package com.gohar_amin.tz.utils

import android.app.Activity
import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import android.content.Context
import android.os.Bundle
import android.util.Log
import aybrowser.ultra.taxibookingonline.utils.InternetConnectivity

class MyApplication : Application(), ActivityLifecycleCallbacks {
    override fun onCreate() {
        context = this
        isConnected = InternetConnectivity.isConnected(this)
        Log.e("MyApplication", "" + InternetConnectivity.isConnected(this))
        super.onCreate()
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