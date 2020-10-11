package com.deepquotes;

import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Quotes {


    public static final int UPDATE_TEXT = 123456;
    private String quote;
    private JobParameters jobParameters;

    public Quotes(String quote, JobParameters jobParameters) {
        this.quote = quote;
        this.jobParameters = jobParameters;
    }

    public String getQuote() {
        return quote;
    }

    public JobParameters getJobParameters() {
        return jobParameters;
    }

}
