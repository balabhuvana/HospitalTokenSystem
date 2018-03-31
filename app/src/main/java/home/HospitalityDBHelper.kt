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
var COL_DATE = "job_data"
var COL_TIME = "job_time"
var COL_SCHEDULE = "job_scheduled"

class HospitalityDBHelper(var context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {

        val createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COL_TIME + " VARCHAR(256)," +
                COL_DATE + " VARCHAR(256)," +
                COL_SCHEDULE + " VARCHAR(256))";

        db?.execSQL(createTable)

    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {

    }

    fun insertJob(jobModel: JobModel) {
        val db = this.writableDatabase
        val contentValue = ContentValues()
        contentValue.put(COL_TIME, jobModel.jobTime)
        contentValue.put(COL_DATE, jobModel.jobDate)
        contentValue.put(COL_SCHEDULE, jobModel.isScheduled)
        val result = db.insert(TABLE_NAME, null, contentValue)
        if (result == -1.toLong()) {
            Toast.makeText(context, "Table not created", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(context, "Table is created successfully", Toast.LENGTH_LONG).show()
        }
    }

    fun readJob(): ArrayList<JobModel> {
        var jobModelList = ArrayList<JobModel>()

        val db = this.readableDatabase
        val query = "select * from " + TABLE_NAME
        val result = db.rawQuery(query, null)

        if (result.moveToFirst()) {

            do {
                var jobModel = JobModel()
                jobModel.jobId = result.getString(result.getColumnIndex(COL_ID))
                jobModel.jobTime = result.getString(result.getColumnIndex(COL_TIME))
                jobModel.jobDate = result.getString(result.getColumnIndex(COL_DATE))
                jobModel.isScheduled = result.getString(result.getColumnIndex(COL_SCHEDULE))
                jobModelList.add(jobModel)
            } while (result.moveToNext())

        }

        result.close()
        db.close()
        return jobModelList
    }


    fun deleteJob() {
        val db = this.writableDatabase
        db.delete(TABLE_NAME, COL_ID + "=?", arrayOf(1.toString()))
        db.close()
    }
}