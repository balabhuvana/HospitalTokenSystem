package home


import android.content.IntentFilter
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import com.hospital.tokensystem.R
import kotlinx.android.synthetic.main.fragment_token.*


/**
 * A simple [Fragment] subclass.
 */
class TokenFragment : Fragment() {

    private val phoneStateReceiver = PhoneStateReceiver()
    private val userDefinedReceiver = UserDefinedBroadcastReceiver()

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_token, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerBroadcast.setOnClickListener {
            var phoneFilter=IntentFilter()
            phoneFilter.addAction("android.intent.action.PHONE_STATE")
            activity.registerReceiver(phoneStateReceiver, IntentFilter(
                    "android.intent.action.PHONE_STATE"));
            Toast.makeText(activity, "Registered broadcast receiver", Toast.LENGTH_SHORT)
                    .show();
        }
        unRegisterBroadcast.setOnClickListener {
            activity.unregisterReceiver(phoneStateReceiver);
            Toast.makeText(activity, "Un Registered broadcast receiver", Toast.LENGTH_SHORT)
                    .show();
        }
    }

}// Required empty public constructor
