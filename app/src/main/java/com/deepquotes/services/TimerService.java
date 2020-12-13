package com.deepquotes.services;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.deepquotes.Quotes;
import com.deepquotes.QuotesWidgetProvider;
import com.deepquotes.R;
import com.deepquotes.utils.Constant;
import com.deepquotes.utils.DBManager;
import com.deepquotes.utils.QuotesSQLHelper;

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

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.util.TypedValue.COMPLEX_UNIT_SP;
import static com.deepquotes.Quotes.UPDATE_TEXT;

public class TimerService extends Service {

    /**
     * activity存活时的刷新服务，
     * 通过RemoteView刷新Widgets
     * 通过Broadcast通知Activity刷新主界面
     */

    private SharedPreferences appConfigSP;


    private QuotesSQLHelper mQuotesSQLHelper;
    private SQLiteDatabase mDatabase;

    private final static String UNKOWN_AUTHOR = "来源于网络";


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void onCreate() {
        super.onCreate();

        mQuotesSQLHelper = DBManager.getInstance(this);
        mDatabase = mQuotesSQLHelper.getReadableDatabase();

        appConfigSP = getSharedPreferences("appConfig",MODE_PRIVATE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        Log.d("6666", String.valueOf(appConfigSP.getInt("当前刷新间隔(分钟):", 15)));


        if (appConfigSP.getBoolean("isEnableHitokoto",false)) {   //启用一言
            if (appConfigSP.getBoolean("随机",false))             //随机一言
                getDeepQuotes(new Random().nextInt(4), "");
            else {
                StringBuffer stringBuffer = new StringBuffer("?");
                if(appConfigSP.getBoolean("动画、漫画",false)) stringBuffer.append("c=a&c=b");
                if(appConfigSP.getBoolean("游戏",false)) stringBuffer.append("&c=c");
                if(appConfigSP.getBoolean("文学",false)) stringBuffer.append("&c=d");
                if(appConfigSP.getBoolean("影视",false)) stringBuffer.append("&c=h");
                if(appConfigSP.getBoolean("诗词",false)) stringBuffer.append("&c=i");
                if(appConfigSP.getBoolean("网易云",false)) stringBuffer.append("&c=j");
//                Log.d("TAG","stringBuffer "+stringBuffer);
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

                        Quotes quotes = (Quotes) msg.obj;
                        String text = quotes.getText();
                        String author = quotes.getAuthor();
                        //向数据库插入数据
                        insertDatabase(text,author);

                        //发送更新成功广播,通知Activity刷新界面（若Activity存活）
                        Intent updatetext = new Intent("com.deepquotes.broadcast.updateTextView");
                        updatetext.putExtra("quote",text);
//                        Log.d("广播","already send broadcast");
                        sendBroadcast(updatetext);

                        //更新widget
                        RemoteViews remoteViews = new RemoteViews(getApplicationContext().getPackageName(), R.layout.quotes_layout);
                        remoteViews.setTextViewText(R.id.quotes_textview, text);
                        remoteViews.setTextColor(R.id.quotes_textview, appConfigSP.getInt("fontColor", Color.WHITE));
                        remoteViews.setTextViewTextSize(R.id.quotes_textview, COMPLEX_UNIT_SP, appConfigSP.getInt("字体大小:", 20));
                        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
                        ComponentName componentName = new ComponentName(getApplicationContext(), QuotesWidgetProvider.class);
                        appWidgetManager.updateAppWidget(componentName, remoteViews);


                    }
                    break;
                default:break;

            }
        }
    };



    public void onDestroy() {
        super.onDestroy();
        mDatabase.close();
    }


    private void insertDatabase(String text,String author){
        String insertSQL = "insert into "+
                Constant.TABLE_NAME + "("+Constant._TEXT + "," + Constant._AUTHOR + ")"+
                " values('" + text + "','" + author + "')";
        mDatabase.execSQL(insertSQL);
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

                            if (responseJSON.getInt("code") == 429)
                                responseStr = "每日调用次数已达上限";
                            else {
                                JSONObject quoteData = responseJSON.getJSONObject("data");
                                responseStr = quoteData.getString("title");
                            }

//                            Log.d("DeepQuote2",responseStr);

                            Message message = handler.obtainMessage();
                            message.what = UPDATE_TEXT;
                            message.obj = new Quotes(responseStr,UNKOWN_AUTHOR);
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
                            responseStr = responseJSON.getString("text");

//                            Log.d("DeepQuote3",responseStr);

                            Message message = handler.obtainMessage();
                            message.what = UPDATE_TEXT;
                            message.obj = new Quotes(responseStr,UNKOWN_AUTHOR);
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
                            String author = responseJSON.getString("from");
//                            Log.d("hikotoko",responseStr);

                            Message message = handler.obtainMessage();
                            message.what = UPDATE_TEXT;
                            message.obj = new Quotes(responseStr,author);
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

                    Message message = handler.obtainMessage();
                    message.what = UPDATE_TEXT;
                    message.obj = new Quotes(data,UNKOWN_AUTHOR);
                    handler.sendMessage(message);

//                    Log.d("DeepQuote1", data);
                } catch (IOException e) {
                    e.printStackTrace();
                }catch (NullPointerException e){
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
        Request request = new Request.Builder().url("https://api.yum6.cn/djt/index.php?encode=json").build();
        client.newCall(request).enqueue(callback);
    }

    private void getHitokotoQuote(String postParam,Callback callback){

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url("https://v1.hitokoto.cn/"+postParam).build();
        client.newCall(request).enqueue(callback);

    }

}
