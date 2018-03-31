package home

/**
 * Created by bala on 28/3/18.
 */
class JobModel {
    var jobId: String? = null
    var jobTime: String? = null
    var jobDate: String? = null
    var isScheduled: String? = null

    constructor(jobTime: String, jobDate: String, isSchedule: String) {
        this.jobTime = jobTime
        this.jobDate = jobDate
        this.isScheduled = isScheduled
    }

    constructor() {

    }
}