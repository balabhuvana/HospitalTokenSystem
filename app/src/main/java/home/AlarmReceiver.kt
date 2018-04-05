package home

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import android.util.Log
import android.widget.Toast
import com.hospital.tokensystem.MyApplication


class AlarmReceiver : BroadcastReceiver() {

    private val TAG: String = AlarmReceiver::class.java.simpleName
    private var phoneStateReceiver = PhoneStateReceiver()

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "onReceive ")

        val mApplication = context.getApplicationContext() as MyApplication
        mApplication.isPhoneStateListening = true

        Handler().postDelayed(
                {
                    try {
                        mApplication.isPhoneStateListening = false
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }, 120000)
    }

    fun registerBroadcastReceiver(context: Context) {
        var phoneFilter = IntentFilter()
        phoneFilter.addAction("android.intent.action.PHONE_STATE")
        context.registerReceiver(phoneStateReceiver, IntentFilter(
                "android.intent.action.PHONE_STATE"));
        Toast.makeText(context, "Registered broadcast receiver", Toast.LENGTH_SHORT)
                .show();
    }
}
