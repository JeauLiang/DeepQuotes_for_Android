package com.deepquotes;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Quotes {

    //https://www.nihaowua.com/home.html


    public void getHitokotoQuotes(final Context context){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder().url("https://v1.hitokoto.cn/").build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    Toast.makeText(context,responseData,Toast.LENGTH_SHORT).show();
                    Log.d("API",responseData);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public String getDeepQuotes(){
        String data=null;
        try {
            Document document = Jsoup.connect("http://www.nows.fun/").get();
            Elements element = document.select("div.main-wrapper");
            for (int i=0;i<element.size();i++) {
                data = element.get(i).select("span").text();
                Log.d("API", data);
            }
            return data;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getDeepQuotes2(){
        //https://www.apicp.cn/API/yan/api.php
        String responseData=null;
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url("https://www.apicp.cn/API/yan/api.php").build();
            Response response = client.newCall(request).execute();
            responseData = response.body().string();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return responseData;
    }

    public String getDeepQuotes3(){
        //https://www.nihaowua.com/home.html
        String responseData=null;
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url("https://www.nihaowua.com/home.html").build();
            Response response = client.newCall(request).execute();
            responseData = response.body().string();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return responseData;
    }
}
