package com.deepquotes

import android.app.job.JobParameters

class Quotes {
    var text: String
        private set
    var author: String
        private set
    var jobParameters: JobParameters? = null
        private set

    constructor(text: String, author: String, jobParameters: JobParameters?) {
        this.text = text
        this.author = author
        this.jobParameters = jobParameters
    }

    constructor(text: String, author: String) {
        this.text = text
        this.author = author
    }

    companion object {
        const val UPDATE_TEXT = 123456
    }
}