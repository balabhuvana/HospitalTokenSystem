package home


import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.IntentFilter
import android.os.Bundle
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
import org.joda.time.Minutes
import java.util.*
import android.app.PendingIntent
import android.app.AlarmManager
import android.os.SystemClock
import android.content.Intent


/**
 * A simple [Fragment] subclass.
 */
class TokenFragment : Fragment() {

    private var TAG: String = TokenFragment::class.java.simpleName
    private val phoneStateReceiver = PhoneStateReceiver()
    private var db: HospitalityDBHelper? = null
    private var dispatcher: FirebaseJobDispatcher? = null
    private var timePickerDialog: TimePickerDialog? = null
    private var datePickerDialog: DatePickerDialog? = null
    private var calendar: Calendar? = null
    private var futureDateTime: JobDateTime? = null
    private var startSecond = 0
    private var jobScheduler: JobScheduler? = null
    private var serviceComponent: ComponentName? = null
    private var alarmMgr: AlarmManager? = null
    private var alarmIntent: PendingIntent? = null

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
        jobScheduler = context.getSystemService(JobScheduler::class.java)
        serviceComponent = ComponentName(context, TestJobService::class.java)


        btnSetTime.setOnClickListener {
            val c = Calendar.getInstance()
            val mHour = c.get(Calendar.HOUR_OF_DAY)
            val mMinute = c.get(Calendar.MINUTE)

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
            val mYear = c.get(Calendar.YEAR)
            val mMonth = c.get(Calendar.MONTH)
            val mDay = c.get(Calendar.DAY_OF_MONTH)


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
            db?.insertJob(futureDateTime!!)
        }

        btnShowAllJobs.setOnClickListener {
            val alJobModel: ArrayList<JobDateTime> = db!!.readJob()

            for (item in alJobModel) {
                Log.d(TAG, "" + item.jobId)
                Log.d(TAG, "" + item.dateString)
                Log.d(TAG, "" + item.monthString)
                Log.d(TAG, "" + item.yearString)
                Log.d(TAG, "" + item.hourString)
                Log.d(TAG, "" + item.minuteString)
                Log.d(TAG, "" + item.scheduleString)
            }
        }

        btnDeleteJob.setOnClickListener {
            db?.deleteJob()
        }

        btnCreateJob.setOnClickListener {

            scheduleUsingAlarmManager()

            // Using normal job schudule
           /* val alJobModel: ArrayList<JobDateTime> = db!!.readJob()
            for (item in alJobModel) {
                Log.d(TAG, "btnCreateJob - " + getDateAndTimeObject(item))
                Log.d(TAG, "btnCreateJob - " + getDateAndTimeObject(item) * 60)
                scheduleJob(item.jobId!!.toInt(), getDateAndTimeObject(item) * 60)
            }*/

            /*// firebase
            var alJobModel: ArrayList<JobDateTime> = db!!.readJob()
            createFireBaseDispatcher("my-first-job-", 0, calendar?.get(Calendar.SECOND)!!.plus(getDateAndTimeObject(alJobModel.get(0)) * 60).toLong())
            createFireBaseDispatcher("my-second-job-", 300, calendar?.get(Calendar.SECOND)!!.plus(getDateAndTimeObject(alJobModel.get(1)) * 60).toLong())
*/
        }

    }

    fun createFireBaseDispatcher(jobName: String, currentTime: Int, startTime: Long?) {

        val myExtrasBundle = Bundle()
        myExtrasBundle.putString("job_name", jobName)

        val myJob = dispatcher?.newJobBuilder()
                // the JobService that will be called
                ?.setService(MyJobFireBaseService::class.java)
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

    private fun getDateAndTimeObject(jobDateTime: JobDateTime): Int {
        var currentDate = DateTime()

        var featureDate = DateTime(jobDateTime?.yearString?.toInt()!!, jobDateTime?.monthString?.toInt()!!,
                jobDateTime?.dateString?.toInt()!!, jobDateTime?.hourString?.toInt()!!, jobDateTime?.minuteString?.toInt()!!)

        startSecond = Minutes.minutesBetween(currentDate, featureDate).getMinutes() * 60

        return Minutes.minutesBetween(currentDate, featureDate).getMinutes()
    }

    fun scheduleJob(jobId: Int, delay: Int) {
        val builder = JobInfo.Builder(jobId, serviceComponent)
        builder.setMinimumLatency((delay * 1000).toLong()) // wait at least
        //builder.setOverrideDeadline((3 * 1000).toLong()) // maximum delay
        //builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED); // require unmetered network
        //builder.setRequiresDeviceIdle(false); // device should be idle
        //builder.setRequiresCharging(false); // we don't care if the device is charging or not
        builder.setRequiresDeviceIdle(false)
        jobScheduler?.schedule(builder.build())
    }

    fun scheduleUsingAlarmManager() {
        alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0)

        alarmMgr!!.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + 60 * 1000, alarmIntent)
    }


}// Required empty public constructor
