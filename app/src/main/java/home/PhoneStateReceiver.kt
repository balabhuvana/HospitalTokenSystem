package home

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsManager
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.Toast


import com.android.internal.telephony.ITelephony
import com.hospital.tokensystem.MyApplication

import java.lang.reflect.Method

class PhoneStateReceiver : BroadcastReceiver() {
    private val TAG = PhoneStateReceiver::class.java.simpleName
    private var myContext: Context? = null

    override fun onReceive(context: Context, intent: Intent) {
        try {

            val mApplication = context.getApplicationContext() as MyApplication

            Log.d(TAG, "" + mApplication.isPhoneStateListening)
            if (mApplication.isPhoneStateListening == true) {
                Log.d(TAG, "onReceive - is triggering ")
                myContext = context
                val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
                val incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)

                Log.d(TAG, "incomingNumber - " + incomingNumber)

                if (state == TelephonyManager.EXTRA_STATE_RINGING) {
                    Toast.makeText(context, "Ringing State Number is -", Toast.LENGTH_SHORT).show()
                    endCall(context)
                }
            } else if (mApplication.isPhoneStateListening == false) {
                Log.d(TAG, "onReceive - is normal ")
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun endCall(context: Context) {
        try {
            val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            val clazz = Class.forName(telephonyManager.javaClass.name)
            val method = clazz.getDeclaredMethod("getITelephony")
            method.isAccessible = true
            val telephonyService = method.invoke(telephonyManager) as ITelephony
            telephonyService.silenceRinger()
            telephonyService.endCall()

            //sendSMS(incomingNumber,"Your token no is ");

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun sendSMS(phoneNo: String, msg: String) {
        try {
            Log.d(TAG, "")
            val smsManager = SmsManager.getDefault()
            smsManager.sendTextMessage(phoneNo, "", msg, null, null)
            Toast.makeText(myContext, "Message Sent",
                    Toast.LENGTH_LONG).show()
        } catch (ex: Exception) {
            Toast.makeText(myContext, ex.message.toString(),
                    Toast.LENGTH_LONG).show()
            ex.printStackTrace()
        }

    }

}
