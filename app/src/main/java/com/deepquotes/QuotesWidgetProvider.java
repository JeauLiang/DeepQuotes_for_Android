package com.deepquotes;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

public class QuotesWidgetProvider extends AppWidgetProvider {

    public static final String ACTION_CLICK = "com.deepquotes.ACTION_CLICK";
    private static Set idsSet = new HashSet();
    public static int mIndex;

    //窗口小部件点击时调用
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        String action = intent.getAction();
        if (ACTION_CLICK.equals(action)) {
            Toast.makeText(context, "你点击了控件", Toast.LENGTH_SHORT).show();
            //getQuotes(context);
            //updateAllWidget();
        }
    }

    private void updateAllWidget(Context context, AppWidgetManager appWidgetManager, Set set) {
        int appId;
        Iterator it = set.iterator();

        mIndex++;

        while (it.hasNext()){
            appId = ((Integer)it.next()).intValue();

            RemoteViews remoteViews = new RemoteViews(context.getPackageName(),R.layout.quotes_layout);
            remoteViews.setTextViewText(R.id.quotes_textview,String.valueOf(mIndex));
            remoteViews.setOnClickPendingIntent(R.id.quotes_textview,updateQuotesIntent(context));
        }
    }

    private PendingIntent updateQuotesIntent(Context context) {
        Intent intent = new Intent();
        intent.setClass(context,QuotesWidgetProvider.class);
        intent.addCategory(Intent.CATEGORY_ALTERNATIVE);
        PendingIntent pi = PendingIntent.getBroadcast(context,0,intent,0);
        return pi;

    }


    //窗口小部件更新时时调用
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        //Toast.makeText(context,"你更新了控件",Toast.LENGTH_SHORT).show();
        

    }

    //窗口小部件被删除时调用
    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        //Toast.makeText(context,"你删除了控件",Toast.LENGTH_SHORT).show();
    }

    //该窗口小部件第一次添加到桌面时调用该方法
    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        //Toast.makeText(context,"你添加了了控件",Toast.LENGTH_SHORT).show();
    }

    //最后一个该窗口小部件删除时调用该方法
    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        //Toast.makeText(context,"你删除了最后一个控件",Toast.LENGTH_SHORT).show();
    }


    //当小部件大小改变时调用
    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
        //Toast.makeText(context,"你改变了控件大小",Toast.LENGTH_SHORT).show();
    }


    //当小部件从备份恢复时调用该方法
    @Override
    public void onRestored(Context context, int[] oldWidgetIds, int[] newWidgetIds) {
        super.onRestored(context, oldWidgetIds, newWidgetIds);
        //Toast.makeText(context,"你恢复控件",Toast.LENGTH_SHORT).show();
    }

    public void getQuotes(final Context context){
        final int requestCode = new Random().nextInt(4);
        switch (requestCode){
            case 1:
                new Quotes().getHitokotoQuotes(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {

                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        String responseData = response.body().string();
                        try {
                            JSONObject jsonObject = new JSONObject(responseData);
                            Toast.makeText(context,jsonObject.get("hitokoto").toString(),Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                break;
            case 2:
                new Quotes().getDeepQuotes();
                break;
            case 3:
                new Quotes().getDeepQuotes2(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {

                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        String responseData = response.body().string();
                        Toast.makeText(context,responseData,Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            case 4:
                new Quotes().getDeepQuotes3();
                break;
            default:
                break;
        }
    }

}
