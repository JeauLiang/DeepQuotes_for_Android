package com.deepquotes;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Quotes {


    public String getHitokotoQuotes(URL quotesApi){
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(quotesApi).build();
            Response response = client.newCall(request).execute();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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
}
