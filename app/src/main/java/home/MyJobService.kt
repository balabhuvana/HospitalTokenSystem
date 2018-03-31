package home

import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import android.util.Log
import android.widget.Toast
import com.firebase.jobdispatcher.JobParameters
import com.firebase.jobdispatcher.JobService
import com.hospital.tokensystem.LoginActivity
import kotlinx.android.synthetic.main.fragment_token.*

class MyJobService : JobService() {
    private var TAG: String = MyJobService::class.java.simpleName
    private var phoneStateReceiver = PhoneStateReceiver()
    override fun onStartJob(job: JobParameters): Boolean {
        // Do some work here
        Log.d(TAG, "onStartJob - " + job.extras?.getString("job_name"))
        registerBroadcastReceiver()

        Handler().postDelayed(
                {
                    try {
                        if (phoneStateReceiver != null)
                            Log.d(TAG, "unregisterReceiver")
                        unregisterReceiver(phoneStateReceiver)
                        jobFinished(job, false)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }, 60000)

        return true // Answers the question: "Is there still work going on?"
    }

    override fun onStopJob(job: JobParameters): Boolean {
        return false // Answers the question: "Should this job be retried?"
    }

    override fun onDestroy() {

        try {
            if (phoneStateReceiver != null)
                Log.d(TAG, "unregisterReceiver")
            unregisterReceiver(phoneStateReceiver)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        super.onDestroy()
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