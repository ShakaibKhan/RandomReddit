package com.shakaibkhan.randomreddit;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.widget.Toast;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import org.json.*;

import java.util.HashMap;


/**
 * Created by shakaibkhan on 2017-01-03.
 */

public class RedditParser extends AsyncTask<String, String, String> {
    String subreddit;
    final private String REDDIT_BASE_URL = "https://www.reddit.com/r/";
    String redditURL ;
    String after = null;
    int position = 0;
    int final_position = 0;
    OkHttpClient webClient = new OkHttpClient();
    String[] redditLinks = new String[25];
    String[] redditTitles = new String[25];
    String redditResponse;
    Handler handler = null;

    RedditParser(String sr, Handler handle){
        this.subreddit = sr;
        this.handler = handle;
    }
    RedditParser(String sr, String after){
        this.subreddit = sr;
        this.after = after;
    }
    RedditParser(String sr){
        this.subreddit = sr;
    }

    RedditParser(String sr, Handler handle, String af){
        subreddit = sr;
        this.after = af;
        this.handler = handle;
    }

    private void buildUrl(){
        this.redditURL = REDDIT_BASE_URL +this.subreddit+".json?limit=25";
        if(this.after != null ){
            this.redditURL += "&after="+this.after;
        }
    }


    public String getImageLink(){
        // End the thread and call a new one with after
        if(position>=final_position){
            //this.cancel(true);
        }
        position += 1;
        return redditLinks[position-1];
    }

    @Override
    protected String doInBackground(String... params){
        redditResponse = null;
        this.buildUrl();
        Request request = new Request.Builder().url(this.redditURL).build();
        try{
            Response response = webClient.newCall(request).execute();
            redditResponse = response.body().string();
        }catch(Exception e){e.printStackTrace();}
        return  redditResponse;
        }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String result){
        super.onPostExecute(result);
        JSONObject redditJson = null;
        JSONArray redditJsonArray = null;
                try{
            redditJson = new JSONObject(redditResponse);
            redditJson = redditJson.getJSONObject("data");
            this.after = redditJson.getString("after");
            redditJsonArray = redditJson.getJSONArray("children");


            for(int i = 2; i< redditJsonArray.length();i++){
                redditJson = redditJsonArray.getJSONObject(i);
                redditJson = redditJson.getJSONObject("data");
                if(checkUrl(redditJson.getString("url"))){
                    if(redditJson.getString("url").contains(".gifv")){
                        redditLinks[final_position] = fixGifUrl(redditJson.getString("url").replace(";","&").replace("&amp&","&"));
                    }else{
                        redditLinks[final_position] = redditJson.getString("url").replace(";","&").replace("&amp&","&");
                    }

                    redditTitles[final_position] = redditJson.getString("title").replace(";","&").replace("&amp&","&");
                    final_position++;
                }
            }

        }catch(JSONException je){
            je.printStackTrace();
        }
        if(this.handler != null){
            this.handler.sendEmptyMessage(0);
        }

    }

    private boolean checkUrl(String url){
        if(url.contains(".gif") ||url.contains(".gifv") || url.contains(".png") || url.contains(".jpg")
                || url.contains(".mp4") || url.contains(".jpeg") || url.contains("i.reddituploads.com")){
            return true;
        }
        return false;
    }

    private String fixGifUrl(String gifUrl){
        return gifUrl.replace(".gifv",".gif");
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
    }

}