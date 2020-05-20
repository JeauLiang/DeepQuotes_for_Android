package com.deepquotes;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.util.Random;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Quotes {

    //https://www.nihaowua.com/home.html



    public void getHitokotoQuotes( Callback callback){

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url("https://v1.hitokoto.cn/").build();
        client.newCall(request).enqueue(callback);

    }

    public void getDeepQuotes(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String data=null;
                try {
                    Document document = Jsoup.connect("http://www.nows.fun/").get();
                    Elements element = document.select("div.main-wrapper");
                    for (int i=0;i<element.size();i++) {
                        data = element.get(i).select("span").text();
                        Log.d("API", data);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        //return data;

    }

    public void getDeepQuotes2(Callback callback){
        //https://www.apicp.cn/API/yan/api.php

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url("https://www.apicp.cn/API/yan/api.php").build();
        client.newCall(request).enqueue(callback);

    }

    public void getDeepQuotes3(){
        //https://www.nihaowua.com/home.html
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Document document = Jsoup.connect("https://www.nihaowua.com/home.html").get();
                    Elements element = document.select("div.post55");
                    for (int i=0;i<element.size();i++){
                        String data = element.get(i).select("font").text();
                        Log.d("API",data);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }
}
