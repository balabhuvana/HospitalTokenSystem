package home


import android.Manifest
import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.os.SystemClock
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.hospital.tokensystem.R
import kotlinx.android.synthetic.main.fragment_token.*
import org.joda.time.DateTime
import org.joda.time.Minutes
import java.util.*


/**
 * A simple [Fragment] subclass.
 */
class TokenFragment : Fragment() {

    private var TAG: String = TokenFragment::class.java.simpleName
    private var db: HospitalityDBHelper? = null
    private var timePickerDialog: TimePickerDialog? = null
    private var datePickerDialog: DatePickerDialog? = null
    private var calendar: Calendar? = null
    private var futureDateTime: JobDateTime? = null
    private var startSecond = 0
    private var alarmMgr: AlarmManager? = null
    private var alarmIntent: PendingIntent? = null
    private var intent: Intent? = null
    private var MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 1000

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_token, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = HospitalityDBHelper(context)
        calendar = Calendar.getInstance()
        futureDateTime = JobDateTime()


        alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        intent = Intent(context, AlarmReceiver::class.java)

        btnSetTime.setOnClickListener {
            val c = Calendar.getInstance()
            val mHour = c.get(Calendar.HOUR_OF_DAY)
            val mMinute = c.get(Calendar.MINUTE)

            timePickerDialog = TimePickerDialog(context,
                    TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                        futureDateTime?.apply {
                            hourString = hourOfDay.toString()
                            minuteString = minute.toString()
                        }
                        val timeStr = hourOfDay.toString() + ":" + minute
                        tvTime.text = timeStr

                    }, mHour, mMinute, false)
            timePickerDialog?.show()

        }

        btnSetDate.setOnClickListener {
            val c = Calendar.getInstance()
            val mYear = c.get(Calendar.YEAR)
            val mMonth = c.get(Calendar.MONTH)
            val mDay = c.get(Calendar.DAY_OF_MONTH)


            datePickerDialog = DatePickerDialog(activity,
                    DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                        val dateStr = dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year
                        tvDate.text = dateStr
                        futureDateTime?.apply {
                            dateString = dayOfMonth.toString()
                            monthString = (monthOfYear + 1).toString()
                            yearString = year.toString()
                        }

                    }, mYear, mMonth, mDay)
            datePickerDialog?.datePicker?.setMinDate(System.currentTimeMillis() - 1000)
            datePickerDialog?.show()
        }

        btnAddAppoinment.setOnClickListener {
            if (!(tvTime.text.equals("") || tvDate.text.equals(""))) {
                db?.insertJob(futureDateTime!!)
            } else {
                Toast.makeText(context, "Please select the date and time", Toast.LENGTH_LONG).show()
            }
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
            showCameraPreview()
        }

    }

    private fun showCameraPreview() {
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_PHONE_STATE)
                == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context,
                Manifest.permission.SEND_SMS)
                == PackageManager.PERMISSION_GRANTED) {

            createAppointmentSchedule()

        } else {
            requestCameraPermission()
        }
    }

    private fun requestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                Manifest.permission.READ_PHONE_STATE)) {

            val builder = AlertDialog.Builder(activity)

            // Set the alert dialog title
            builder.setTitle("App background color")

            // Display a message on alert dialog
            builder.setMessage("Are you want to set the app background color to RED?")

            // Set a positive button and its click listener on alert dialog
            builder.setPositiveButton("YES") { dialog, which ->
                // Do something when user press the positive button
                Toast.makeText(context, "Ok, we change the app background.", Toast.LENGTH_SHORT).show()
                requestPermissions(arrayOf(Manifest.permission.READ_PHONE_STATE, Manifest.permission.SEND_SMS),
                        MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
            }


            // Display a negative button on alert dialog
            builder.setNegativeButton("No") { dialog, which ->
                Toast.makeText(context, "You are not agree.", Toast.LENGTH_SHORT).show()
            }


            // Display a neutral button on alert dialog
            builder.setNeutralButton("Cancel") { _, _ ->
                Toast.makeText(context, "You cancelled the dialog.", Toast.LENGTH_SHORT).show()
            }

            // Finally, make the alert dialog using builder
            val dialog: AlertDialog = builder.create()

            // Display the alert dialog on app interface
            dialog.show()

            /*requestPermissions(arrayOf(Manifest.permission.READ_PHONE_STATE, Manifest.permission.SEND_SMS),
                    MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);*/


        } else {

            // No explanation needed, we can request the permission.

            Log.d(TAG, "requestCameraPermission - else")

            requestPermissions(arrayOf(Manifest.permission.READ_PHONE_STATE, Manifest.permission.SEND_SMS),
                    MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);


            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.
        }
    }


    private fun createAppointmentSchedule() {
        val alJobModel: ArrayList<JobDateTime> = db!!.readSpecificJob()
        Log.d("btnCreateJob ->", "" + alJobModel.size)
        for (item in alJobModel) {
            scheduleUsingAlarmManager(item.jobId!!.toInt(), getDateAndTimeObject(item))
            db?.updateJob(item.jobId!!)
        }
    }

    private fun getDateAndTimeObject(jobDateTime: JobDateTime): Int {
        val currentDate = DateTime()

        val featureDate = DateTime(jobDateTime.yearString?.toInt()!!, jobDateTime.monthString?.toInt()!!,
                jobDateTime.dateString?.toInt()!!, jobDateTime.hourString?.toInt()!!, jobDateTime.minuteString?.toInt()!!)

        startSecond = Minutes.minutesBetween(currentDate, featureDate).minutes

        return Minutes.minutesBetween(currentDate, featureDate).getMinutes() * 60 * 1000
    }


    fun scheduleUsingAlarmManager(jobId: Int, delay: Int) {

        Log.d(TAG, " scheduleUsingAlarmManager - " + delay)

        alarmIntent = PendingIntent.getBroadcast(context, jobId, intent, 0)
        alarmMgr!!.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + delay, alarmIntent)

    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_READ_PHONE_STATE -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[1] == PackageManager.PERMISSION_GRANTED)) {

                    val read_phone_state = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    val send_sms_state = grantResults[1] == PackageManager.PERMISSION_GRANTED
                    if (read_phone_state && send_sms_state) {
                        createAppointmentSchedule()
                    }
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return
            }

        // Add other 'when' lines to check for other
        // permissions this app might request.

            else -> {
                // Ignore all other requests.
            }
        }
    }

}// Required empty public constructor
