package home;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;


import com.android.internal.telephony.ITelephony;

import java.lang.reflect.Method;

public class PhoneStateReceiver extends BroadcastReceiver {
    private String TAG = PhoneStateReceiver.class.getSimpleName();
    private Context myContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        try {

            myContext = context;
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);

            Log.d(TAG, "incomingNumber - " + incomingNumber);

            if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                Toast.makeText(context, "Ringing State Number is -", Toast.LENGTH_SHORT).show();
            }
            if ((state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK))) {
                Toast.makeText(context, "Received State", Toast.LENGTH_SHORT).show();
            }
            if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                Toast.makeText(context, "Idle State", Toast.LENGTH_SHORT).show();
            }

            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            try {
                TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                Class clazz = Class.forName(telephonyManager.getClass().getName());
                Method method = clazz.getDeclaredMethod("getITelephony");
                method.setAccessible(true);
                ITelephony telephonyService = (ITelephony) method.invoke(telephonyManager);
                telephonyService.silenceRinger();
                telephonyService.endCall();

                //sendSMS(incomingNumber,"Your token no is ");

            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void sendSMS(String phoneNo, String msg) {
        try {
            Log.d(TAG,"");
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, "", msg, null, null);
            Toast.makeText(myContext, "Message Sent",
                    Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Toast.makeText(myContext, ex.getMessage().toString(),
                    Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }

}
