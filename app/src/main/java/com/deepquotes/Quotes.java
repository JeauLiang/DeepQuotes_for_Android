package com.deepquotes;

import android.app.job.JobParameters;

public class Quotes {


    public static final int UPDATE_TEXT = 123456;
    private String text;
    private String author;
    private JobParameters jobParameters;

    public Quotes(String text, String author,JobParameters jobParameters) {
        this.text = text;
        this.author = author;
        this.jobParameters = jobParameters;
    }

    public String getAuthor() {
        return author;
    }

    public Quotes(String text, String author) {
        this.text = text;
        this.author = author;
    }

    public String getText() {
        return text;
    }

    public JobParameters getJobParameters() {
        return jobParameters;
    }

}
