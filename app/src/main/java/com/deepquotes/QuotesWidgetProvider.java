package com.deepquotes;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.appwidget.AppWidgetManager.ACTION_APPWIDGET_DELETED;
import static android.appwidget.AppWidgetManager.ACTION_APPWIDGET_UPDATE;
import static android.appwidget.AppWidgetManager.getInstance;
import static android.content.Context.MODE_PRIVATE;
import static android.util.TypedValue.COMPLEX_UNIT_SP;

public class QuotesWidgetProvider extends AppWidgetProvider {

    public static final String ACTION_CLICK = "com.deepquotes.ACTION_CLICK";
    private static Set idsSet = new HashSet();
    public static int mIndex;
    private SharedPreferences historyQuotesSP ;
    private SharedPreferences appConfigSP;


    private static final int UPDATE_DURATION = 30 * 1000; // Widget 更新间隔



//    //窗口小部件点击时调用
//    @Override
//    public void onReceive(Context context, Intent intent) {
//        super.onReceive(context, intent);


//        String action = intent.getAction();
//        Log.d("TAG", "onReceive: 定时更新"+action);
//        if (action.equals("CLOCK_WIDGET_UPDATE")) {
//            Toast.makeText(context, "定时更新", Toast.LENGTH_SHORT).show();
//            Log.d("TAG", "onReceive: 定时更新");
//
//
//            Intent i = new Intent(context.getApplicationContext(),TimerService.class);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                context.startForegroundService(i);
//            }else context.getApplicationContext().startService(i);
//            //getQuotes(context);
//            //updateAllWidget();
//        }
//    }

//    private void updateAllWidget(Context context, AppWidgetManager appWidgetManager, Set set) {
//        int appId;
//        Iterator it = set.iterator();
//
//        mIndex++;
//
//        while (it.hasNext()){
//            appId = ((Integer)it.next()).intValue();
//
//            RemoteViews remoteViews = new RemoteViews(context.getPackageName(),R.layout.quotes_layout);
//            remoteViews.setTextViewText(R.id.quotes_textview,String.valueOf(mIndex));
//            remoteViews.setOnClickPendingIntent(R.id.quotes_textview,updateQuotesIntent(context));
//        }
//    }
//
//    private PendingIntent updateQuotesIntent(Context context) {
//        Intent intent = new Intent();
//        intent.setClass(context,QuotesWidgetProvider.class);
//        intent.addCategory(Intent.CATEGORY_ALTERNATIVE);
//        PendingIntent pi = PendingIntent.getBroadcast(context,0,intent,0);
//        return pi;
//
//    }


    //窗口小部件更新时时调用
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

//        AlarmManager alarmManager = (AlarmManager) context.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
//
//        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(),UPDATE_DURATION,createPendingIntent(context));

        Log.d("QuotesWidgetProvider", "onUpdate: "+context);

//        Intent intent = new Intent(context.getApplicationContext(),TimerService.class);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            context.startForegroundService(intent);
//        }else context.getApplicationContext().startService(intent);

//        Toast.makeText(context,"你更新了控件",Toast.LENGTH_SHORT).show();

        if (historyQuotesSP==null)
            historyQuotesSP = context.getSharedPreferences("historyQuotes",MODE_PRIVATE);
        if (appConfigSP==null)
            appConfigSP = context.getSharedPreferences("appConfig",MODE_PRIVATE);



