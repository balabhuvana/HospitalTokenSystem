package home

import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import android.util.Log
import android.widget.Toast


/**
 * JobService to be scheduled by the JobScheduler.
 * start another service
 */
class TestJobService : JobService() {

    private var TAG: String = TestJobService::class.java.simpleName
    private var phoneStateReceiver = PhoneStateReceiver()

    override fun onStartJob(params: JobParameters): Boolean {// Do some work here
        Log.d(TAG, "onStartJob - ")
        registerBroadcastReceiver()

        Handler().postDelayed(
                {
                    try {
                        if (phoneStateReceiver != null)
                            Log.d(TAG, "unregisterReceiver")
                        unregisterReceiver(phoneStateReceiver)
                        jobFinished(params, false)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }, 60000)

        return true // Answers the question: "Is there still work going on?"
    }

    override fun onStopJob(params: JobParameters): Boolean {
        return true
    }

    companion object {
        private val TAG = "SyncService"
    }

    fun registerBroadcastReceiver() {
        var phoneFilter = IntentFilter()
        phoneFilter.addAction("android.intent.action.PHONE_STATE")
        registerReceiver(phoneStateReceiver, IntentFilter(
                "android.intent.action.PHONE_STATE"));
        Toast.makeText(applicationContext, "Registered broadcast receiver", Toast.LENGTH_SHORT)
                .show();
    }

}