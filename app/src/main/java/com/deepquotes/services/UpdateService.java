package com.deepquotes.services;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.RemoteViews;

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
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.util.TypedValue.COMPLEX_UNIT_SP;
import static com.deepquotes.Quotes.UPDATE_TEXT;

public class UpdateService extends JobService {

    /**
     * activity销毁后的后台刷新服务，依赖于JobScheduler任务调度
     */
    
    private final static String TAG = "JOBSERVICE";
    private boolean isFinish = false;

    private SharedPreferences appConfigSP;

    private QuotesSQLHelper mQuotesSQLHelper;
    private SQLiteDatabase mDatabase;

    private final static String UNKOWN_AUTHOR = "来源于网络";



    @Override
    public void onCreate() {
        super.onCreate();

        mQuotesSQLHelper = DBManager.getInstance(this);
        mDatabase = mQuotesSQLHelper.getReadableDatabase();

        appConfigSP = getSharedPreferences("appConfig",MODE_PRIVATE);
    }

    @Override
    public boolean onStartJob(JobParameters params) {
//        Log.i(TAG, "onStartJob: ");


        if (appConfigSP.getBoolean("isEnableHitokoto",false)) {   //启用一言
            if (appConfigSP.getBoolean("随机",false))             //随机一言
                getDeepQuotes(new Random().nextInt(4), "",params);
            else {
                StringBuffer stringBuffer = new StringBuffer("?");
                if(appConfigSP.getBoolean("动画、漫画",false)) stringBuffer.append("c=a&c=b");
                if(appConfigSP.getBoolean("游戏",false)) stringBuffer.append("&c=c");
                if(appConfigSP.getBoolean("文学",false)) stringBuffer.append("&c=d");
                if(appConfigSP.getBoolean("影视",false)) stringBuffer.append("&c=h");
                if(appConfigSP.getBoolean("诗词",false)) stringBuffer.append("&c=i");
                if(appConfigSP.getBoolean("网易云",false)) stringBuffer.append("&c=j");
//                Log.d("TAG","stringBuffer "+stringBuffer);
                getDeepQuotes(new Random().nextInt(4),stringBuffer.toString(),params);
            }
        }else {     //关闭一言
            getDeepQuotes(new Random().nextInt(3),"",params);
        }
        return true;
    }

    Handler handler = new Handler(Looper.myLooper(),new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case UPDATE_TEXT:
                    if (msg.obj != null) {
//                        String textMessage = msg.obj.toString();
                        Quotes quotes = (Quotes) msg.obj;
                        String textMessage = quotes.getText();
                        String author = quotes.getAuthor();
                        //向数据库插入数据
                        insertDatabase(textMessage,author);

                        //发送更新成功广播,通知Activity刷新界面（若Activity存活）
                        Intent updatetext = new Intent("com.deepquotes.broadcast.updateTextView");
                        updatetext.putExtra("quote",textMessage);
//                        Log.d("广播","already send broadcast");
                        sendBroadcast(updatetext);

                        //更新widget
                        RemoteViews remoteViews = new RemoteViews(getApplicationContext().getPackageName(), R.layout.quotes_layout);
                        remoteViews.setTextViewText(R.id.quotes_textview, textMessage);
                        remoteViews.setTextColor(R.id.quotes_textview, appConfigSP.getInt("fontColor", Color.WHITE));
                        remoteViews.setTextViewTextSize(R.id.quotes_textview, COMPLEX_UNIT_SP, appConfigSP.getInt("字体大小:", 20));
                        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
                        ComponentName componentName = new ComponentName(getApplicationContext(), QuotesWidgetProvider.class);
                        appWidgetManager.updateAppWidget(componentName, remoteViews);

//                        int refreshTime = appConfigSP.getInt("当前刷新间隔(分钟):",15);

                        jobFinished(quotes.getJobParameters(),false);
//                        Log.i(TAG, "handleMessage: job finish");

                    }
                    break;
                default:break;

            }
            return false;
        }
    });



    @Override
    public boolean onStopJob(JobParameters params) {
//        Log.i(TAG, "onStopJob: ");
        return true;
    }

    private void insertDatabase(String text,String author){
        String insertSQL = "insert into "+
                Constant.TABLE_NAME + "("+Constant._TEXT + "," + Constant._AUTHOR + ")"+
                " values('" + text + "','" + author + "')";
        mDatabase.execSQL(insertSQL);
    }


    @Override
    public void onDestroy() {
//        Log.i(TAG, "onDestroy: ");
        super.onDestroy();
        mDatabase.close();
    }

    private void getDeepQuotes(int seed, String postParam, final JobParameters parameters){
        switch (seed){
            case 0:
                getDeepQuote(parameters);
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
//                                responseStr = responseJSON.getString("msg");
                                responseStr = "每日调用次数已达上限";
                            else {
                                JSONObject quoteData = responseJSON.getJSONObject("data");
                                responseStr = quoteData.getString("title");
                            }

//                            Log.d("DeepQuote2",responseStr);

                            Message message = handler.obtainMessage();
                            message.what = UPDATE_TEXT;
                            Quotes quotes = new Quotes(responseStr,UNKOWN_AUTHOR,parameters);
                            message.obj = quotes;

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
                            Quotes quotes = new Quotes(responseStr,UNKOWN_AUTHOR,parameters);
                            message.obj = quotes;
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
                            Quotes quotes = new Quotes(responseStr,author,parameters);
                            message.obj = quotes;
                            handler.sendMessage(message);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });



        }
    }

    private void getDeepQuote(final JobParameters parameters){
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
                    Quotes quotes = new Quotes(data,UNKOWN_AUTHOR,parameters);
                    message.obj = quotes;
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