        final int N = appWidgetIds.length;
        for (int i=0; i<N; i++){
            int appWidgetId = appWidgetIds[i];

            RemoteViews views = new RemoteViews(context.getPackageName(),R.layout.quotes_layout);
            views.setTextColor(R.id.quotes_textview,Color.WHITE);
            int current = historyQuotesSP.getInt("currentQuote",0);
            views.setTextViewText(R.id.quotes_textview,historyQuotesSP.getString(String.valueOf(current),"欲买桂花同载酒，终不似，少年游"));
            views.setTextViewTextSize(R.id.quotes_textview, COMPLEX_UNIT_SP, appConfigSP.getInt("字体大小:", 20));
            Intent openIntent = new Intent(context, ScrollingActivity.class);

            PendingIntent openPendingIntent = PendingIntent.getActivity(context, 0, openIntent, 0);
            views.setOnClickPendingIntent(R.id.quotes_textview, openPendingIntent);

            appWidgetManager.updateAppWidget(appWidgetId,views);
        }

//        views.setTextViewText(R.id.quotes_textview,"控件更新: "+ Math.random());
//        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.quotes_layout);
////        views.setTextViewText(R.id.quotes_textview, "66666");
////        // Instruct the widget manager to update the widget
//        appWidgetManager.updateAppWidget(appWidgetIds, views);

    }

    //窗口小部件被删除时调用
    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        Toast.makeText(context,"你删除了控件",Toast.LENGTH_SHORT).show();
    }

    private PendingIntent createPendingIntent(Context context){
        Intent alarmIntent = new Intent(context.getApplicationContext(),TimerService.class);
        PendingIntent pendingIntent = PendingIntent.getService(context.getApplicationContext(),0,alarmIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }


    //该窗口小部件第一次添加到桌面时调用该方法
    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);

        appConfigSP = context.getSharedPreferences("appConfig",MODE_PRIVATE);
        int duration = appConfigSP.getInt("当前刷新间隔(分钟):",15);

        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        ComponentName componentName = new ComponentName(context,UpdateService.class);
        JobInfo jobInfo = new JobInfo.Builder(12345,componentName)
                .setPeriodic(duration*60*1000)
                .build();
        jobScheduler.schedule(jobInfo);




        Toast.makeText(context,"你添加了了控件",Toast.LENGTH_SHORT).show();
        Log.i("TAG", "onEnabled: ");
//        Intent startTimerIntent = new Intent(context, TimerService.class);
////        startTimerIntent.putExtra("refreshTime",sharedPreferences.getInt("当前刷新间隔(分钟):",10));
//        context.startService(startTimerIntent);
    }

    //最后一个该窗口小部件删除时调用该方法
    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        Toast.makeText(context,"你删除了最后一个控件",Toast.LENGTH_SHORT).show();

        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.cancel(12345);
//        context.stopService(new Intent(context, TimerService.class));

//        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//        manager.cancel(createPendingIntent(context));
    }


    //当小部件大小改变时调用
    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
        Toast.makeText(context,"你改变了控件大小",Toast.LENGTH_SHORT).show();
    }


    //当小部件从备份恢复时调用该方法
    @Override
    public void onRestored(Context context, int[] oldWidgetIds, int[] newWidgetIds) {
        super.onRestored(context, oldWidgetIds, newWidgetIds);
        Toast.makeText(context,"你恢复控件",Toast.LENGTH_SHORT).show();
    }




//    public void getQuotes(final Context context){
//        final int requestCode = new Random().nextInt(4);
//        switch (requestCode){
//            case 1:
//                new Quotes().getHitokotoQuotes(new Callback() {
//                    @Override
//                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
//
//                    }
//
//                    @Override
//                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
//                        String responseData = response.body().string();
//                        try {
//                            JSONObject jsonObject = new JSONObject(responseData);
//                            Toast.makeText(context,jsonObject.get("hitokoto").toString(),Toast.LENGTH_SHORT).show();
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });
//                break;
//            case 2:
//                new Quotes().getDeepQuotes();
//                break;
//            case 3:
//                new Quotes().getDeepQuotes2(new Callback() {
//                    @Override
//                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
//
//                    }
//
//                    @Override
//                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
//                        String responseData = response.body().string();
//                        Toast.makeText(context,responseData,Toast.LENGTH_SHORT).show();
//                    }
//                });
//                break;
//            case 4:
//                new Quotes().getDeepQuotes3();
//                break;
//            default:
//                break;
//        }
//    }

}
