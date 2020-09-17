package com.deepquotes;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.util.TypedValue.COMPLEX_UNIT_SP;
import static com.deepquotes.Quotes.UPDATE_TEXT;

public class TimerService extends Service {

    private SharedPreferences sharedPreferences;

    private Timer timer;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private int refreshTime;




    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void onCreate() {
        super.onCreate();

        sharedPreferences = getSharedPreferences("data",MODE_PRIVATE);

//        timer = new Timer();
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                updateViews();
//            }
//        }, 0, 1000);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("6666", String.valueOf(sharedPreferences.getInt("当前刷新间隔(分钟):", 10)));

        if (sharedPreferences.getBoolean("isEnableHitokoto",false)) {   //启用一言
            if (sharedPreferences.getBoolean("随机",false))             //随机一言
                getDeepQuotes(new Random().nextInt(4), "");
            else {
                StringBuffer stringBuffer = new StringBuffer("?");
                if(sharedPreferences.getBoolean("动画、漫画",false)) stringBuffer.append("c=a&c=b");
                if(sharedPreferences.getBoolean("游戏",false)) stringBuffer.append("&c=c");
                if(sharedPreferences.getBoolean("文学",false)) stringBuffer.append("&c=d");
                if(sharedPreferences.getBoolean("影视",false)) stringBuffer.append("&c=h");
                if(sharedPreferences.getBoolean("诗词",false)) stringBuffer.append("&c=i");
                if(sharedPreferences.getBoolean("网易云",false)) stringBuffer.append("&c=j");
                Log.d("TAG","stringBuffer "+stringBuffer);
                getDeepQuotes(new Random().nextInt(4),stringBuffer.toString());
            }
        }else {     //关闭一言
            getDeepQuotes(new Random().nextInt(3),"");
        }

        return super.onStartCommand(intent, flags, startId);
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case UPDATE_TEXT:
                    if (msg.obj != null) {
                        String textMessage = msg.obj.toString();
//                        headlineTextView.setText(textMessage);

                        RemoteViews remoteViews = new RemoteViews(getApplicationContext().getPackageName(),R.layout.quotes_layout);
                        remoteViews.setTextViewText(R.id.quotes_textview, textMessage);
                        remoteViews.setTextColor(R.id.quotes_textview, sharedPreferences.getInt("fontColor", Color.WHITE));
                        remoteViews.setTextViewTextSize(R.id.quotes_textview, COMPLEX_UNIT_SP, sharedPreferences.getInt("字体大小:", 10));
                        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
                        ComponentName componentName = new ComponentName(getApplicationContext(), QuotesWidgetProvider.class);
                        appWidgetManager.updateAppWidget(componentName, remoteViews);
                    }
                    break;
                default:break;

            }
        }
    };



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

    private void getDeepQuotes(int seed,String postParam){
        switch (seed){
            case 0:
                getDeepQuote();
                break;
            case 1:
                getDeepQuote2(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {

                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        try {
                            String responseStr = response.body().string();
                            JSONObject responseJSON = new JSONObject(responseStr);
                            JSONObject quoteData = responseJSON.getJSONObject("data");
                            responseStr = quoteData.getString("title");

                            Log.d("DeepQuote2",responseStr);

                            Message message = new Message();
                            message.what = UPDATE_TEXT;
                            message.obj = responseStr;
                            handler.sendMessage(message);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                break;
            case 2:
                getDeepQuote3(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {

                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        try {
                            String responseStr = response.body().string();
                            JSONObject responseJSON = new JSONObject(responseStr);
                            responseStr = responseJSON.getString("txt");

                            Log.d("DeepQuote3",responseStr);

                            Message message = new Message();
                            message.what = UPDATE_TEXT;
                            message.obj = responseStr;
                            handler.sendMessage(message);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                break;
            default:
                getHitokotoQuote(postParam,new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {

                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        try {
                            String responseStr = response.body().string();
                            JSONObject responseJSON = new JSONObject(responseStr);
                            responseStr = responseJSON.getString("hitokoto");
                            Log.d("hikotoko",responseStr);

                            Message message = new Message();
                            message.what = UPDATE_TEXT;
                            message.obj = responseStr;
                            handler.sendMessage(message);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });



        }
    }

    private void getDeepQuote(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String data=null;
                try {
                    Document document = Jsoup.connect("http://www.nows.fun/").get();
                    Elements element = document.select("div.main-wrapper");
                    for (int i=0;i<element.size();i++) {
                        data = element.get(i).select("span").text();
                    }

                    Message message = new Message();
                    message.what = UPDATE_TEXT;
                    message.obj = data;
                    handler.sendMessage(message);

                    Log.d("DeepQuote1", data);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void getDeepQuote2(Callback callback){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url("https://v1.alapi.cn/api/soul").build();
        client.newCall(request).enqueue(callback);
    }

    private void getDeepQuote3(Callback callback){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url("https://data.zhai78.com/openOneBad.php").build();
        client.newCall(request).enqueue(callback);
    }

    private void getHitokotoQuote(String postParam,Callback callback){

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url("https://v1.hitokoto.cn/"+postParam).build();
        client.newCall(request).enqueue(callback);

    }

}
