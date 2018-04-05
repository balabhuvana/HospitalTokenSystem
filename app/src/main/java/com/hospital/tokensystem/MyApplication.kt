package com.hospital.tokensystem

import android.app.Application
import android.content.res.Configuration


/**
 * Created by bala on 5/4/18.
 */
class MyApplication : Application() {

    var singleton: MyApplication? = null
    var isPhoneStateListening: Boolean? = false

    fun getInstance(): MyApplication? {
        return singleton
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }

    override fun onCreate() {
        super.onCreate()
        singleton = this;
    }

    override fun onLowMemory() {
        super.onLowMemory()
    }

    override fun onTerminate() {
        super.onTerminate()
    }

}