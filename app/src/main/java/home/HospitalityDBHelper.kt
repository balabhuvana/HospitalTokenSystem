package home

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast

/**
 * Created by bala on 29/3/18.
 */
var DATABASE_NAME = "hospitality"
var TABLE_NAME = "Job"
var COL_ID = "job_id"
var COL_DATE = "job_date"
var COL_MONTH = "job_month"
var COL_YEAR = "job_year"
var COL_HOUR = "job_hour"
var COL_MINUTE = "job_minute"
var COL_SCHEDULE = "job_scheduled"

class HospitalityDBHelper(var context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {

        val createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COL_DATE + " VARCHAR(256)," +
                COL_MONTH + " VARCHAR(256)," +
                COL_YEAR + " VARCHAR(256)," +
                COL_HOUR + " VARCHAR(256)," +
                COL_MINUTE + " VARCHAR(256)," +
                COL_SCHEDULE + " flag INTEGER DEFAULT 0)";

        db?.execSQL(createTable)

    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {

    }

    fun insertJob(jobDateTime: JobDateTime) {
        val db = this.writableDatabase
        val contentValue = ContentValues()
        contentValue.put(COL_DATE, jobDateTime.dateString)
        contentValue.put(COL_MONTH, jobDateTime.monthString)
        contentValue.put(COL_YEAR, jobDateTime.yearString)
        contentValue.put(COL_HOUR, jobDateTime.hourString)
        contentValue.put(COL_MINUTE, jobDateTime.minuteString)
        contentValue.put(COL_SCHEDULE, 0)
        val result = db.insert(TABLE_NAME, null, contentValue)
        if (result == -1.toLong()) {
            Toast.makeText(context, "Table not created", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(context, "Table is created successfully", Toast.LENGTH_LONG).show()
        }
    }

    fun readJob(): ArrayList<JobDateTime> {
        var jobModelList = ArrayList<JobDateTime>()

        val db = this.readableDatabase
        val query = "select * from " + TABLE_NAME
        val result = db.rawQuery(query, null)

        if (result.moveToFirst()) {

            do {
                var jobDateTime = JobDateTime()
                jobDateTime.jobId = result.getString(result.getColumnIndex(COL_ID))
                jobDateTime.dateString = result.getString(result.getColumnIndex(COL_DATE))
                jobDateTime.monthString = result.getString(result.getColumnIndex(COL_MONTH))
                jobDateTime.yearString = result.getString(result.getColumnIndex(COL_YEAR))
                jobDateTime.hourString = result.getString(result.getColumnIndex(COL_HOUR))
                jobDateTime.minuteString = result.getString(result.getColumnIndex(COL_MINUTE))
                jobDateTime.scheduleString = result.getInt(result.getColumnIndex(COL_SCHEDULE))
                jobModelList.add(jobDateTime)
            } while (result.moveToNext())

        }

        result.close()
        db.close()
        return jobModelList
    }

    fun readSpecificJob(): ArrayList<JobDateTime> {
        var jobModelList = ArrayList<JobDateTime>()

        val db = this.readableDatabase

        val query = "SELECT * FROM " + TABLE_NAME + " WHERE job_scheduled=" + "0"
        val result = db.rawQuery(query, null)

        if (result.moveToFirst()) {

            do {
                var jobDateTime = JobDateTime()
                jobDateTime.jobId = result.getString(result.getColumnIndex(COL_ID))
                jobDateTime.dateString = result.getString(result.getColumnIndex(COL_DATE))
                jobDateTime.monthString = result.getString(result.getColumnIndex(COL_MONTH))
                jobDateTime.yearString = result.getString(result.getColumnIndex(COL_YEAR))
                jobDateTime.hourString = result.getString(result.getColumnIndex(COL_HOUR))
                jobDateTime.minuteString = result.getString(result.getColumnIndex(COL_MINUTE))
                jobDateTime.scheduleString = result.getInt(result.getColumnIndex(COL_SCHEDULE))
                jobModelList.add(jobDateTime)
            } while (result.moveToNext())

        }

        result.close()
        db.close()
        return jobModelList
    }


    fun updateJob(jobId: String) {
        val db = this.writableDatabase
        var contentValue = ContentValues()
        contentValue.put(COL_SCHEDULE, 1)
        db.update(TABLE_NAME, contentValue, "job_id=" + jobId, null);
    }


    fun deleteJob() {
        val db = this.writableDatabase
        db.execSQL("delete from " + TABLE_NAME);
        //db.delete(TABLE_NAME, COL_ID + "=?", arrayOf(1.toString()))
        db.close()
    }
}