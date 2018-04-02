package home


import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.firebase.jobdispatcher.*
import com.hospital.tokensystem.R
import kotlinx.android.synthetic.main.fragment_token.*
import org.joda.time.DateTime
import org.joda.time.Duration
import org.joda.time.LocalDateTime
import org.joda.time.Minutes
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


/**
 * A simple [Fragment] subclass.
 */
class TokenFragment : Fragment() {

    private var TAG: String = TokenFragment::class.java.simpleName
    private val phoneStateReceiver = PhoneStateReceiver()
    private var isReceiverListening = false
    var mHandler: Handler? = null
    private var db: HospitalityDBHelper? = null
    private var dispatcher: FirebaseJobDispatcher? = null
    private var timePickerDialog: TimePickerDialog? = null
    private var datePickerDialog: DatePickerDialog? = null
    private var calendar: Calendar? = null
    private var futureDateTime: JobDateTime? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_token, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = HospitalityDBHelper(context)
        dispatcher = FirebaseJobDispatcher(GooglePlayDriver(context))
        calendar = Calendar.getInstance()
        futureDateTime = JobDateTime()


        btnSetTime.setOnClickListener {
            var c = Calendar.getInstance();
            var mHour = c.get(Calendar.HOUR_OF_DAY);
            var mMinute = c.get(Calendar.MINUTE);

            timePickerDialog = TimePickerDialog(getContext(),
                    TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                        futureDateTime?.hourString = hourOfDay.toString()
                        futureDateTime?.minuteString = minute.toString()
                        tvTime.setText(hourOfDay.toString() + ":" + minute)

                    }, mHour, mMinute, false)
            timePickerDialog?.show()

        }

        btnSetDate.setOnClickListener {
            val c = Calendar.getInstance()
            var mYear = c.get(Calendar.YEAR)
            var mMonth = c.get(Calendar.MONTH)
            var mDay = c.get(Calendar.DAY_OF_MONTH)


            datePickerDialog = DatePickerDialog(activity,
                    DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                        tvDate.setText(dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year)
                        futureDateTime?.dateString = dayOfMonth.toString()
                        futureDateTime?.monthString = (monthOfYear + 1).toString()
                        futureDateTime?.yearString = year.toString()

                    }, mYear, mMonth, mDay)
            datePickerDialog?.show()
        }

        btnAddAppoinment.setOnClickListener {
            var jobModel = JobModel(tvTime.text.toString(), tvDate.text.toString(), "No")

            db?.insertJob(jobModel)
        }

        btnShowAllJobs.setOnClickListener {
            var alJobModel: ArrayList<JobModel> = db!!.readJob()

            for (item in alJobModel) {
                Log.d(TAG, "" + item.jobId)
                Log.d(TAG, "" + item.jobDate)
                Log.d(TAG, "" + item.isScheduled)
            }
        }

        btnDeleteJob.setOnClickListener {
            db?.deleteJob()
        }

        btnCreateJob.setOnClickListener {
            getDateAndTimeObject()
            //createFireBaseDispatcher("my-first-job", 0, calendar?.get(Calendar.SECOND)!!.plus(60).toLong())
            //createFireBaseDispatcher("my-second-job", 180, calendar?.get(Calendar.SECOND)!!.plus(300).toLong())
            //createFireBaseDispatcher("my-first-job", 0, 60)
            //createFireBaseDispatcher("my-second-job", 300, 480)
        }

    }

    fun milliSecondsCalculation(): Long? {
        return calendar?.getTimeInMillis()
    }

    fun createFireBaseDispatcher(jobName: String, currentTime: Int, startTime: Long?) {

        val myExtrasBundle = Bundle()
        myExtrasBundle.putString("job_name", jobName)

        val myJob = dispatcher?.newJobBuilder()
                // the JobService that will be called
                ?.setService(MyJobService::class.java)
                // uniquely identifies the job
                ?.setTag(jobName)
                // don't persist past a device reboot
                ?.setLifetime(Lifetime.UNTIL_NEXT_BOOT)
                // start between 0 and 60 seconds from now
                ?.setTrigger(Trigger.executionWindow(currentTime, startTime!!.toInt()))
                // don't overwrite an existing job with the same tag
                ?.setReplaceCurrent(false)
                // retry with exponential backoff
                ?.setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                // constraints that need to be satisfied for the job to run
                ?.setExtras(myExtrasBundle)
                ?.build()

        dispatcher?.mustSchedule(myJob)

    }

    fun createFireBaseDispatcherTwo(jobName: String, currentTime: Int, startTime: Int) {

        val myExtrasBundle = Bundle()
        myExtrasBundle.putString("job_name", jobName)

        val myJob = dispatcher?.newJobBuilder()
                // the JobService that will be called
                ?.setService(MyJobService::class.java)
                // uniquely identifies the job
                ?.setTag(jobName)
                // don't persist past a device reboot
                ?.setLifetime(Lifetime.UNTIL_NEXT_BOOT)
                // start between 0 and 60 seconds from now
                ?.setTrigger(Trigger.executionWindow(currentTime, startTime))
                // don't overwrite an existing job with the same tag
                ?.setReplaceCurrent(false)
                // retry with exponential backoff
                ?.setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                // constraints that need to be satisfied for the job to run
                ?.setExtras(myExtrasBundle)
                ?.build()

        dispatcher?.mustSchedule(myJob)

    }

    fun scheduleAlarmHandler() {
        mHandler = Handler()
        mHandler?.apply {
            postDelayed(mRunnable, 1000)
        }
    }

    fun registerBroadcastReceiver() {
        isReceiverListening = true
        btnSetTime.isEnabled = false
        //btnCancelTime.isEnabled = true
        var phoneFilter = IntentFilter()
        phoneFilter.addAction("android.intent.action.PHONE_STATE")
        activity.registerReceiver(phoneStateReceiver, IntentFilter(
                "android.intent.action.PHONE_STATE"));
        Toast.makeText(activity, "Registered broadcast receiver", Toast.LENGTH_SHORT)
                .show();
    }

    fun unRegisterBroadCastReceiver() {
        isReceiverListening = false
        //btnCancelTime.isEnabled = false
        btnSetTime.isEnabled = true
        activity.unregisterReceiver(phoneStateReceiver);
        Toast.makeText(activity, "Un Registered broadcast receiver", Toast.LENGTH_SHORT)
                .show();
        mHandler?.removeCallbacks(mRunnable)
    }

    private val mRunnable = object : Runnable {

        override fun run() {
            Log.e("Handlers", "Calls")
            /** Do something  */
            if (!isReceiverListening) {
                Log.e("Handlers", "mRunnable - inside - if")
                registerBroadcastReceiver()
            } else {
                Log.e("Handlers", "mRunnable - inside - else")
            }
            mHandler?.postDelayed(this, 24 * 60 * 60)
        }
    }

    private fun getDateAndTimeObject(): Long {
        var currentDate = DateTime()
        Log.d(TAG, "CurrentDate - " + currentDate)

        var featureDate = DateTime(futureDateTime?.yearString?.toInt()!!, futureDateTime?.monthString?.toInt()!!,
                futureDateTime?.dateString?.toInt()!!, futureDateTime?.hourString?.toInt()!!, futureDateTime?.minuteString?.toInt()!!)

        Log.d(TAG, "featureDate " + featureDate)

        Log.d(TAG, "diff  - " + Minutes.minutesBetween(currentDate, featureDate).getMinutes())

        return Minutes.minutesBetween(currentDate, featureDate).getMinutes().toLong()
    }


}// Required empty public constructor
