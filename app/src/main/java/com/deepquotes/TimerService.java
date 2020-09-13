package com.deepquotes;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.widget.RemoteViews;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class TimerService extends Service {

    private SharedPreferences sharedPreferences;

    private Timer timer;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private int refreshTime;


    public TimerService() {
    }



    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void onCreate() {
        super.onCreate();

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                updateViews();
            }
        }, 0, 1000);
    }

    private void updateViews() {
        String time = sdf.format(new Date());
        RemoteViews rv = new RemoteViews(getPackageName(), R.layout.quotes_layout);
        rv.setTextViewText(R.id.quotes_textview, "黄朝阳666 ： " + time);
        AppWidgetManager manager = AppWidgetManager.getInstance(getApplicationContext());
        ComponentName cn = new ComponentName(getApplicationContext(), QuotesWidgetProvider.class);
        manager.updateAppWidget(cn, rv);
    }

    public void onDestroy() {
        super.onDestroy();
        timer = null;
    }

}
